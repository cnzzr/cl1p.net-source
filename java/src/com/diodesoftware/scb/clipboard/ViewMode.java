package com.diodesoftware.scb.clipboard;

import org.apache.log4j.Logger;

import com.diodesoftware.dbmapper.PasswordEncrypter;
import com.diodesoftware.scb.ClipRequest;
import com.diodesoftware.scb.HtmlParam;
import com.diodesoftware.scb.PasswordMgr;
import com.diodesoftware.scb.tables.Clip;

public class ViewMode {
	
	public final static int EDIT = 1;
	public final static int READ_ONLY = 2;
	public final static int PASSWORD_REQUIRED = 3;
	private static Logger log = Logger.getLogger(ViewMode.class); 
	
	public static int getViewMode(ClipRequest request) {
		int result = EDIT;
		Clip clip = request.getClip();
		boolean debug = log.isDebugEnabled();
		if (hasPassword(clip)) {
			if (debug)
				log.debug("Clip Has password");
			if (clip.isViewPassword()) {
				if (debug)
					log.debug("View Password");
				boolean validPassword = passwordValid(request, clip);
				if (debug)
					log.debug("Password valid [" + validPassword + "]");
				if (validPassword || request.isOwner() || request.isRoot()) {
					if (debug)
						log.debug("View Password is valid");
					result = EDIT;
				} else {
					if(debug)
						log.debug("View Password is invalid");	
					if(passwordEntered(request))
						request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE, "Password is invalid");
										
					result = PASSWORD_REQUIRED;
				}
			} else {
				// Read only edit page. Requiring Password
				if (passwordValid(request, clip) || request.isOwner()) {
					result = EDIT;
				} else {
					if(passwordEntered(request))
						request.setAttribute(DisplayUrlLogic.ERROR_MESSAGE, "Password is invalid");
					result = READ_ONLY;
				}
			}
		} else {
			result = EDIT;
		}

		return result;
	}
	
  
    private static boolean hasPassword(Clip clip) {
        String password = clip.getPassword();
        boolean b = (password != null && password.trim().length() > 0);
        if (log.isDebugEnabled())
            log.debug("Has Password [" + b + "] Value [" + password + "]");
        return b;
    }
    
    private static boolean passwordEntered(ClipRequest request){
        String password = (String) request.getParameter(HtmlParam.PASSWORD);
      return (password != null && password.length() > 0);
    
    }

    private static boolean passwordValid(ClipRequest request, Clip clip) {
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

	

}
