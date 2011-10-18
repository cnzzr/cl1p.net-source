package com.diodesoftware.scb;


import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.diodesoftware.Word;


public class ClipContextListener implements ServletContextListener {


	
	
	public static final String PROPERTY_NAME = "cl1pconfig";
	public static final String ENVIRONMENT_VARIABLE = "cl1pconfig";
	


	public static String configFile = "";
	public static int FOUND_IN_INIT_PARAMETER = 1;
	public static int FOUND_IN_JAVA_PROPERTY = 2;
	public static int FOUND_IN_ENVIRONMENT_VARIABLE = 3;
	public static int FOUND_IN = 0;
	
	
	public void contextDestroyed(ServletContextEvent event) {

	}

	public void contextInitialized(ServletContextEvent event) {		
		init(event.getServletContext());
	}
	
	public static void init(ServletContext context){
		try{
		org.apache.log4j.BasicConfigurator.configure();
        org.apache.log4j.Category.getRoot().setLevel(Level.WARN);

        Word.load("/WEB-INF/words.properties", context);
		configFile = context.getInitParameter(PROPERTY_NAME);
		if(configFile == null){
			System.err.println("CL1P: Config File Name not found in init parameter [" + PROPERTY_NAME + "]");
			Enumeration names = context.getInitParameterNames();
			while(names.hasMoreElements()){
				String name = (String)names.nextElement();
				System.err.println("CL1P: Has init parameter [" + name + "] Value [" + 
						context.getInitParameter(name) + "]");
				
				
			}
			configFile = System.getProperty(PROPERTY_NAME);
			if(configFile == null){
				System.err.println("CL1P: Config File Name not found in java parameter [" + PROPERTY_NAME + "]");
				configFile = System.getenv(ENVIRONMENT_VARIABLE);
				if(configFile != null){
					FOUND_IN = FOUND_IN_ENVIRONMENT_VARIABLE;
					System.err.println("CL1P: Config File [" + configFile + "] found in environment varibale [" + ENVIRONMENT_VARIABLE + "]");
				}else{
					System.err.println("CL1P: Config File Name not found in environment varibale [" + ENVIRONMENT_VARIABLE + "]");
				}
			}else{
				FOUND_IN = FOUND_IN_JAVA_PROPERTY;
				System.err.println("CL1P: Config File [" + configFile + "] found in java property [" + PROPERTY_NAME + "]");
			}			
		}else{
			FOUND_IN = FOUND_IN_INIT_PARAMETER;
			System.err.println("CL1P: Config File [" + configFile + "] found in init parameter [" + PROPERTY_NAME + "]");
		}}catch(Exception e){
			e.printStackTrace();
		}
	}
	

}
