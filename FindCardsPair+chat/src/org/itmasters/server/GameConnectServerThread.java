package org.itmasters.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameConnectServerThread implements Runnable {
	
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	public GameConnectServerThread(Socket client) {
		try {
			socket = client;
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() 
				+ ":: GAME CONNECT SERVER CONNECTED");
		while(true){
			
			try {
				Object [] data = (Object[]) in.readObject();
				String action = (String)data[0];
				switch(action){
					case "server_start":
						String user_name = (String)data[1];
						new Thread(new MultiPlayerServer(user_name, (int)data[2], 
								(int)data[3], (int)data[4])).start();
						break;
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println(Thread.currentThread().getName() 
						+ ":: GAME CONNECT SERVER DISCONNECTED");
				break;
			}
		}
	}

}
