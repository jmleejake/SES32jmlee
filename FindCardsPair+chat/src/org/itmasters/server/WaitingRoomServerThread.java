package org.itmasters.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import org.itmasters.client.MultiPlayerClient;
import org.itmasters.manager.CardPairDAO;

public class WaitingRoomServerThread implements Runnable {
	
	Socket socket;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	// ��ü����Ʈ
	private static ArrayList<WaitingRoomServerThread> room_list = new ArrayList<>();
	
	// ä�ð��ø���Ʈ (����ڸ���Ʈ)
	private static ArrayList<HashMap<String, String>> user_map_list = new ArrayList<>();
	private static ArrayList<String> user_list = new ArrayList<>();
	
	public WaitingRoomServerThread(Socket client) {
		try {
			socket = client;
			in = new ObjectInputStream(client.getInputStream());
			out = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		room_list.add(this);
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() 
				+ ":: GUI WAITINGROOM CONNECTED");
		try {
			out.writeObject(new Object[]{"title", "WAITINGROOM " 
		+ Thread.currentThread().getName()});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while(true) {
			try {
					Object[] data = (Object[])in.readObject();
					String type = (String)data[0];
					switch (type) {
						case "waiting":
							int insertResult = (int)data[1];
							int port_no = (int)data[2];
							if(insertResult > 0) {
								broadcast(new Object[]{"insert", insertResult});
							}
							broadcast(new Object[]{"port_no", port_no});
							
							// 1P ���� �����(2�ο� ����) ����ܿ��� ����
							String uname = (String)data[3];
							user_list.remove(uname);
							broadcast(new Object[]{"user_update", user_list, 
									String.format("[%s]���� �����Ϸ� ���̽��ϴ�!!", uname)});
							
							break;
							
						case "2p_started":
							int updateResult = (int)data[1];
							if(updateResult > 0) {
								broadcast(new Object[]{"update", updateResult});
							}
							
							uname = (String)data[2];
							user_list.remove(uname); // 2P ���� �����(����Ʈ Ŭ���� ) ����ܿ��� ����
							broadcast(new Object[]{"user_update", user_list, 
									String.format("[%s]���� �����Ϸ� ���̽��ϴ�!!", uname)});
							break;
							
						case "chat_first":
							String user_name = (String)data[1];
							
							user_list.add(user_name);
							HashMap<String, String> user_map = new HashMap<>();
							user_map.put(Thread.currentThread().getName(), user_name);
							user_map_list.add(user_map);
							
							broadcast(new Object[]{"first_update", 
									String.format("[%s]���� �����ϼ̽��ϴ�!!", user_name), 
									user_list});
							break;
							
						case "chat_msg":
							user_name = (String)data[1];
							String msg = (String)data[2];
							
							broadcast(new Object[]{"chat_update", 
									String.format(" %s : %s", user_name, msg)});
							break;
					}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				room_list.remove(this);
				// ��������� ���� ����Ʈ�������� ó��
				broadcast(new Object[]{"delete", 1});
				
				System.out.println(Thread.currentThread().getName() 
						+ ":: GUI WAITINGROOM DISCONNECTED");
				
				for (HashMap<String, String> map : user_map_list) {
					if(map.get(Thread.currentThread().getName()) != null) {
						user_list.remove(map.get(Thread.currentThread().getName()));
						broadcast(new Object[]{"last_update", 
								String.format("[%s]���� �����ϼ̽��ϴ�!!", 
								map.get(Thread.currentThread().getName())), 
								user_list});
						user_map_list.remove(map);
					}
				}
				
				break;
			}
		}
	}
	
	/**
	 * @return �� thread�� ����� socket�� outputstream
	 */
	public ObjectOutputStream getOut() {
		return out;
	}

	/**
	 * ���ӵǾ� �ִ� ��� ����ڿ��� �޽����� ����
	 * @param data => Object �迭����
	 */
	public void broadcast(Object[] data) {
		for (WaitingRoomServerThread th : room_list) {
			try {
				// �� thread�� ����� socket�� outputstream�� write��
				th.getOut().writeObject(data);
				
				// objectstream�� �¿� ������ data�� ������ ���� buffer������ ����ش�
				th.getOut().reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
