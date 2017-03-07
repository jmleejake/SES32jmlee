package org.itmasters.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.itmasters.manager.CardPairDAO;
import org.itmasters.manager.UtilClass;

public class SinglePlayer extends JFrame implements ActionListener{
   ArrayList<JButton> btnList = new ArrayList<>();
   HashMap<Integer, String> map_icon;
   
   JLabel lb_score = null;
   JLabel lb_combo = null;
   
   JButton obj1 = null;
   JButton obj2 = null;
   
   JButton btn_all_score = null;
   
   String uname = null;
   String question_img = null;
   String game_type = null; // �������忡 ����� type����
   
   int val1 = 0;
   int val2 = 0;
   
   int turnup_cnt = 3; // ī������� ����(�ΰ� ī�带 ������ �ѹ��� Ŭ���ؾ� ����Ȯ�ΰ���)
   int end_point = 0; // ������ �������� �˼��ִ� ����
   
   int fail_cnt = 0;
   int success_cnt = 0;
   int continuously_cnt = 1;
   int continuously = 0;
   
   int total_score = 0;
   
   CardPairDAO dao;
   
   /**
    * Constructor - GUI����
	* @param user_name ����ڸ�
	*/
   public SinglePlayer(String user_name) {
	   
      this.setBounds(100, 200, 400, 500);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      dao = new CardPairDAO();
      
      // ����ڸ���
      if(user_name == null) {
    	  while(true) {
    		  uname = JOptionPane.showInputDialog("����ڸ�?!");
    		  if(uname == null) {
    			  JOptionPane.showMessageDialog(null, "����ڸ��� �Է��϶��!!!");
    		  } else {
    			  HashMap<String, Object> ret = dao.selectGameServer(uname);
    			  if(((String)ret.get("user_name")).equals("none")) {
    				  break;
    			  } else {
    				  JOptionPane.showMessageDialog(null, "�̹� �����ϴ� ����ڶ��!!!");
    			  }
    		  }
    	  }
      } else {
    	  // ���� ����۽� �Ѿ�� ����ڸ��� ��������� ����
    	  uname = user_name;
      }
      
      int row = 0;
      int column = 0;
      
      // ��/�� �� ���� (3~6)
      while(true) {
         while(true) {
            try {
               row = Integer.parseInt(JOptionPane.showInputDialog("��[3~6]"));
               if(2 < row && row < 7) break;
            } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(null, "���ڰ� �ƴϴٿ�!!");
            }
         }
         
         while(true) {
            try {
               column = Integer.parseInt(JOptionPane.showInputDialog("��[3~6]"));
               if(2 < column && column < 7) break;
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
      
      game_type = row + "X" + column; 
      
      makeNorth(uname);
	  makeCenter(row, column);
	  
      this.setVisible(true);
   }
   
   /**
    * ����ڸ� / ���� / �����޺� / ���������˾� / �׸������˾�
	* @param user_name
	*/
   public void makeNorth(String user_name) {
	   JPanel p_north = new JPanel();
	   p_north.setLayout(new GridLayout(1, 3));
	      
	   JLabel lb_name = new JLabel(String.format("����ڸ�: %s", user_name));
	   p_north.add(lb_name);
	   
	   JPanel p_north_score = new JPanel();
	   p_north_score.setLayout(new GridLayout(2, 1));
	   lb_score = new JLabel(String.format("����: %d", total_score));
	   lb_combo = new JLabel(String.format("�����޺�: %d", continuously_cnt));
	   p_north_score.add(lb_score);
	   p_north_score.add(lb_combo);
	   p_north.add(p_north_score);
	      
	   JPanel p_north_btn = new JPanel();
	   p_north_btn.setLayout(new GridLayout(1, 1));
	   btn_all_score = new JButton("��������");
	   btn_all_score.addActionListener(new ButtonAction());
	   p_north_btn.add(btn_all_score);
	   p_north.add(p_north_btn);
	      
	   this.add(p_north, BorderLayout.NORTH);
   }
   
   /**
    * ī�� ����
	* @param row ��
	* @param column ��
	*/
   public void makeCenter(int row, int column) {
	   JPanel p_center = new JPanel();
	   p_center.setLayout(new GridLayout(row, column));
	   
	   question_img = String.format("icons/question/question_%d.png", (int)(Math.random()*5) + 1);
	     
	   for (int i = 0; i < row*column; i++) {
	      btnList.add(new JButton(new ImageIcon(question_img)));
	   }
	   end_point = btnList.size() / 2;
	   map_icon = UtilClass.getRandomIconImgForSingle(btnList.size() / 2);
	   map_icon.put(99, question_img);
	   System.out.println(map_icon);
	     
	   int j = 0;
	   for (int i = 0; i < btnList.size(); i+=2) {
	      btnList.get(i).setName(j+"");
	      btnList.get(i).addActionListener(this);
	      j++;
	   }
	      
	   int k = 0;
	   for (int i = 1; i < btnList.size(); i+=2) {
	      btnList.get(i).setName(k+"");
	      btnList.get(i).addActionListener(this);
	      k++;
	   }
	      
	   Collections.shuffle(btnList);
	      
	   for (JButton btn : btnList) {
		   p_center.add(btn);
	   }
	      
	   this.add(p_center, BorderLayout.CENTER);
   }
	
   @Override
   public void actionPerformed(ActionEvent e) {
	   turnup_cnt--;
		if(turnup_cnt == 2) { // 1��ī��
			obj1 = (JButton)e.getSource();
			val1 = Integer.parseInt(((JComponent) e.getSource()).getName());
			setIconImage(obj1, val1);
		} else if (turnup_cnt == 1) { // 2��ī��
			obj2 = (JButton)e.getSource();
			val2 = Integer.parseInt(((JComponent) e.getSource()).getName());
			if(obj1 != obj2) {
				setIconImage(obj2, val2);
			} else {
				turnup_cnt = 2;
			}
		} else if (turnup_cnt == 0) { // 1��-2���� ��� (3��°Ŭ��)
			turnup_cnt = 3;
			if(val1 != val2) { // ������ ī���� �� ���� ���� ������
				setIconImage(obj1, 99);
				setIconImage(obj2, 99);
				fail_cnt++;
				continuously = 0;
				continuously_cnt = 0;
				lb_combo.setText(String.format("�����޺�: %d", continuously_cnt));
			} else { // ������ ī���� �� ���� ������
				if(obj1 != obj2) { // ������ ī�带 ���� 3�������� ����
					obj1.removeActionListener(this); // disable�ӿ��� Ŭ��event�� �����ϹǷ� listener ����
					obj2.removeActionListener(this);
					obj1.setEnabled(false);
					obj2.setEnabled(false);
					total_score += 100; // 
					end_point--;
					success_cnt++;

					if(success_cnt > 0 && continuously > 0) { // �������� ���� ���
						continuously_cnt++;
						if(success_cnt == continuously) {
							total_score += continuously_cnt*20;
						}
					}
 
					lb_score.setText(String.format("����: %d", total_score));
					lb_combo.setText(String.format("�����޺�: %d", continuously_cnt));
 
					continuously = success_cnt;
					success_cnt = 0;

					// ī���� ¦�� ��� ���� ���
					if(end_point == 0) {
						// �������� DB����
						dao.insertLeaderBoard(uname, game_type, total_score);
						//��������޽���
						int ret = JOptionPane.showConfirmDialog(null, 
								String.format("�����ު�������\n��������:: %d \n�ٽ� �����Ͻðڽ��ϱ�?!", total_score), 
								"ī�� ¦ ���߱�!!", JOptionPane.YES_NO_OPTION);
						if(ret != 0) {
							System.exit(0); // main����
						} else {
							// ����۽�
							this.setVisible(false); // ����â �ݱ�
							new SinglePlayer(uname); // ���� ����ڸ�� �Բ� ������ ��ȣ��
						}
					}
				} // obj1 != obj2
			} // val1 == val2
		} // turnup_cnt == 0
   }
   
   class ButtonAction implements ActionListener {
	   @Override
	   public void actionPerformed(ActionEvent e) {
		   if(e.getSource() == btn_all_score) {
				new LeaderBoard();
		   }
	   }
   }
   
   /**
    * @param obj ī�� object
    * @param val �ش� ī�� object�� name
    */
   public void setIconImage(JButton obj, int val) {
	   obj.setIcon(null); // ���� ī�尡 ������ �ִ� �̹��� ����
	   obj.setIcon(new ImageIcon(map_icon.get(val))); // ���ο� �̹��� ����
   }
   
   public static void main(String[] args) {
	   new SinglePlayer("1111111");
   }
   
}