/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Sep 27, 2003
 * Time: 9:41:24 AM
 * To change this template use Options | File Templates.
 */
package com.diodesoftware.scb.clipboard;

import com.diodesoftware.scb.SitePage.Page;
import com.diodesoftware.scb.tables.*;
import com.diodesoftware.scb.*;
import com.diodesoftware.scb.agents.*;
import com.diodesoftware.dbmapper.PasswordEncrypter;


import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

public class DisplayUrlLogic {

    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String DONT_DISPLAY_PASSWORD = "dontDisplayPassword";
    public static final String PAGE_SAVED = "pageSaved";


    private ClipAgent clipAgent;
    private Logger log = Logger.getLogger(DisplayUrlLogic.class);

    public DisplayUrlLogic() {
        clipAgent = ClipAgent.getInstance();
    }

    public Page getDisplayPage(ClipRequest request, Clip clip) {

        String extention = request.getExtention();
        Page page = null;
        if (clip.isHtml()) {
            if (log.isDebugEnabled())
                log.debug("Clip is HTML");
            if (hasPassword(clip)) {
                if (log.isDebugEnabled())
                    log.debug("Clip Has Password");
                //View Html with edit button and password
                page = new Page(request.getSession().getServletContext().getRealPath("textReadOnly" + extention));
                if (request.isWap())
                    page.setJsp("/cl1p-inc-rgdm/readOnly_wap.jsp");
                else
                    page.setJsp("/cl1p-inc-rgdm/readOnly.jsp");

            } else {
                if (log.isDebugEnabled())
                    log.debug("Clip does not have Password");
                //View Html. With edit button (No Password)
                page = new Page(request.getSession().getServletContext().getRealPath("htmlReadOnlyNP" + extention));
                
                if (request.isWap())
                    page.setJsp("/cl1p-inc-rgdm/readOnly_wap.jsp");
                else
                    page.setJsp("/cl1p-inc-rgdm/readOnly.jsp");
            }
        } else {
            if (log.isDebugEnabled())
                log.debug("Clip is Text");
            if (hasPassword(clip)) {
                // if (request.getAttribute(PAGE_SAVED) != null) {
                // To display google ads I need to swtich to HTML view right away!
                //   page = new Page(request.getSession().getServletContext().getRealPath("textReadOnly"+extention));
                //} else {
                if (log.isDebugEnabled())
                    log.debug("Clip Has password");
                if (clip.isViewPassword()) {
                    if (log.isDebugEnabled())
                        log.debug("View Password");
                    boolean validPassword = passwordValid(request, clip);
                    if (log.isDebugEnabled())
                        log.debug("Password valid [" + validPassword + "]");
                    if (validPassword || request.isOwner()) {
                        if (log.isDebugEnabled())
                            log.debug("View Password is valid");
                        page = new Page(request.getSession().getServletContext().getRealPath("text" + extention));
                        if (request.isWap())
                            page.setJsp("/cl1p-inc-rgdm/edit_wap.jsp");
                        else
                            page.setJsp("/cl1p-inc-rgdm/edit.jsp");


                        page.setEditable(true);
                    } else {
                        log.debug("View Password is invalid");
                        page = new Page(request.getSession().getServletContext().getRealPath("enterPassword" + extention));
                        page.setViewRestricted(true);
                        if (request.isWap())
                            page.setJsp("/cl1p-inc-rgdm/viewRestricted_wap.jsp");
                        else
                            page.setJsp("/cl1p-inc-rgdm/viewRestricted.jsp");

                    }
                } else {
                    // Read only edit page. Requiring Password
                    if (passwordValid(request, clip) || request.isOwner()) {
                        page = new Page(request.getSession().getServletContext().getRealPath("text" + extention));
                        if (request.isWap())
                            page.setJsp("/cl1p-inc-rgdm/edit_wap.jsp");
                        else
                            page.setJsp("/cl1p-inc-rgdm/edit.jsp");
                        page.setEditable(true);
                    } else {
                        page = new Page(request.getSession().getServletContext().getRealPath("textReadOnly" + extention));
                        if (request.isWap())
                            page.setJsp("/cl1p-inc-rgdm/readOnly_wap.jsp");
                        else
                            page.setJsp("/cl1p-inc-rgdm/readOnly.jsp");
                    }
                }
                //}
            } else {
                if (log.isDebugEnabled())
                    log.debug("Clip Does not have password");
                //Normail Edit page.
                page = new Page(request.getSession().getServletContext().getRealPath("text" + extention));
                if (request.isWap())
                    page.setJsp("/cl1p-inc-rgdm/edit_wap.jsp");
                else
                    page.setJsp("/cl1p-inc-rgdm/edit.jsp");
                page.setEditable(true);
            }
        }
        return page;
    }

    public Page getEditPage(ClipRequest request, Clip clip) {
        Page page = null;
        String extention = request.getExtention();
        if (hasContent(request)) {
            if (log.isDebugEnabled())
                log.debug("Trying to save new content");
            if (passwordValid(request, clip)) {
                if (log.isDebugEnabled())
                    log.debug("Password is vaild");

                if (log.isDebugEnabled())
                    log.debug("Saving text");
                clip.setHtml(false);
                saveTODb(request, clip);
                request.setAttribute(ERROR_MESSAGE, "Page saved");

            } else {
                request.setAttribute(ERROR_MESSAGE, "Password is invalid");
            }
        } else {
            if (passwordValid(request, clip)) {
                request.setAttribute("showAd", new Boolean(true));
                // Return HTML Edit Page. With HTML content in Text Area
                if (hasPassword(clip)) {
                    page = new Page(request.getSession().getServletContext().getRealPath("text" + extention));
                    if (request.isWap())
                        page.setJsp("/cl1p-inc-rgdm/edit_wap.jsp");
                    else
                        page.setJsp("/cl1p-inc-rgdm/edit.jsp");
                    page.setEditable(true);
                } else {
                    page = new Page(request.getSession().getServletContext().getRealPath("text" + extention));
                    if (request.isWap())
                        page.setJsp("/cl1p-inc-rgdm/edit_wap.jsp");
                    else
                        page.setJsp("/cl1p-inc-rgdm/edit.jsp");
                    page.setEditable(true);
                }
                return page;
            } else {
                request.setAttribute(ERROR_MESSAGE, "Password is invalid");
            }
        }
        request.setAttribute("showAd", new Boolean(true));
        return getDisplayPage(request, clip);
    }

    private boolean hasContent(ClipRequest request) {
        String content = request.getParameter(HtmlParam.CONTENT);
        return content != null;
    }

    private boolean hasPassword(Clip clip) {
        String password = clip.getPassword();
        boolean b = (password != null && password.trim().length() > 0);
        if (log.isDebugEnabled())
            log.debug("Has Password [" + b + "] Value [" + password + "]");
        return b;
    }

    private boolean passwordValid(ClipRequest request, Clip clip) {
        if (request.isOwner()) return true; // Owners don't have to supply paswords
        if (log.isDebugEnabled())
            log.debug("Checking to see if password is valid.");
        PasswordMgr pwdMgr = PasswordMgr.getInstance(request);
        if (pwdMgr.hasKey(clip.getUri())) {
            if (log.isDebugEnabled()) log.debug("Key stored in manager");
            return true;
        }
        if (hasPassword(clip)) {
            String password = (String) request.getParameter(HtmlParam.PASSWORD);
            if (password != null && password.length() > 0) {
                password = PasswordEncrypter.encrypt(password);
                String clipPassword = clip.getPassword();
                boolean b = clipPassword.equals(password);
                if (b) {
                    log.debug("Password is valid.");

                    pwdMgr.addKey(clip.getUri());
                } else
                    log.debug("Password invalid. [" + password + "] != [" + clipPassword + "]");

                return b;
            }
            if (log.isDebugEnabled())
                log.debug("Password not provided.");
            return false;
        }
        if (log.isDebugEnabled())
            log.debug("No password required. Valid");
        return true;
    }


    private void saveTODb(ClipRequest request, Clip clip) {
        /*String content = request.getParameter(HtmlParam.CONTENT);
        if (content != null)
            clip.setValue(content);
        if(clip.getClipType() != Clip.CLIP_TYPE_PLAIN_TEXT){
        	if(clip.getClipType() == Clip.CLIP_TYPE_RICH_TEXT){
        		RichText richText = (RichText)DBMapper.getInstance().load(RichText.class, clip.getClipTypeId(), request.getCon());
        		if(content != null){                 
        			richText.setValue(content);
        			DBMapper.getInstance().save(richText, request.getCon());
        		}
        		
        	}
        }
        String password = request.getParameter(HtmlParam.PASSWORD);
        String passwordVerify = request.getParameter(HtmlParam.PASSWORD_VERIFY);
        String viewPasswords = request.getParameter(HtmlParam.VIEW_PASSWORD);
        String keepForString = request.getParameter(HtmlParam.KEEP_FOR);
        String title = request.getParameter(HtmlParam.TITLE);
        String rowsStr = request.getParameter(HtmlParam.ROWS);
        if(rowsStr != null){
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
                request.setAttribute(ERROR_MESSAGE, "Passwords Don't match");
                request.setAttribute(DONT_DISPLAY_PASSWORD, new Boolean(true));
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

        clipAgent.saveClip(clip, request.getCon());
        ClipSession cSession = ClipSession.getSession(request.getRequest());
        User user = cSession.getUser();
        if (user != null) {
            UserAgent.getInstance().addUserClip(clip.getNumber(), user.getNumber(), request.getCon());
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
        request.setAttribute(PAGE_SAVED, new Boolean(true));
        // Extrass
        saveLinks(clip, request);
        removeLink(clip, request);

        String deleteFile = request.getParameter("deleteFile");
        if ("yes".equals(deleteFile)) {
            int fileNumber = 0;
            try{
                fileNumber = Integer.parseInt(request.getParameter("deleteFileNumber"));
            }catch(NullPointerException e){}
            CleanerAgent.deleteFile(clip, fileNumber, request.getRequest().getSession()
					.getServletContext());
        }
        //Replace password in map, it may have changed to encrypted value
                */
    }

    private void saveLinks(Clip clip, ClipRequest request) {
        String link = request.getParameter(HtmlParam.ADD_LINK);
        if (link != null && link.trim().length() > 0) {
            // Get the ID based on the URL
            link = getLinkURI(link, request);
            if (link == null) return;
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

    private void removeLink(Clip clip, ClipRequest request) {
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

    private String getLinkURI(String link, ClipRequest request) {
        if (link.startsWith("http://")) {
            if (link.indexOf("cl1p.net") != -1) {
                // Find the key for this URL
                try {
                    URL u = new URL(link);
                    link = u.getPath();
                } catch (MalformedURLException e) {
                    request.setAttribute(ERROR_MESSAGE, "URL is not valid");
                    return null;
                }
            } else {
                // Not allowed
                request.setAttribute(ERROR_MESSAGE, "Only other cl1ps can be added here.");
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
