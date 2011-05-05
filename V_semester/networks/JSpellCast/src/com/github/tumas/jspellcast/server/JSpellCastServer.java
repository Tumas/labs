package com.github.tumas.jspellcast.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.tumas.jspellcast.proto.IcyProtocol;
import static com.github.tumas.jspellcast.server.JSpellCastServerGlobals.*;

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
	
	// lazy hack to make current server instance available in anonymous Thread classes
	private JSpellCastServer instance;
	
	private ArrayList<JSpellCastSource> sources = new ArrayList<JSpellCastSource>();
	//private HashMap<String, >
	
	JSpellCastServer() throws IOException{
		this(8001, 8000);
	}
	
	JSpellCastServer(int sourcePort, int clientPort) throws IOException{
		this.sourcePort = sourcePort;
		this.clientPort = clientPort;

		sourceSocket = new ServerSocket(sourcePort);
		clientSocket = new ServerSocket(clientPort);
	
		executor = Executors.newCachedThreadPool();
		instance = this;
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
				while (true){
					try {
						Socket newSourceSocket = sourceSocket.accept();

						if (getSourcesCount() == MAXSOURCES){
							newSourceSocket.close();
						}
						else {
							JSpellCastSource newSource = new JSpellCastSource(newSourceSocket, instance);
						
							sources.add(newSource);
							executor.execute(newSource);
							updateSourcesCount(1);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}	
			}
		}.start();
		
		// Client accept thread
		new Thread(){
			public void run(){
				// accept client
				// associate client with source
				// update counter

				Socket newClientSocket;
				
				while (true){
					
					printServerStats();
					
					try {
						newClientSocket = clientSocket.accept();
					
						if (getClientsCount() == MAXCLIENTS || getSourcesCount() == 0){
							System.out.println("Either maximum number of clients reached" +
									" or there are no stream sources for client to connect to");
							
							try {
								newClientSocket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
						} else {
							JSpellCastClient newClient = new JSpellCastClient(newClientSocket);
							JSpellCastSource sourceForClient = null;
							
							// possible blocking here
							newClient.handshake();
							System.out.println("NEW CLIENT: " + newClient);
							
							if ((sourceForClient = associateByMountPoint(newClient)) == null)
								try {
									sourceForClient = randomlyAssociate(newClient);
								} catch (Exception e){
									System.out.println("Could not associate client at a given moment. Better luck next time");
									newClient.disconnect();
								}

							if (sourceForClient != null){
								updateClientsCount(1);
								
								String infoMessage = String.format(IcyProtocol.ICYSRVTOCLMSG, "You are connected to a dummy spellcast server",
										"JSpellCast server", sourceForClient.getBitrate(), SERVERMETAINT); 
								
								System.out.println("INFO message: " + infoMessage);
								newClient.send(infoMessage.getBytes());
							}

							printServerStats();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public JSpellCastSource associateByMountPoint(JSpellCastClient client){
		JSpellCastSource src = findByMountPoint(client.getMountPoint());
		
		if (src != null) { 
			src.registerClient(client);
		}
		
		return src;
	}
	
	public JSpellCastSource randomlyAssociate(JSpellCastClient client){
		int sourceIndex = new Random().nextInt(getSourcesCount());
		JSpellCastSource src = null;
		
		src = sources.get(sourceIndex);
		
		System.out.println("Randomly selecting source: " + src.getMountPoint());
		src.registerClient(client);
		
		return src;
	}
	
	private JSpellCastSource findByMountPoint(String mountPoint){
		for (JSpellCastSource source : sources){
			if (source.getMountPoint().equals(mountPoint))
				return source;
		}

		return null;
	}
	
	public int getClientPort() {
		return clientPort;
	}
	
	public int getSourcePort(){
		return sourcePort;
	}

	public synchronized void printServerStats(){
		System.out.println(" ***************** INFO *****************************");

		for (JSpellCastSource src : sources){
			System.out.println("src: " + src.getMountPoint() + " - " + src.getName() + " (" + src.getClients().size() + ")");
			for (JSpellCastClient cl : src.getClients()){
				System.out.println("\t client: " + cl.getUserAgent());
			}
		}

		System.out.println("******************************************************");
		System.out.println("SOURCES: " + getSourcesCount() + "\nCLIENTS: " + getClientsCount());
	}

	public void removeSource(JSpellCastSource jSpellCastSource) {
		sources.remove(jSpellCastSource);
	}
}