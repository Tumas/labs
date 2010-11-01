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

import static com.github.tumas.jspellcast.proto.IcyProtocol.*;

public class JSpellCastClient {
	private final int BUFSIZE = 8192;
	private final int SECONDSTOBUFFER = 5;
	private final int QUEUECAPACITYINSECONDS = 5;
	
	private String host;
	private String mountPoint;
	private int port;
	
	private int metaInt = -1;
	private int bitRate = 320;
	private int musicBytes = 0; // to_meta
	
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

	/**
	 * 	Initiate connection and send the introductory message to the server
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException {
		String message = String.format(CLIENT2SRVMESSAGE, getMountPoint(),
				"HTTP/1.0", getHost(), getPort(), 1);
			
		socket = new Socket(getHost(), getPort());
		logger.info("Socket associated with " + getHost() + ":" + getPort() + " created");
		
		output = socket.getOutputStream();
		output.write(message.getBytes());
		logger.info("Sent information to server");
	}

	/**
	 *  Get response from server
	 * 
	 * The idea here is simple : Streaming server sends header information and then the actual audio data.
	 * However, there is a difference between when ShoutCast and IceCast servers start sending audio data:
	 *   ShoutCast sends audio data right after the header so we end up getting some audio bytes along with the header.
	 *   IceCast sends header and audio separately. At first, we get clean header with no audio data right after it
	 *   	and then with successive reads from socket we can get the actual audio stream.	
	 *    
	 * @return String Header sent by server
	 * @throws IOException
	 */
	public String getResponse() throws IOException {
		input = socket.getInputStream();
		String response = "";
		int bytesRead = 0;
		int index = 0;
		
		boolean endPresented = false;
		byte b[] = new byte[BUFSIZE];
		
		while (!endPresented && ((bytesRead = input.read(b)) != -1)){
			String temp = new String(b);
			
			if ((index = temp.indexOf(HEADERENDTOKEN)) != -1){
				endPresented = true;
				response += temp.substring(0, index + HEADERENDTOKEN.length());

				for (int i = index + HEADERENDTOKEN.length(); i < bytesRead; i++){
					buffer.offer((Byte) b[i]);
				}
			}
			else 
				response += temp;
		}

		musicBytes = bytesRead - (index + HEADERENDTOKEN.length());

		logger.info("Server responded");
		return response;
	}

	public void updateInfo(HashMap<String, String> map){
		setBitRate(processIntegerField(map, "icy-br"));
		setMetaInt(processIntegerField(map, "icy-metaint"));
	}

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void playStream() throws IOException, InterruptedException{
		System.out.println("Connection info: \n\tBR: " + getBitRate() + " kbps\n\tMetaInt: " + getMetaInt());

		class DownloadThread implements Runnable {
			private String oldMeta;
			
			@Override
			public void run() {
				byte[] localBuffer = new byte[BUFSIZE];
				int bytesRead, metaLength = 0, filledMetaLength = 0;
				String metaData = "";
				boolean gettingMeta = false;
				
				try {
					while ((bytesRead = input.read(localBuffer)) != -1){
						int current = 0;
						
						while (current < bytesRead){
						
							while (current < bytesRead && musicBytes < getMetaInt()){
								offertoBuffer(localBuffer[current++]);
								musicBytes++;
							}

							if (current == bytesRead) 
								break;
				
							if (!gettingMeta){
								metaLength = ((int) localBuffer[current++]) * 16;
								filledMetaLength = 0;
								gettingMeta = true;
							}
						
							while (current < bytesRead && filledMetaLength < metaLength){
								metaData += new String(localBuffer, current++, 1);					
								filledMetaLength++;
							}
							
							if (filledMetaLength == metaLength){
								printMetaData(metaData);
								metaData = "";
								gettingMeta = false;
								musicBytes = 0;
							}
						}
					}
				} catch (Exception e){
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			
			
			private void offertoBuffer(byte bt) throws InterruptedException {
				while (!buffer.offer((Byte) bt)) { 
					Thread.sleep(100); 
				}
			}

			private void printMetaData(String newMeta){
				if (oldMeta == null || !oldMeta.equals(newMeta)){
					oldMeta = newMeta;

					System.out.println("Playing: " + newMeta);
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
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.execute(new DownloadThread());
		
		int toBuffer = getBitRate() * SECONDSTOBUFFER * 1000 / 8;
		while (buffer.size() < toBuffer) {
			Thread.sleep(200);
			System.out.println("BUFFERING.. " + buffer.size()); 
		}

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
}