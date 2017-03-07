package org.itmasters.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.itmasters.manager.CardPairDAO;
import org.itmasters.manager.PropertiesManager;

public class WaitingRoomClient extends JFrame implements ActionListener, Runnable {
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	ObjectOutputStream game_out = null;
	
	JButton btn_p1;
	JButton btn_p2;
	
	JTable table;
	DefaultTableModel model;
	JTextArea chat_area;
	JScrollPane scroll_chat;
	JList<String> user_list;
	DefaultListModel<String> user_model;
	JTextField tf_chat_msg;
	
	JLabel lb_type;
	JLabel lb_name;
	JLabel lb_ip;
	JLabel lb_port;
	JButton btn_join;
	
	CardPairDAO dao;
	HashMap<String, Object> map = new HashMap<>();
	
	int selected_row = -1;
	int game_port = 2016;
	
	String uname = null; // ���� ����� ����ڸ� �Է�
	String ip = PropertiesManager.getProperties().getProperty("WRC_IP");
	
	public WaitingRoomClient() {
		this.setBounds(100, 200, 650, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		/*DAO START*/
		dao = new CardPairDAO();
		
		/*SOCKET RUN*/
		socketClientRun();
		gameSocketClientRun();
		
		// ����ڸ���
		while(true) {
			uname = JOptionPane.showInputDialog("����ڸ�?!");
			if(uname == null) {
				JOptionPane.showMessageDialog(null, "����ڸ��� �Է��϶��!!!");
			} else {
				HashMap<String, Object> map = dao.selectGameServer(uname);
				boolean isDup = ((String)map.get("user_name")).equals("none");
				if(!isDup) {
					JOptionPane.showMessageDialog(null, "�ߔ�!!!");
				} else {
					break;
				}
			}
		}
		
		makeNorth();
		makeCenter();
		
		this.setVisible(true);
		
		setAll();
	}
	
	public void makeNorth() {
		btn_p1 = new JButton("1�ο� ����");
		btn_p1.addActionListener(this);
		btn_p2 = new JButton("2�ο� ����");
		btn_p2.addActionListener(this);
		JPanel p_north = new JPanel(new GridLayout(1, 2, 0, 1000));
		
		p_north.add(btn_p1);
		p_north.add(btn_p2);
		
		this.add(p_north, BorderLayout.NORTH);
	}
	
	public void makeCenter() {
		JPanel p_center = new JPanel();
		p_center.setLayout(new GridLayout(1, 2));
		
		JPanel p_table_list = new JPanel();
		p_table_list.setLayout(new GridLayout(2, 1));
		
		// Game table
		JScrollPane scroll = new JScrollPane();
		table = new JTable();
		table.addMouseListener(new MouseAction());
		scroll.setViewportView(table);
		p_table_list.add(scroll);
		
		// Chating message list
		JPanel p_chat = new JPanel();
		p_chat.setLayout(new BorderLayout());
		scroll_chat = new JScrollPane();
		chat_area = new JTextArea();
		scroll_chat.setViewportView(chat_area);
		p_chat.add(scroll_chat, BorderLayout.CENTER);
		tf_chat_msg = new JTextField();
		tf_chat_msg.addActionListener(new ChatAction());
		p_chat.add(tf_chat_msg, BorderLayout.SOUTH);
		JLabel lb_chat_title = new JLabel("    [ ä  ��  â ]");
		p_chat.add(lb_chat_title, BorderLayout.NORTH);
		p_table_list.add(p_chat);
		
		p_center.add(p_table_list);
		
		JPanel p_detail_user_list = new JPanel();
		p_detail_user_list.setLayout(new GridLayout(2, 1));
		
		// GameRoom detail information
		JPanel p_detail = new JPanel(new GridLayout(5, 1));
		lb_type = new JLabel("Ÿ��:  ");
		lb_name = new JLabel("����ڸ�:  ");
		lb_ip = new JLabel("IP:  ");
		lb_port = new JLabel("Port No.  ");
		btn_join = new JButton("����");
		btn_join.addActionListener(this);
		p_detail.add(lb_type);
		p_detail.add(lb_name);
		p_detail.add(lb_ip);
		p_detail.add(lb_port);
		p_detail.add(btn_join);
		p_detail_user_list.add(p_detail);
		
		// user list
		JPanel p_user_list = new JPanel();
		p_user_list.setLayout(new BorderLayout());
		JScrollPane scroll_user = new JScrollPane();
		user_list = new JList<>();
		user_model = new DefaultListModel<>();
		try {
			out.writeObject(new Object[]{"chat_first", uname});
		} catch (IOException e) {
			e.printStackTrace();
		}
		scroll_user.setViewportView(user_list);
		p_user_list.add(scroll_user, BorderLayout.CENTER);
		JLabel lb_title = new JLabel("[ ���� ������ ��� ]");
		p_user_list.add(lb_title, BorderLayout.NORTH);
		p_detail_user_list.add(p_user_list);
		
		p_center.add(p_detail_user_list);
		
		this.add(p_center, BorderLayout.CENTER);
	}
	
	public void socketClientRun() {
		try {
			Socket client = new Socket(ip, 8888);
			
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void gameSocketClientRun() {
		try {
			Socket client = new Socket(ip, 9999);
			
			game_out = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream game_in = new ObjectInputStream(client.getInputStream());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setAll() {
		model = new DefaultTableModel(
				new String[]{"Ÿ��", "����ڸ�",  "��⿩��"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(model);
		ArrayList<HashMap<String, Object>> svr_list = dao.selectGameServerList();
		for (HashMap<String, Object> hmap : svr_list) {
			model.addRow(new Object[]{hmap.get("type"), hmap.get("user_name"), hmap.get("wait")});
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_p1) {
			new SinglePlayer(uname);
		} else if(e.getSource() == btn_p2) {
			createWaitGameRoom();
		} else if(e.getSource() == btn_join) {
			String ip = null;
			int port = 0;
			
			if(selected_row != -1) {
				String user_name = (String)model.getValueAt(table.getSelectedRow(), 1);
				HashMap<String, Object> gameMap = dao.selectGameServer(user_name);
				ip = (String)gameMap.get("ip");
				port = (int)gameMap.get("port");
				
				new Thread(new MultiPlayerClient(uname, ip, port)).start();
				
				// "������"���� ���º���
				int ret = dao.updateGameServer(user_name);
				if(ret > 0) {
					try {
						out.writeObject(new Object[]{"2p_started", ret, uname});
						out.reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "����Ʈ���� ���� �����ϼ���!!");
			}
		}
	}
	
	class MouseAction extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			selected_row = table.getSelectedRow();
			String user_name = (String)model.getValueAt(selected_row, 1);
			
			HashMap<String, Object> gameMap = dao.selectGameServer(user_name);
			lb_type.setText("Ÿ��: " + gameMap.get("type"));
			lb_name.setText("����ڸ�: " + gameMap.get("user_name"));
			lb_ip.setText("IP: " + gameMap.get("ip"));
			lb_port.setText("Port No. " + gameMap.get("port"));
			
			if(((String)gameMap.get("wait")).equals("F")) {
				btn_join.setEnabled(false);
			} else {
				btn_join.setEnabled(true);
			}
		}
	}
	
	class ChatAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String msg = tf_chat_msg.getText();
			try {
				out.writeObject(new Object[]{"chat_msg", uname, msg});
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			tf_chat_msg.setText("");
		}
	}
	
	public void createWaitGameRoom() {
		int row = 0;
		int column = 0;
		
		// ��/�� �� ���� (3~6)
		while(true) {
			while(true) {
				try {
					row = Integer.parseInt(JOptionPane.showInputDialog("��[5~8]"));
					if(4 < row && row < 9) break;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "���ڰ� �ƴϴٿ�!!");
				}
			}
	         
			while(true) {
				try {
					column = Integer.parseInt(JOptionPane.showInputDialog("��[5~8]"));
					if(4 < column && column < 9) break;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "���ڰ� �ƴϴٿ�!!");
				}
			}
	         
			if((row * column) % 2 == 0) {
				break;
			} else {
				JOptionPane.showMessageDialog(null, "��� ���� ���� ¦������ �Ѵٿ�!!!");
			}
		}
		
		try {
			map.put("user_name", uname);
			map.put("ip", InetAddress.getLocalHost().getHostAddress());
			map.put("row", row);
			map.put("column", column);
			map.put("game_port", game_port);
			
			game_out.writeObject(new Object[]{"server_start", uname, row, column, game_port});
			game_out.reset();
			
			int ret = dao.insertGameServer(map);
			if(ret > 0) game_port++; 
			out.writeObject(new Object[]{"waiting", ret, game_port, uname});
			out.reset();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Object[] data = (Object[])in.readObject();
				switch ((String)data[0]) {
					case "insert":
						int ret = (int)data[1];
						if(ret > 0) {
							setAll();
						}
						break;
						
					case "title":
						this.setTitle((String)data[1]);
						break;
						
					case "update":
						ret = (int)data[1];
						if(ret > 0) {
							setAll();
						}
						break;
						
					case "port_no":
						this.game_port = (int)data[1];
						break;
						
					case "delete":
						ret = (int)data[1];
						if(ret > 0) {
							setAll();
						}
						break;
						
					case "first_update":
					case "last_update":
						user_model.clear();
						
						String chat = (String) data[1];
						chat_area.append(chat + "\n");
						
						for (String user : (ArrayList<String>) data[2]) {
							user_model.addElement(user);
						}
						user_list.setModel(user_model);
						scroll_chat.getVerticalScrollBar()
							.setValue(scroll_chat.getVerticalScrollBar().getMaximum());
						break;
						
					case "chat_update":
						chat = (String) data[1];
						chat_area.append(chat + "\n");
						scroll_chat.getVerticalScrollBar()
						.setValue(scroll_chat.getVerticalScrollBar().getMaximum());
						break;
						
					case "user_update":
						user_model.clear();
						for (String user : (ArrayList<String>) data[1]) {
							user_model.addElement(user);
						}
						user_list.setModel(user_model);
						
						chat = (String) data[2];
						chat_area.append(chat + "\n");
						scroll_chat.getVerticalScrollBar()
						.setValue(scroll_chat.getVerticalScrollBar().getMaximum());
						
						break;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		new Thread(new WaitingRoomClient()).start();
	}
}
