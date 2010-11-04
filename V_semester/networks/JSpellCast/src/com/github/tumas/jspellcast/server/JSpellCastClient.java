package com.github.tumas.jspellcast.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import com.github.tumas.jspellcast.proto.IcyProtocol;

public class JSpellCastClient {
	private Socket socket;

	private OutputStream output;
	private InputStream input;
	
	private String mountPoint;
	private String userAgent;
	private int toMeta = 0;
	
	JSpellCastClient(Socket socket) throws IOException{
		this.socket = socket;
	
		output = socket.getOutputStream();
		input = socket.getInputStream();
	}

	/**
	 * Encode given metadata parameters and send to the client
	 * 
	 * @param title Stream title
	 * @param url Stream url
	 * @throws IOException
	 */
	public void sendMeta(String title, String url) throws IOException{
		output.write(IcyProtocol.encodeAsMeta(title, url).getBytes());
	}
	
	public OutputStream getOutput(){
		return output;
	}
	
	public void send(byte b[]) throws IOException{
		output.write(b);
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setMountPoint(String mountPoint) {
		this.mountPoint = mountPoint;
	}

	public String getMountPoint() {
		return mountPoint;
	}

	public void disconnect() throws IOException {
		socket.close();
	}

	public void handshake() throws IOException {
		String response = IcyProtocol.getHeader(input);
		
		setMountPoint(IcyProtocol.getHeaderMountPoint(response));
		HashMap<String, String> protoObject = IcyProtocol.parseHeader(response);
		setUserAgent(protoObject.get("user-agent"));
	}
		
	@Override
	public String toString(){
		return "\n\t User-Agent: " + getUserAgent() + "\n\t Mountpoint: " + getMountPoint();   
	}

	public void setToMeta(int toMeta) {
		this.toMeta = toMeta;
	}

	public int getToMeta() {
		return toMeta;
	}
}