package com.github.tumas.jspellcast.server;

import static com.github.tumas.jspellcast.proto.IcyProtocol.HEADERENDTOKEN;
import static com.github.tumas.jspellcast.proto.IcyProtocol.ICYSRV2SRCOK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import com.github.tumas.jspellcast.proto.IcyProtocol;

public class JSpellCastSource implements Runnable {
	private final int BUFSIZE = 8192;
	
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	
	private int br;
	private String name;
	private String mountPoint;
	
	JSpellCastSource(Socket socket) throws IOException{
		this.socket = socket;
	
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

			while (true){
				// forward mp3 data to clients
					// read data from server
					// thread-per client 
			}
			
			
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
		String infoHeader = getHeader();
		HashMap<String, String> headMap = IcyProtocol.parseHeader(infoHeader);

		setMountPoint(IcyProtocol.getHeaderMountPoint(infoHeader));
		setName(headMap.get("user-agent"));

		// parse bitrate
		String bitrateLine = headMap.get("ice-audio-info");
		if (bitrateLine != null){
			String[] options = bitrateLine.split("=|;|\\s");

			for (int i = 1; options.length > 1 && i < options.length; i++)
				if (options[i - 1].toLowerCase().equals("bitrate"))
					setBitRate(Integer.parseInt(options[i]));
		}
	}
	
	private String getHeader() throws IOException{
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

	private int getBitrate() {
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
}