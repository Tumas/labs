package com.github.tumas.jspellcast.proto;

import static com.github.tumas.jspellcast.server.JSpellCastServerGlobals.BUFSIZE;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

public class IcyProtocol {
	
	public static final String HEADERLINETOKEN = "\r\n";
	public static final String HEADERENDTOKEN = HEADERLINETOKEN + HEADERLINETOKEN;
	
	public static final String CLIENT2SRVMESSAGE = "GET /%s %s\r\n" + "Host: %s:%s\r\n" + 
		"User-Agent: JSpellCast\r\n" + "Icy-MetaData: %d\r\n" + "Connection: close\r\n\r\n";

	public static final String ICYSRV2SRCOK = "ICY 200 OK" + HEADERENDTOKEN;
	public static final String ICYSRVTOCLMSG = "ICY 200 OK" + HEADERLINETOKEN +
		"icy-notice:%s" + HEADERLINETOKEN + 
		"icy-name:%s" + HEADERLINETOKEN +
		"Content-Type:audio/mpeg" + HEADERLINETOKEN + 
		"icy-br:%s" + HEADERLINETOKEN + 
		"icy-pub:1" + HEADERLINETOKEN + 
		"icy-metaint:%d" + HEADERENDTOKEN;
	
	public static final String METADATAFORMAT = "%cStreamTitle='%s';StreamUrl='%s'";
	
	/**
	 * @param title String track title 
	 * @param url String stream url
	 * @return
	 */
	public static String encodeAsMeta(String title, String url){
		title = title == null ? "" : title;
		url = url == null ? "" : url;
		
		int length = title.length() + url.length() + METADATAFORMAT.length() - 5;
		int padding = 16 - length % 16;
		String meta = String.format(METADATAFORMAT, (length + padding) / 16 , title, url);
		
		for (int i = 0; i <= padding; i++)
			meta += (char) 0;
		
		return meta;
	}
	
	public static String getHeader(InputStream input) throws IOException{
		boolean endPresented = false;
		byte b[] = new byte[BUFSIZE];
		String response = "";
		
		while (!endPresented && (input.read(b) != -1)){
			String temp = new String(b);

			if (temp.indexOf(HEADERENDTOKEN) != -1)
				endPresented = true;

			response += temp;
		}
		
		return response;
	}

	
	/* Naive method definitions ... */

	public static HashMap<String, String> parseHeader(String header){
		HashMap<String, String> protoObject = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(header, HEADERLINETOKEN);
		String line;

		while (st.hasMoreTokens()){
			line = st.nextToken();
			
			String[] strs = line.split(":", 2);
			if (strs.length >= 2) {
				protoObject.put(strs[0].toLowerCase(), strs[1]);
			}
		}
	
		return protoObject;
	}
	
	// TODO: rewrite this into a cryptic regExp one liner.
	public static String getHeaderMountPoint(String header){
		String[] lines = header.split(HEADERLINETOKEN);
		
		if (lines.length <= 0) 
			return null;
		
		String[] lineTokens = lines[0].split(" ");
		
		if (lineTokens.length < 2)
			return null;
		
		return lineTokens[1].substring(1);
	}
}
