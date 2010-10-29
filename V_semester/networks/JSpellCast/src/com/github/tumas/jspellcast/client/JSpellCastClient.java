package com.github.tumas.jspellcast.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import com.github.tumas.jspellcast.proto.IcyProtocol;

public class JSpellCastClient {
	private final int BUFSIZE = 512;
	private final int SECONDSTOBUFFER = 5;
	private final int QUEUECAPACITYINSECONDS = 5;
	
	private String host;
	private String mountPoint;
	private int port;
	
	private int metaInt = -1;
	private int bitRate = 320;
	
	private Socket socket;
	private Logger logger;
	
	private InputStream input;
	private OutputStream output;
	
	// 40000 = 1000 * 320 kbps / 8 
	private ArrayBlockingQueue<Byte> buffer = new ArrayBlockingQueue<Byte>(40000 * QUEUECAPACITYINSECONDS);
	
	public JSpellCastClient(String host, int port, String mountPoint) {
		setHost(host);
		setPort(port);
		setMountPoint(mountPoint);
	
		logger = Logger.getLogger("info");
	}

	public void connect() throws UnknownHostException, IOException {
		String message = String.format(IcyProtocol.clientToServerMessage, getMountPoint(),
				"HTTP/1.0", getHost(), getPort(), 0);
			
		socket = new Socket(getHost(), getPort());
		logger.info("Socket associated with " + getHost() + ":" + getPort() + " created");
		
		output = socket.getOutputStream();
		output.write(message.getBytes());
		logger.info("Sent information to server");
	}
	
	public String getResponse() throws IOException {
		input = socket.getInputStream();
		String response = "";
		
		boolean endPresented = false;
		byte b[] = new byte[BUFSIZE];
		
		while (!endPresented && (input.read(b) != -1)){
			String temp = new String(b);
			
			int index;
			if ((index = temp.indexOf(IcyProtocol.HeaderEndToken)) != -1){
				endPresented = true;
				response += temp.substring(0, index + IcyProtocol.HeaderEndToken.length());
		
				for (int i = index + IcyProtocol.HeaderEndToken.length() + 1; i < temp.length(); i++)
					buffer.add((Byte) b[i]);
			}
			else 
				response += temp;
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
		
	public void playStream() throws IOException, InterruptedException{
		System.out.println("Connection info: \n\tBR: " + getBitRate() + " kbps\n\tMetaInt: " + getMetaInt());

		class DownloadThread implements Runnable {
			
			@Override
			public void run() {
				byte[] localBuffer = new byte[BUFSIZE];
				int bytesRead;
				
				
				try {
					while ((bytesRead = input.read(localBuffer)) != -1){

						for (int i = 0; i < bytesRead; i++){
							while (!buffer.offer((Byte) localBuffer[i])) { 
								Thread.sleep(100);
							}
						}

						//System.out.println(bytesRead);
						//System.out.println("Buffer size: " + buffer.size());
					}

				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e){
					System.out.println(e.getMessage());
				}
			}
		}

		class PlayBackThread implements Runnable {
			PipedInputStream getIn = new PipedInputStream(); 
			PipedOutputStream readOut;
			
			@Override
			public void run() {

				// data copying thread
				new Thread(){
					public void run(){
						try {
							readOut = new PipedOutputStream(getIn);
							
							while (true){
								readOut.write(buffer.poll());
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					} 
				}.start();

				// actual playback
				try {
					new Player(getIn).play();
				} catch (JavaLayerException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		// do some buffering 
		ExecutorService executor = Executors.newFixedThreadPool(2);

		executor.execute(new DownloadThread());
		
		int toBuffer = getBitRate() * SECONDSTOBUFFER * 1000 / 8;
		while (buffer.size() < toBuffer) { Thread.sleep(200); }

		System.out.println("Buffered " + toBuffer + " bytes - (" + SECONDSTOBUFFER + " seconds), starting playback ");
		executor.execute(new PlayBackThread());

		executor.shutdown();
	}
	
	public void closeConnection() throws IOException {
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
	}

	public int getBitRate() {
		return bitRate;
	}	
}