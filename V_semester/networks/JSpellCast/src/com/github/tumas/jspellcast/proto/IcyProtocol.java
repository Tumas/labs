package com.github.tumas.jspellcast.proto;

import java.util.HashMap;
import java.util.StringTokenizer;

public class IcyProtocol {
	
	public static final String clientToServerMessage = "GET /%s %s\r\n" + "Host: %s:%s\r\n" + 
		"User-Agent: JSpellCast\r\n" + "Icy-MetaData: %d\r\n" + "Connection: close\r\n\r\n";
	
	public static final String HeaderLineToken = "\r\n";
	public static final String HeaderEndToken = HeaderLineToken + HeaderLineToken;
	
	public static HashMap<String, String> parseHeader(String header){
		HashMap<String, String> protoObject = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(header, HeaderLineToken);
		String line;

		while (st.hasMoreTokens()){
			line = st.nextToken();

			String[] strs = line.split(":");
			if (strs[0].indexOf("icy-") != -1 && strs.length >= 2) {
				protoObject.put(strs[0].toLowerCase(), strs[1]);
			}
		}
	
		return protoObject;
	}
}
