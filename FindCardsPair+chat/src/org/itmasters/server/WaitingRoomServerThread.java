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
	
	// 전체리스트
	private static ArrayList<WaitingRoomServerThread> room_list = new ArrayList<>();
	
	// 채팅관련리스트 (사용자리스트)
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
							
							// 1P 게임 입장시(2인용 시작) 대기명단에서 제거
							String uname = (String)data[3];
							user_list.remove(uname);
							broadcast(new Object[]{"user_update", user_list, 
									String.format("[%s]님이 게임하러 가셨습니다!!", uname)});
							
							break;
							
						case "2p_started":
							int updateResult = (int)data[1];
							if(updateResult > 0) {
								broadcast(new Object[]{"update", updateResult});
							}
							
							uname = (String)data[2];
							user_list.remove(uname); // 2P 게임 입장시(리스트 클릭후 ) 대기명단에서 제거
							broadcast(new Object[]{"user_update", user_list, 
									String.format("[%s]님이 게임하러 가셨습니다!!", uname)});
							break;
							
						case "chat_first":
							String user_name = (String)data[1];
							
							user_list.add(user_name);
							HashMap<String, String> user_map = new HashMap<>();
							user_map.put(Thread.currentThread().getName(), user_name);
							user_map_list.add(user_map);
							
							broadcast(new Object[]{"first_update", 
									String.format("[%s]님이 입장하셨습니다!!", user_name), 
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
				// 게임종료시 대기실 리스트갱신위한 처리
				broadcast(new Object[]{"delete", 1});
				
				System.out.println(Thread.currentThread().getName() 
						+ ":: GUI WAITINGROOM DISCONNECTED");
				
				for (HashMap<String, String> map : user_map_list) {
					if(map.get(Thread.currentThread().getName()) != null) {
						user_list.remove(map.get(Thread.currentThread().getName()));
						broadcast(new Object[]{"last_update", 
								String.format("[%s]님이 퇴장하셨습니다!!", 
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
	 * @return 각 thread에 연결된 socket의 outputstream
	 */
	public ObjectOutputStream getOut() {
		return out;
	}

	/**
	 * 접속되어 있는 모든 사용자에게 메시지를 전달
	 * @param data => Object 배열변수
	 */
	public void broadcast(Object[] data) {
		for (WaitingRoomServerThread th : room_list) {
			try {
				// 각 thread에 연결된 socket의 outputstream을 write함
				th.getOut().writeObject(data);
				
				// objectstream을 태워 보내는 data의 갱신을 위해 buffer공간을 비워준다
				th.getOut().reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
