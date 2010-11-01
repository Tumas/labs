package com.github.tumas.jspellcast.server;


public class Main {
	public static void main(String[] args){
		JSpellCastServer server;
		int port1, port2;
		
		try {
			
			if (args.length == 0)
				server = new JSpellCastServer();
			else if (args.length == 1){
				port1 = Integer.parseInt(args[0]);
				server = new JSpellCastServer(port1, port1 + 1);
			}
			else if (args.length == 2){
				port1 = Integer.parseInt(args[0]);
				port2 = Integer.parseInt(args[1]);
				
				server = new JSpellCastServer(port1, port2);
			}
			else 
				throw new Exception("USAGE: java server [source_port [, client_port]];");
			

			System.out.println("Server launched: \n\t Source port: " + server.getSourcePort() 
					+ "\n\t Client port: " + server.getClientPort());
			
			server.run();
		} catch (Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}

/*
 * TODO:
 *  fun part!
 */
