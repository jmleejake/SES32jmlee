package org.itmasters.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.itmasters.client.MultiPlayerClient;
import org.itmasters.manager.CardPairDAO;

public class GameConnectServer {

	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public GameConnectServer(){
		try {
			ServerSocket server = new ServerSocket(9999);
			System.out.println("GAME CONNECT SERVER START");
			while(true){
				Socket client = server.accept();
				new Thread(new GameConnectServerThread(client)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GameConnectServer();
	}
	
}
