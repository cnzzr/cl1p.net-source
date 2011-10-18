package com.diodesoftware.util;

import java.io.FileWriter;
import java.io.PrintWriter;

public class I18NDebug {
	
	private static PrintWriter pw = null;
	
	public static void log(Exception e)
	{
		try{
		if(pw == null){
			FileWriter fw = new FileWriter("/Users/rob/i18n.log");
			pw = new PrintWriter(fw);
		}
		e.printStackTrace(pw);
		}catch(Exception es){
			es.printStackTrace();
		}
	}
}
