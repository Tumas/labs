package com.github.tumas.jspellcast.client;

import java.io.IOException;

import com.github.tumas.jspellcast.proto.IcyProtocol;

public class Main {

	public static void main(String[] args) throws IOException{
		JSpellCastClient client = null;
		
		if (args.length < 2 || args.length > 3){
			System.err.println("Whoops, wrong argument format. Use the following: host port [mountpoint]");
			return ;
		}
	
		// strip http:// if presented
		String host = args[0];
		if (host.toLowerCase().startsWith("http://"))
			host = host.substring("http://".length(), host.length());
		
		try {			
			client = new JSpellCastClient(host, 
						Integer.parseInt(args[1]), 
						args.length == 2 ? "" : args[2]);

			client.connect();
			String header = client.getResponse();

			System.out.println(header);
			
			
			client.updateInfo(IcyProtocol.parseHeader(header));
			client.playStream();
		}
		catch (NumberFormatException e){
			System.err.println("Port should be a number (got " + args[1] + ")");
		} 
		catch (Exception e){
			System.err.print("Whoops: ");
			System.err.println(e);
		
			client.closeConnection();
		}
		
		/* Does not work -> we still have some thread working on that socket 
		finally {
			client.closeConnection();
		}
		*/

	}
}