package com.diodesoftware.scb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ClipMsg {

	private static Logger log = Logger.getLogger(ClipMsg.class);
	public static String msg = "";
	
	public static void loadMsgFile() {
		try {
			File file = new File("/home/rob/cl1p.msg");
			if(file.exists()){
			BufferedReader in = new BufferedReader(new FileReader(file
					));
			StringBuffer sb = new StringBuffer();
			while (in.ready()) {
				sb.append(in.readLine());
			}
			in.close();
			msg = sb.toString();
			}
			
		} catch (IOException e) {
			log.error("Error loading msg", e);
		}
	}
}
