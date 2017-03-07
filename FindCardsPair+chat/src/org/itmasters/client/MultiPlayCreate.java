package org.itmasters.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

public class MultiPlayCreate {
	ObjectOutputStream out_waiting_room = null;
	ObjectInputStream in_waiting_room = null;
	
	String uname = null;
	
	public MultiPlayCreate() {
		int row = 0;
		int column = 0;
		
		// ����ڸ���
		while(true) {
			uname = JOptionPane.showInputDialog("����ڸ�?!");
			if(uname == null) {
				JOptionPane.showMessageDialog(null, "����ڸ��� �Է��϶��!!!");
			} else {
				break;
			}
		}
	      
		// ��/�� �� ���� (3~6)
		while(true) {
			while(true) {
				try {
					row = Integer.parseInt(JOptionPane.showInputDialog("��[5~9]"));
					if(4 < row && row < 9) break;
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "���ڰ� �ƴϴٿ�!!");
				}
			}
	         
			while(true) {
				try {
					column = Integer.parseInt(JOptionPane.showInputDialog("��[5~9]"));
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
			Socket client_waiting_room = new Socket("localhost", 8888);
			out_waiting_room = new ObjectOutputStream(client_waiting_room.getOutputStream());
			in_waiting_room = new ObjectInputStream(client_waiting_room.getInputStream());
			
			out_waiting_room.writeObject(new Object[]{"waiting", uname, InetAddress.getLocalHost().getHostAddress(), row, column});
			out_waiting_room.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
