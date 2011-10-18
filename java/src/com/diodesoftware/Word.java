package com.diodesoftware;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class Word {
	private static Logger log = Logger.getLogger(Word.class);
	public static Properties props = null;
	public static void load(String fileName, ServletContext context){
		try{
			InputStream in = context.getResourceAsStream(fileName);
			props = new Properties();
			props.load(in);
			in.close();
		}catch(IOException e){
			log.error("Error loading words", e);
		}
	}
	public static String w(String key){		
		String r =	(String)props.get(key);
		if(r == null)r = key;
		return r;
	}
}
