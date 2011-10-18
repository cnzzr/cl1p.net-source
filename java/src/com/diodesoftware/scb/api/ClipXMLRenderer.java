package com.diodesoftware.scb.api;

import java.io.IOException;
import java.io.StringWriter;


import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.FlyweightCDATA;

import com.diodesoftware.scb.tables.Clip;

public class ClipXMLRenderer {

	public String writeXML(Clip clip) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("cl1p");
		root.setAttributeValue("title", clip.getTitle());
		root.setAttributeValue("password", clip.getPassword()!=null?"":"");
		root.setAttributeValue("readonly", clip.getViewPassword()?"":"");
		
		
		root.add(DocumentHelper.createCDATA(clip.getValue()));
		
		return document.asXML();
	}
}
