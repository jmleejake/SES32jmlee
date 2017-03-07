package org.itmasters.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.itmasters.manager.PropertiesManager;

public class MultiPlayerClient extends JFrame implements Runnable, ActionListener {
	Socket client = null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	
	ArrayList<JButton> btnList = new ArrayList<>();
	HashMap<String, String> map_icon = new HashMap<>();
	
	String name_1p = null;
	String name_2p = null;
	
	JPanel p_center;
	
	JLabel lb_1p_name = null;
	JLabel lb_1p_score = null;
	JLabel lb_1p_combo = null;
	
	JLabel lb_2p_score = null;
	JLabel lb_2p_combo = null;
   
	JButton obj1 = null;
	JButton obj2 = null;
   
	String question_img = null;
	String theme = null;
	
	String val1 = null;
	String val2 = null;
   
	int turnup_cnt = 3; // ī������� ����(�ΰ� ī�带 ������ �ѹ��� Ŭ���ؾ� ����Ȯ�ΰ���)
	int end_point = 0; // ������ �������� �˼��ִ� ����
   
	int fail_cnt = 0;
	int success_cnt = 0;
	int continuously_cnt = 1;
	int continuously = 0;
   
	int total_score = 0;
	
	// �����۹ڽ�
	Properties config = PropertiesManager.getProperties();
	// ������ ��� Ƚ��
	int item_use_cnt = Integer.parseInt(config.getProperty("ITEM_USE"));
	JButton btn_item = null;
	JLabel lb_item_status = null;
	JButton btn_3turn;
	JButton btn_interrupt;
	JButton btn_show;
	JButton btn_shuffle;
	JLabel lb_item_use;
	// 3����ü����
	int sec = 0;
	// Ŭ������
	String interrupt_status = "F";
	String btn_key;
	// 3�� �����÷���
	int three_turn_play = 0;
	String three_turn_status = "F";
	// ���� ����
	ArrayList<String> valueList = new ArrayList<>();
	
	public MultiPlayerClient(String name, String ip, int port) {
		try {
			client = new Socket(ip, port);
			
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setBounds(700, 400, 500, 600);
			this.setTitle("MULTI PLAYER 2P");
			
			name_2p = name;
			makeNorth(name);
			
			out.writeObject(new Object[]{"2p_name", name});
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param map_icon server�� ī�忡 ���õ� name�� �̹����� ���� map
	 * @param strArr server�� ī�忡 ���õ� name
	 * @param row server�� ī�忡 ���õ� ��
	 * @param column server�� ī�忡 ���õ� ��
	 */
	public void makeCenter(HashMap<String, String> map_icon,
			String[] strArr,
			int row, int column) {
		this.map_icon = map_icon;
		p_center = new JPanel();
		p_center.setLayout(new GridLayout(row, column));
	     
		for (int i = 0; i < row*column; i++) {
			btnList.add(new JButton(new ImageIcon(map_icon.get("99"))));
		}
		
		end_point = btnList.size() / 2;
	    
		for(String str : strArr) {
			valueList.add(str);
		}
		
		int i = 0;
		for (JButton btn : btnList) {
			if(row*column > i) {
				btn.setName(valueList.get(i));
				p_center.add(btn);
				i++;
			}
		}
	      
		this.add(p_center, BorderLayout.CENTER);
	}
	
	/**
	 * 1P, 2P ����ڸ� / ���� / �����޺� / ������
	 * @param user_name
	 */
	public void makeNorth(String user_name) {
		JPanel p_north = new JPanel();
		p_north.setLayout(new GridLayout(1, 3, 20, 10));
	   
		JPanel p_north_1p_score = new JPanel();
		p_north_1p_score.setLayout(new GridLayout(3, 1));
		lb_1p_name = new JLabel(String.format("1P ����ڸ�: %s", user_name));
		lb_1p_score = new JLabel(String.format("1P ����: %d", total_score));
		lb_1p_combo = new JLabel(String.format("1P �����޺�: %d", continuously_cnt));
		p_north_1p_score.add(lb_1p_name);
		p_north_1p_score.add(lb_1p_score);
		p_north_1p_score.add(lb_1p_combo);
		p_north.add(p_north_1p_score);
		
		JPanel p_north_btn = new JPanel();
		p_north_btn.setLayout(new GridLayout(3, 1));
		btn_item = new JButton("�����۹ڽ�");
		btn_item.addActionListener(new ButtonAction());
		btn_item.setEnabled(false); // �ʱ�ȭ���� ��ư �����ϰ� disable
		lb_item_status = new JLabel("STATUS :: ");
		lb_item_use = new JLabel("���� ��� Ƚ�� :: " + item_use_cnt);
		p_north_btn.add(btn_item);
		p_north_btn.add(lb_item_status);
		p_north_btn.add(lb_item_use);
		p_north.add(p_north_btn);
		
		JPanel p_north_2p_score = new JPanel();
		p_north_2p_score.setLayout(new GridLayout(3, 1));
		JLabel lb_2p_name = new JLabel(String.format("2P ����ڸ�: %s", user_name));
		lb_2p_score = new JLabel(String.format("2P ����: %d", total_score));
		lb_2p_combo = new JLabel(String.format("2P �����޺�: %d", continuously_cnt));
		p_north_2p_score.add(lb_2p_name);
		p_north_2p_score.add(lb_2p_score);
		p_north_2p_score.add(lb_2p_combo);
		p_north.add(p_north_2p_score);
	      
		this.add(p_north, BorderLayout.NORTH);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Object[] ret = (Object[])in.readObject();
				switch ((String)ret[0]) {
				
				case "first": // �ʱ�ȭ�� client�� GUI����
					System.out.println("2P - first");
					String[] strArr = (String[])ret[1];
					HashMap<String, String> map_icon = (HashMap<String, String>)ret[2];
					int row = (int)ret[3];
					int column = (int)ret[4];
					name_1p = (String)ret[5];
					
					lb_1p_name.setText(String.format("1P ����ڸ�: %s", name_1p));
					makeCenter(map_icon, strArr, row, column);
					this.setVisible(true);
					break;
					
				case "turned": // �������� ������ ī��ǥ��
					System.out.println("2P - turned");
					String val = (String)ret[1];
					for (JButton btn : btnList) {
						if(btn.getName().equals(val)) {
							btn.setIcon(new ImageIcon(this.map_icon.get(val)));
							break;
						}
					}
					break;
					
				case "backturn": // ¦ ���߱⿡ �����Ͽ� �ٽ� �ǵ�����
					System.out.println("2P - backturn");
					val = (String)ret[1];
					String back = (String)ret[2];
					for (JButton btn : btnList) {
						if(btn.getName().equals(val)) {
							btn.setIcon(new ImageIcon(this.map_icon.get(back)));
							break;
						}
					}
					break;
					
				case "success": // ¦ ���߱⿡ �����Ͽ� ������ ���·� ����
					System.out.println("2P - success");
					val = (String)ret[1];
					valueList.remove(val);
					for (JButton btn : btnList) {
						if(btn.getName().equals(val)) {
							btn.setEnabled(false);
							break;
						}
					}
					break;
					
				case "turnover": // ��뿡�� Ŭ���ϴ� ������ �ѱ涧
					System.out.println("2P - turnover");
					String status = (String)ret[1];
					String key = (String)ret[2];
					String name = (String)ret[3];
					
					if(item_use_cnt != 0){    //�ڱ� �Ͽ� ������ ��ư �����ֱ�
	                    btn_item.setEnabled(true);
	                } else{
	                    btn_item.setEnabled(false);
	                }
		               
					if(status.equals("T")) {
						JOptionPane.showMessageDialog(null, String.format("[%s]���� [Ŭ������] �������� ����Ͽ����ϴ�.", name));
					}
					
					for (JButton btn : btnList) {
						btn.removeActionListener(this);
						if(btn.isEnabled()) {
							if(status.equals("T")) { // Ŭ������ �������� ��뿡�� ���������
								// Ŭ�����ظ� ���� ������ 4���� ī�尡 ������ �������
								if(btn.getName().equals(key) && end_point > 1) {
									btn.removeActionListener(this);
								} else {
									btn.addActionListener(this);
								}
							} else {
								btn.addActionListener(this);
							}
						}
					}
					turnup_cnt = 3;
					break;
					
				case "1p_score": // 1P�� score�� �����Ҷ�
					int a = (int) ret[1];
					lb_1p_score.setText(String.format("1P ����: %d", a));
					int b = (int) ret[2];
					lb_1p_combo.setText(String.format("1P �����޺�: %d", b));
					break;
					
				case "1p_combo": // 1�� �����޺����� �����Ҷ�
					lb_1p_combo.setText(String.format("1P �����޺�: %d", (int) ret[1]));
					break;
					
				case "get_end_point": // 1�κ��� end_point���� �Ѱܹ�����
					end_point = (int) ret[1];
					
					// ī���� ¦�� ��� ���� ���
					if(end_point == 0) {
						out.writeObject(new Object[]{"get_end_point", end_point, total_score, continuously_cnt});
						
						a = (int) ret[2];
						b = (int) ret[3];
						lb_1p_score.setText(String.format("1P ����: %d", a));
						lb_1p_combo.setText(String.format("1P �����޺�: %d", b));
						
						//��������޽���
						int score_1p = Integer.parseInt(lb_1p_score.getText().split(":")[1].trim());
						int score_2p = Integer.parseInt(lb_2p_score.getText().split(":")[1].trim());
						String vic_msg = score_1p == score_2p ? "���º�" : (score_1p < score_2p ? "��" : "��");
						JOptionPane.showMessageDialog(null, 
								String.format("�����ٿ�~~ \n��������:: %d \n����� %s!!!", total_score, vic_msg));
						
						System.exit(0); // main����
					}
					break;
					
				case "shuffle": // 1p���� ���� ī�弯�� ������
					System.out.println("2p - shuffle");
					Collections.shuffle(valueList);
					int i = 0;
					for (JButton btn : btnList) {
						if(btn.isEnabled()) {
							btn.setName(valueList.get(i));
							i++;
						}
						
					}
					break;
					
				case "1p_close": // 1p�� â�� �ݾ�����
					JOptionPane.showMessageDialog(null, String.format("[%s]���� �����ٿ�!!!", name_1p));
					break;
					
				case "item_used": // 1p�� �������� ����Ͽ�����
					name = (String)ret[1];
					String used_item = (String)ret[2];
					JOptionPane.showMessageDialog(null, String.format("[%s]���� [%s] �������� ����Ͽ����ϴ�.", name, used_item));
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SocketException se) {
			try {
				if(client!=null) client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		turnup_cnt--;
		if(turnup_cnt == 2) { // 1��ī��
			obj1 = (JButton)e.getSource();
			val1 = ((JComponent) e.getSource()).getName();
			setIconImage(obj1, val1);
			if(interrupt_status.equals("T")) { // Ŭ������ �������� ���� �����϶�
				btn_key = val1;
			}
			try {
				out.writeObject(new Object[]{"turned", val1});
				out.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (turnup_cnt == 1) { // 2��ī��
			obj2 = (JButton)e.getSource();
			val2 = ((JComponent) e.getSource()).getName();
			if(obj1 != obj2) {
				setIconImage(obj2, val2);
				
				try {
					out.writeObject(new Object[]{"turned", val2});
					out.reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				turnup_cnt = 2;
			}
		} else if (turnup_cnt == 0) { // 1��-2���� ��� (3��°Ŭ��)
			if(three_turn_status.equals("T")) { // 3�� �����÷��� �������� ���� ���
				--three_turn_play;
				lb_item_status.setText(String.format(
						"STATUS :: ���� �� :: %d", three_turn_play));
			}
			turnup_cnt = 3;
			String str_val1 = val1;
			String str_val2 = val2;
			val1 = val1.split("-")[0];
			val2 = val2.split("-")[0];
			int val1 = Integer.parseInt(this.val1);
			int val2 = Integer.parseInt(this.val2);
			
			if(val1 != val2) { // ������ ī���� �� ���� ���� ������
				try {
					if(three_turn_status.equals("F")) {
						out.writeObject(new Object[]{"turnover", interrupt_status, btn_key, name_2p});
						out.reset();
						interrupt_status = "F"; // Ŭ������ status�� �ʱ�ȭ
						out.writeObject(new Object[]{"backturn", str_val1, "99"});
						out.writeObject(new Object[]{"backturn", str_val2, "99"});
						out.reset();
					} else {
						if(three_turn_play == 0) {
							out.writeObject(new Object[]{"turnover", interrupt_status, btn_key, name_2p});
							interrupt_status = "F"; // Ŭ������ status�� �ʱ�ȭ
							three_turn_status = "F"; // 3�� �����÷��� status�� �ʱ�ȭ
							lb_item_status.setText("STATUS :: ");
						}
						out.writeObject(new Object[]{"backturn", str_val1, "99"});
						out.writeObject(new Object[]{"backturn", str_val2, "99"});
						out.reset();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// ���� �Ѿ�� ������ �����۹ڽ��� ��ư�� ������� ���ϰ� disable
				btn_item.setEnabled(false);
				
				// 3�� �����÷��̸� ������� ������ ��� ī�� action listener����
				if(three_turn_status.equals("F")) {
					for (JButton btn : btnList) {
						btn.removeActionListener(this);
					}
				}
				
				setIconImage(obj1, "99");
				setIconImage(obj2, "99");
				fail_cnt++;
				continuously = 0;
				continuously_cnt = 0;
				
				lb_2p_combo.setText(String.format("2P �����޺�: %d", continuously_cnt));
				try {
					out.writeObject(new Object[]{"2p_combo", continuously_cnt});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
			
			if(val1 == val2) { // ������ ī���� �� ���� ������
				if(obj1 != obj2) { // ������ ī�带 ���� 3�������� ����
					obj1.removeActionListener(this); // disable�ӿ��� Ŭ��event�� �����ϹǷ� listener ����
					obj2.removeActionListener(this);
					obj1.setEnabled(false);
					obj2.setEnabled(false);
					total_score += 100; // 
					end_point--;
					success_cnt++;
					
					// ���� ���� �������� ���� list���� ������ �� ����
					valueList.remove(str_val1);
					valueList.remove(str_val2);

					if(success_cnt > 0 && continuously > 0) { // �������� ���� ���
						continuously_cnt++;
						if(success_cnt == continuously) {
							total_score += continuously_cnt*20;
						}
					}
					
					try {
						out.writeObject(new Object[]{"success", str_val1});
						out.writeObject(new Object[]{"success", str_val2});
						out.reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					lb_2p_score.setText(String.format("2P ����: %d", total_score));
					lb_2p_combo.setText(String.format("2P �����޺�: %d", continuously_cnt));
					try {
						out.writeObject(new Object[]{"2p_score", total_score, continuously_cnt});
						out.reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					// 1P2P�� ������ end_point�� ����
					try {
						out.writeObject(new Object[]{"get_end_point", end_point, total_score, continuously_cnt});
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					continuously = success_cnt;
					success_cnt = 0;

				} // obj1 != obj2
			} // val1 == val2
			this.val1 = null;
			this.val2 = null;
			try {
				out.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} // turnup_cnt == 0
	}
	
	/**
	* @param obj ī�� object
	* @param val �ش� ī�� object�� name
	*/
	public void setIconImage(JButton obj, String val) {
		obj.setIcon(null); // ���� ī�尡 ������ �ִ� �̹��� ����
		obj.setIcon(new ImageIcon(map_icon.get(val))); // ���ο� �̹��� ����
	}
	
	class ButtonAction implements ActionListener {
		Timer show_time = null;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btn_item) { // �����۹ڽ�
				new ItemBox();
			} else if(e.getSource() == btn_show) { // 3�� ��ü����
				sec = 3;
				
				for (JButton btn : btnList) {
					btn.setEnabled(false);
				}
				show_time = new Timer(1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						lb_item_status.setFont(new Font("�������", Font.PLAIN, 12));
						lb_item_status.setText(String.format("STATUS :: �����ð�: %d", sec));
						for (JButton btn : btnList) {
							setIconImage(btn, btn.getName());
						}
						sec--;
						
						if(sec == -1) {
							show_time.stop();
							for (JButton btn : btnList) {
								for(String str : valueList) {
									if(btn.getName().equals(str)) {
										setIconImage(btn, "99");
										btn.setEnabled(true);
									}
								}
							}
							lb_item_status.setText("STATUS :: ");
							sec = 3;
						}
					}
				});
				show_time.start();
				
			}
		} 
	}
	
	class ItemBox extends JFrame implements ActionListener {
		int turn_cont = Integer.parseInt(config.getProperty("ITEM_TURN_CONT"));
		int show_all = Integer.parseInt(config.getProperty("ITEM_SHOW_ALL"));
		
		public ItemBox() {
			this.setTitle("�����۹ڽ�");
			this.setBounds(200, 100, 250, 110);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setResizable(false);
			JPanel p_btn_item = new JPanel();
			p_btn_item.setLayout(new GridLayout(2, 2));
			btn_3turn = new JButton(turn_cont + "�� ���� �÷���");
			btn_interrupt = new JButton("Ŭ������");
			btn_show = new JButton(show_all + "�� ��ü����");
			btn_shuffle = new JButton("���� ī�� ����");
			btn_3turn.setFont(new Font("�������", Font.PLAIN, 11));
			btn_interrupt.setFont(new Font("�������", Font.PLAIN, 11));
			btn_show.setFont(new Font("�������", Font.PLAIN, 11));
			btn_shuffle.setFont(new Font("�������", Font.PLAIN, 11));
			btn_3turn.addActionListener(this);
			btn_interrupt.addActionListener(this);
			btn_show.addActionListener(this);
			btn_show.addActionListener(new ButtonAction());
			btn_shuffle.addActionListener(this);
			p_btn_item.add(btn_3turn);
			p_btn_item.add(btn_interrupt);
			p_btn_item.add(btn_show);
			p_btn_item.add(btn_shuffle);
			this.add(p_btn_item);
			this.setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Object[] data = null;
			 if(e.getSource() == btn_interrupt) { // Ŭ������
				int item_ret = JOptionPane.showConfirmDialog(null, 
						"Ŭ�����ش� ������ Ŭ���ϰ� �ϰ���� ���� 1�� ��ư�� Ŭ���ϸ� \n"
						+ "���� �Ͽ� ��밡 Ŭ������ ���ϰ� �Ǵ� �������Դϴ�. \n"
						+ "�ش� ī���� ¦�� ���߰ԵǸ� �������� ��ȿȭ �˴ϴ�. \n"
						+ "�������� ���ðڽ��ϱ�?", 
						"ī�� ¦ ���߱�!!", JOptionPane.YES_NO_OPTION);
				if(item_ret == 0) { // �������� ���� ���
					--item_use_cnt;
	                lb_item_use.setText("���� ��� Ƚ�� :: " + item_use_cnt);
	                btn_item.setEnabled(false);       //������ ��ư disable
					interrupt_status = "T";
					dispose();
				}
			} else if(e.getSource() == btn_3turn) { // 3�� �����÷���
				data = new Object[]{"item_used", name_2p, turn_cont + "�� ���� �÷���"};
				--item_use_cnt;
                lb_item_use.setText("���� ��� Ƚ�� :: " + item_use_cnt);
                btn_item.setEnabled(false);       //������ ��ư disable
				three_turn_status = "T";
				three_turn_play = turn_cont;
				lb_item_status.setText(String.format(
						"STATUS :: ���� �� :: %d", three_turn_play));
				dispose();
			} else if(e.getSource() == btn_show) { // 3�� ��ü����
				data = new Object[]{"item_used", name_2p, show_all + "�� ��ü����"};
				sec = show_all;
				--item_use_cnt;
                lb_item_use.setText("���� ��� Ƚ�� :: " + item_use_cnt);
                btn_item.setEnabled(false);       //������ ��ư disable
				dispose();
			} else if(e.getSource() == btn_shuffle) {
				data = new Object[]{"item_used", name_2p, "���� ī�� ����"};
				try {
					--item_use_cnt;
	                lb_item_use.setText("���� ��� Ƚ�� :: " + item_use_cnt);
	                btn_item.setEnabled(false);       //������ ��ư disable
					out.writeObject(new Object[]{"shuffle"});
					dispose();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			 
			 if(e.getSource() != btn_interrupt) {
				 // 1P���� ������ ��� ���θ� �˷��ٶ�
				 try {
					 out.writeObject(data);
				 } catch (IOException e1) {
					 e1.printStackTrace();
				 }
			 }
		}
	}
	
	public static void main(String[] args) {
		new Thread(new MultiPlayerClient("22222", "localhost", 11111)).start();
	}
}
