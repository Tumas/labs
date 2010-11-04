package com.github.tumas.jspellcast.server;

import static com.github.tumas.jspellcast.proto.IcyProtocol.ICYSRV2SRCOK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.tumas.jspellcast.proto.IcyProtocol;
import static com.github.tumas.jspellcast.server.JSpellCastServerGlobals.*;

public class JSpellCastSource implements Runnable {
	private JSpellCastServer srv;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	private int br;
	private String name;
	private String mountPoint;
	private ArrayList<JSpellCastClient> clients = new ArrayList<JSpellCastClient>();
	
	JSpellCastSource(Socket socket, JSpellCastServer srv) throws IOException{
		this.socket = socket;
		this.srv = srv;
	
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	/**
	 * 1. Send OK message to source (authorization step is skipped)
	 * 2. Get information header from source and parse it
	 * 3. Get audio stream from source and distribute it to clients
	 */
	@Override
	public void run() {
	
		try {
			output.write(ICYSRV2SRCOK.getBytes());
			getAndParseHeader();
	
			System.out.println("SOURCE connected: " + toString());
			srv.printServerStats();

			int bytesRead;
			byte b[] = new byte[BUFSIZE];
			ArrayList<JSpellCastClient> clientsToDisconnect = new ArrayList<JSpellCastClient>();
			
			while ((bytesRead = input.read(b)) != -1){
				for (JSpellCastClient client : getClients()){
						try {
							if (client.getToMeta() + bytesRead >= SERVERMETAINT){
								client.getOutput().write(b, 0, SERVERMETAINT - client.getToMeta());
								client.sendMeta("Stub buddies - My awesome stub song", "");
								client.getOutput().write(b, SERVERMETAINT - client.getToMeta(), 
										(client.getToMeta() + bytesRead) % SERVERMETAINT); 
								
								client.setToMeta((client.getToMeta() + bytesRead) % SERVERMETAINT); 
							}
							else { 
								client.getOutput().write(b, 0, bytesRead);
								client.setToMeta(client.getToMeta() + bytesRead);
							}
						} catch (SocketException e){
							// client closed connection 
							System.out.println(e.getMessage());
							System.out.println("GOT SOCKET EXCEPTION");
							clientsToDisconnect.add(client);
						}
					}
				
				// disconnect closed clients
				for (JSpellCastClient client : clientsToDisconnect){
					disconnectClient(client, false);
				}
				clientsToDisconnect.clear();
			}

			disconnect();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public String toString(){
		return "SOURCE : \n" +
				"\t User-agent: " + getName() + "\n" +
				"\t Bitrate: " + getBitrate() + "\n" + 
				"\t Mountpoint: " + getMountPoint();
	}
	
	/**
	 *  VLC sends information header and audio data in separately
	 * @throws IOException
	 */
	private void getAndParseHeader() throws IOException{
		String infoHeader = IcyProtocol.getHeader(input);
		HashMap<String, String> headMap = IcyProtocol.parseHeader(infoHeader);

		setMountPoint(IcyProtocol.getHeaderMountPoint(infoHeader));
		setName(headMap.get("user-agent"));

		// parse bitrate
		String bitrateLine = headMap.get("ice-audio-info");
		if (bitrateLine != null){
			String[] options = bitrateLine.split("=|;|\\s");

			for (int i = 1; options.length > 1 && i < options.length; i++)
				if (options[i - 1].toLowerCase().equals("bitrate")){
					setBitRate(Integer.parseInt(options[i]));
					break;
				}
		}
	}
	
	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getBitrate() {
		return br;
	}

	public void setBitRate(int bitRate) {
		int[] validBitrates = { 28, 64, 96, 128, 192, 320 };
		
		for (int i : validBitrates){
			if (i == bitRate){
				br = bitRate;
				return ;
			}
		}
	}

	/** Add given client to clients list 
	 * 
	 * @param JSpellCastClient client to register
	 * @return boolean flag that determines if operation was successful
	 */
	public boolean registerClient(JSpellCastClient client) {
		if (!getClients().contains(client)){
			System.out.println("CLIENT " + client.getUserAgent() + " associated with source: " + getName() + 
					" ( " + getMountPoint() + " )");
			
			return getClients().add(client);
		}	

		return false;
	}

	/**
	 *  disconnect Client 
	 *  
	 * @param JSpellCastClient client Client to disconnect 
	 * @param boolean inClientsIteration flag indicates whether this method was called when iterating over clients list. 
	 *  If it is, then clients list is not modified and the caller is responsible for deleting elements from it. (in short, 
	 *   attempting to modify collection when iterating over it leads to unspecified behavior)
	 */
	public void disconnectClient(JSpellCastClient client, boolean inClientsIteration){
		System.out.println("disconnecting client" + client.getUserAgent() + " on " + client.getMountPoint());
		
		srv.updateClientsCount(-1);
		srv.printServerStats();
		if (!inClientsIteration)
			getClients().remove(client);
		
		try {
			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** disconnect source
	 *  First, disconnect all sources clients and only then the source itself
	 */
	public void disconnect() {
		System.out.println("disconnecting source clients");
		
		for (JSpellCastClient client : getClients()){
			disconnectClient(client, true);
			System.out.println("disconnected client");
		}
		getClients().clear();
		
		System.out.println("disconnecting source " + getName() + " on " + getMountPoint());
		srv.updateSourcesCount(-1);
		srv.printServerStats();
		srv.removeSource(this);
		
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
	}

	public ArrayList<JSpellCastClient> getClients() {
		return clients;
	}
}