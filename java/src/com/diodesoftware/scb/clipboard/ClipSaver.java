package com.diodesoftware.scb.clipboard;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;


import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.ClipSession;
import com.diodesoftware.scb.HtmlParam;
import com.diodesoftware.scb.PasswordMgr;
import com.diodesoftware.scb.agents.CleanerAgent;
import com.diodesoftware.scb.agents.ClipAgent;
import com.diodesoftware.scb.agents.UserAgent;
import com.diodesoftware.scb.tables.Clip;
import com.diodesoftware.scb.tables.ClipLink;
import com.diodesoftware.scb.tables.RichText;
import com.diodesoftware.scb.tables.User;

public class ClipSaver {

	private static Logger log = Logger.getLogger(ClipSaver.class);

	public static final int MAX_VALUE_SIZE = 70000;
	                                         // 1048909
	/* 
	 * com.mysql.jdbc.PacketTooBigException: Packet for query is too large (1048909 > 1048576). 
	 * You can change this value on the server by setting the max_allowed_packet' variable.
	 *                                         
	 */
	public static boolean isValueToBig(ClipRequest request)
	{
		String value = request.getParameter("ctrlcv");
		if(value != null && value.length() > MAX_VALUE_SIZE)
		{
			log.error("Value " + value.length() + " is greater than " + MAX_VALUE_SIZE);
			return true;
		}
		return false;
	}
	
	public static void saveTODb(ClipRequest request) {
		String viewModeS = request.getParameter("viewMode");
		String content = request.getParameter(HtmlParam.CONTENT);
        if(content != null && content.length() > MAX_VALUE_SIZE)
        {
        	log.error("Had to trim " + content.length() + " to " + MAX_VALUE_SIZE);
        	content = content.substring(0,MAX_VALUE_SIZE);
        }
        Clip clip = request.getClip();

        String append = request.getParameter("A");
		if (append == null) {
			//log.error("Not Appending");
			if (viewModeS == null)
				return;
			try {
				if (Integer.parseInt(viewModeS) != ViewMode.EDIT)
					return;
			} catch (Exception e) {
				return;
			}
			if (content != null)
				clip.setValue(content);
		} else {
            


            content = clip.getValue();
			if (content == null)
				content = "";
			content += append;
			if(request.getParameter("nl")!=null){
			    content =  content + "\n";
			}
			if(content != null && content.length() > MAX_VALUE_SIZE)
	        {
	        	log.error("Append: Had to trim " + content.length() + " to " + MAX_VALUE_SIZE);
	        	content = content.substring(0,MAX_VALUE_SIZE);
	        }
			clip.setValue(content);			
			log.debug("API: Appending New Value is [" + append + "] for cl1p [" + clip.getUri() + "]");
		}
		
		
		if(clip.getTitle() != null)
		{
			String title = clip.getTitle();
			if(title.length() > 100)
				title = title.substring(0,100);
			clip.setTitle(title);
		}
		
		
       
        if (clip.getClipType() != Clip.CLIP_TYPE_PLAIN_TEXT) {
			if (clip.getClipType() == Clip.CLIP_TYPE_RICH_TEXT) {
				RichText richText = (RichText) DBMapper.getInstance().load(
						RichText.class, clip.getClipTypeId(), request.getCon());
				if (content != null) {
					if(content.getBytes().length > MAX_VALUE_SIZE)
					{
						byte[] trim = new byte[MAX_VALUE_SIZE];
						System.arraycopy(content.getBytes(), 0, trim, 0, MAX_VALUE_SIZE);
						log.error("Triming COntent, too big! Current Size " + content.length());
						content = new String(trim);
						
					}
					richText.setValue(content);
					try{
						DBMapper.getInstance().save(richText, request.getCon());
					}catch(ClipSqlException e)
					{
						log.error("Error saving rich text. Content length:" + content.length(),e);
					}
				}

			}
		}
		String password = request.getParameter(HtmlParam.PASSWORD);
		String passwordVerify = request.getParameter(HtmlParam.PASSWORD_VERIFY);
		String viewPasswords = request.getParameter(HtmlParam.VIEW_PASSWORD);
		String keepForString = request.getParameter(HtmlParam.KEEP_FOR);
		String title = request.getParameter(HtmlParam.TITLE);
		String rowsStr = request.getParameter(HtmlParam.ROWS);
        String emailPassword = null;
        if(password!= null && password.length() < 20){
            emailPassword = password;
        }
        String email = request.getParameter("email");

        if (rowsStr != null) {
			int rows = Integer.parseInt(rowsStr);
			clip.setRows(rows);
		}
		String forceSSL = request.getParameter("forceSSL");
		if ("YES".equals(forceSSL))
			clip.setSecure(true);
		else
			clip.setSecure(false);
		if (title != null) {
			clip.setTitle(title);
		}
		int keepFor = 0;
		if (keepForString != null) {
			keepFor = Integer.parseInt(keepForString);
			clip.setKeepFor(keepFor);
			Calendar cal = Calendar.getInstance();
			if (keepFor == -1) {
				cal.set(Calendar.YEAR, 1900);
			} else {
				cal.add(Calendar.MINUTE, keepFor);
			}
			clip.setCleanDate(cal);
		}

        boolean viewPassword = viewPasswords != null;
		if (password != null && passwordVerify != null) {
			if (!passwordVerify.equals(password)) {
				request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE,
						"Passwords Don't match");
				request.setAttribute(DisplayUrlLogic.DONT_DISPLAY_PASSWORD,
						new Boolean(true));
				password = null;
				passwordVerify = null;
			} else {
				PasswordMgr pwnMgr = PasswordMgr.getInstance(request);
				pwnMgr.addKey(clip.getUri());
			}
		}

		if (password != null && password.trim().length() == 0)
			password = null;
		if (password != null && passwordVerify != null)
			clip.setPassword(password);
		// if (clip.getViewPassword() == false)
		clip.setViewPassword(viewPassword);
		clip.setLastEdit(Calendar.getInstance());
		ClipAgent clipAgent = ClipAgent.getInstance();
        clip.setEmail(email);
        clip.setEmailPassword(emailPassword);
        
        clipAgent.saveClip(clip, request.getCon());
		ClipSession cSession = ClipSession.getSession(request.getRequest());
		User user = cSession.getUser();
		if (user != null) {
			UserAgent.getInstance().addUserClip(clip.getNumber(),
					user.getNumber(), request.getCon());
		}
		String removePassword = request.getParameter("removePassword");
		if ("yes".equals(removePassword)) {
			clipAgent.removePassword(clip, request.getCon());
			PasswordMgr pwdMgr = PasswordMgr.getInstance(request);
			pwdMgr.removeKey(clip.getUri());
			clip.setPassword(null);
			clip.setViewPassword(false);
			request.setParameter(HtmlParam.PASSWORD, null);
			request.setParameter(HtmlParam.PASSWORD_VERIFY, null);
		}
		request.setAttribute(DisplayUrlLogic.PAGE_SAVED, new Boolean(true));
		// Extras
		saveLinks(clip, request);
		removeLink(clip, request);
		try{
		String deleteFile = request.getParameter("deleteFile");
		if ("yes".equals(deleteFile)) {

            int fileNumber = 0;
                fileNumber = Integer.parseInt(request.getParameter("deleteFileNumber"));
            
            
            CleanerAgent.deleteFile(clip, fileNumber, request.getCon());
		}
		}catch(Exception e )
		{
			log.error("Error deleting file ",e);
		}
		ClipType.load(request);// Just in case, load it
		request.getCl1pType().save(request);
		request.setClipType(null);
		ClipType.load(request);// Load again to relect recent changes
		request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE, "Page saved");
		// Replace password in map, it may have changed to encrypted value


    }

	private static void saveLinks(Clip clip, ClipRequest request) {
		String link = request.getParameter(HtmlParam.ADD_LINK);
		if (link != null && link.trim().length() > 0) {
			// Get the ID based on the URL
			link = getLinkURI(link, request);
			if (link == null)
				return;
			ClipAgent clipAgent = ClipAgent.getInstance();
			Clip target = clipAgent.loadClip(link, request.getCon());
			if (target == null) {

				// Build a target if it doesnt exist
				target = new Clip();
				target.setUri(link);
				clipAgent.dbMapper.save(target, request.getCon());
			}
			ClipLink cl = new ClipLink();
			cl.setClipId(clip.getNumber());
			cl.setToClipId(target.getNumber());
			cl.setUri(link);
			clipAgent.saveClipLink(cl, request.getCon());
		}
	}

	private static void removeLink(Clip clip, ClipRequest request) {
		String removeLink = request.getParameter(HtmlParam.REMOVE_LINK);
		if (removeLink != null && removeLink.trim().length() > 0) {
			int removeId = Integer.parseInt(removeLink);
			String sql = "Delete from ClipLink where Number = ? and (ClipId = ? or ToClipId = ?)";
			Connection con = request.getCon();
			try {
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setInt(1, removeId);
				prepStmt.setInt(2, clip.getNumber());
				prepStmt.setInt(3, clip.getNumber());
				prepStmt.executeUpdate();
				prepStmt.close();
			} catch (SQLException e) {
				log.error("Error running SQL [" + sql + "]", e);
			}
		}
	}

	private static String getLinkURI(String link, ClipRequest request) {
		if (link.startsWith("http://")) {
			if (link.indexOf("cl1p.net") != -1) {
				// Find the key for this URL
				try {
					URL u = new URL(link);
					link = u.getPath();
				} catch (MalformedURLException e) {
					request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE,
							"URL is not valid");
					return null;
				}
			} else {
				// Not allowed
				request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE,
						"Only other cl1ps can be added here.");
				return null;
			}
		}
		if (!link.startsWith("/")) {
			link = "/" + link;
		}
		if (!link.endsWith("/"))
			link += "/";
		return link;
	}

}
