package com.diodesoftware.scb;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DownloadTokenMgr {

	private static Map map = new HashMap();
	private static Random rnd = new Random();
	
	
	public static String genToken(int clipId){
		int token = putNewToken(clipId);
		return "" + token;
	}
	
	public static int getClipId(String token){
		int i = Integer.parseInt(token);
		TokenEntry e = (TokenEntry)map.get(i);
		if(e == null)return -1;
		return e.clipId;
	}
	
	private static int putNewToken(int clipId){
		int i = rnd.nextInt(500000000);
		if(map.containsKey(i))
			return putNewToken(clipId);
		TokenEntry e = new TokenEntry();
		e.time = System.currentTimeMillis();
		e.clipId = clipId;
		map.put(i, e);
		return i;
	}
	
	public static void cleanOldTokens(){
		long l = System.currentTimeMillis() - 20*60*1000;
		List toRemove = new ArrayList();
		Iterator iter = map.keySet().iterator();
		while(iter.hasNext()){
			int k = ((Integer)iter.next()).intValue();
			TokenEntry te = (TokenEntry)map.get(k);
			if(te.time < l)
				toRemove.add(k);
		}
		iter = toRemove.iterator();
		while(iter.hasNext()){
			map.remove(iter.next());
		}		
	}
	
	
	
	
}
