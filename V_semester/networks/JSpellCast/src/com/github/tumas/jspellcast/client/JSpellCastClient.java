package com.github.tumas.jspellcast.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.github.tumas.jspellcast.proto.IcyProtocol;

public class JSpellCastClient {
	private final int BUFSIZE = 512;
	
	private String host;
	private String mountPoint;
	private int port;
	
	private int metaInt = 8192;
	private int bitRate;
	
	private Socket socket;
	private Logger logger;

	public JSpellCastClient(String host, int port, String mountPoint) {
		setHost(host);
		setPort(port);
		setMountPoint(mountPoint);
	
		logger = Logger.getLogger("info");
	}

	public void connect() throws UnknownHostException, IOException {
		String message = String.format(IcyProtocol.clientToServerMessage, getMountPoint(),
				"http/1.0", getHost(), getPort(), 1);
			
		socket = new Socket(getHost(), getPort());
		logger.info("Socket associated with " + getHost() + ":" + getPort() + " created");
		
		OutputStream output = socket.getOutputStream();
		output.write(message.getBytes());
		logger.info("Sent information to server");
	}
	
	public String getResponse() throws IOException{
		InputStream input = socket.getInputStream();
		String response = "";
		
		boolean endPresented = false;
		byte b[] = new byte[BUFSIZE];
		
		while (!endPresented && (input.read(b) != -1)){
			String temp = new String(b);
			
			int index;
			if ((index = temp.indexOf(IcyProtocol.HeaderEndToken)) != -1){
				endPresented = true;
			}
			
			response += temp.substring(0, index + IcyProtocol.HeaderEndToken.length());
		}

		logger.info("Server responded");
		return response;
	}

	public void updateInfo(HashMap<String, String> map){
		setBitRate(processIntegerField(map, "icy-br"));
		setMetaInt(processIntegerField(map, "icy-metaint"));
	}
	
	private int processIntegerField(HashMap<String, String> map, String fieldName){
		if (map.containsKey(fieldName)){
			try {
				return Integer.parseInt(map.get(fieldName));
			} catch (NumberFormatException e){
				logger.warning("Could not get " + fieldName + " from header");
			}
		}
		else 
			logger.info("Skipping " + fieldName + " info");
		
		return -1;
	}
		
	public void playStream(){
		System.out.println("Connection info: \n\tBR: " + getBitRate() + " kbps\n\tMetaInt: " + getMetaInt());
	}
	
	public void closeConnection() throws IOException{
		if (socket != null)
			socket.close();
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void setMetaInt(int metaInt) {
		if (metaInt > 0)
			this.metaInt = metaInt;
	}

	public int getMetaInt() {
		return metaInt;
	}

	public void setBitRate(int bitRate) {
		int[] validBitrates = { 28, 64, 96, 128, 192, 320 };
		
		for (int i : validBitrates){
			if (i == bitRate){
				this.bitRate = bitRate;
				return ;
			}
		}

		logger.warning("Trying to set unsupported bitrate : " + bitRate);
	}

	public int getBitRate() {
		return bitRate;
	}	
}