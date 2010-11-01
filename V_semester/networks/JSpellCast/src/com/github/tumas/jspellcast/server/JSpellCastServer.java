package com.github.tumas.jspellcast.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JSpellCastServer {
	public final int MAXCLIENTS = 10;
	public final int MAXSOURCES = 5;

	private int clientPort;
	private int sourcePort;
	
	private ServerSocket clientSocket;
	private ServerSocket sourceSocket;
	
	private ExecutorService executor;

	private int clientsConnected = 0;
	private int sourcesConnected = 0;
	
	JSpellCastServer() throws IOException{
		this(8001, 8000);
	}
	
	JSpellCastServer(int sourcePort, int clientPort) throws IOException{
		this.sourcePort = sourcePort;
		this.clientPort = clientPort;

		sourceSocket = new ServerSocket(sourcePort);
		clientSocket = new ServerSocket(clientPort);
	
		executor = Executors.newCachedThreadPool();
	}
	
	public synchronized void updateSourcesCount(int value){
		sourcesConnected += value;
	}
	
	public synchronized void updateClientsCount(int value){
		clientsConnected += value;
	}
	
	public synchronized int getClientsCount(){
		return clientsConnected;
	}
	
	public synchronized int getSourcesCount(){
		return sourcesConnected;
	}
	
	public void run() {
		// Source accept thread
		new Thread(){
			public void run(){
				try {
					Socket newSourceSocket = sourceSocket.accept();

					if (getSourcesCount() == MAXSOURCES){
						newSourceSocket.close();
					}
					else {
						executor.execute(new JSpellCastSource(newSourceSocket));
						updateSourcesCount(1);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		// Client accept thread
		new Thread(){
			public void run(){
				// accept client
				// assign source to client ? spawn a new thread for each client
				// update counter
			}
		}.start();
	}

	public int getClientPort() {
		return clientPort;
	}
	
	public int getSourcePort(){
		return sourcePort;
	}
}