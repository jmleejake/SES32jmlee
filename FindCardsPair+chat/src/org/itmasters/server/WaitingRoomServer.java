package org.itmasters.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WaitingRoomServer {
	
	public WaitingRoomServer() {
		try {
			ServerSocket server = new ServerSocket(8888);
			System.out.println("WAITINGROOM SERVER START");
			while(true) {
				Socket client = server.accept();
				new Thread(new WaitingRoomServerThread(client)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new WaitingRoomServer();
	}

}
