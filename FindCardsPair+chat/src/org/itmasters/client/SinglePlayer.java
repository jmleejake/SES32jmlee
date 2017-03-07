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
   String game_type = null; // 리더보드에 저장시 type변수
   
   int val1 = 0;
   int val2 = 0;
   
   int turnup_cnt = 3; // 카드뒤집기 변수(두개 카드를 뒤집고 한번더 클릭해야 점수확인가능)
   int end_point = 0; // 게임이 끝났음을 알수있는 변수
   
   int fail_cnt = 0;
   int success_cnt = 0;
   int continuously_cnt = 1;
   int continuously = 0;
   
   int total_score = 0;
   
   CardPairDAO dao;
   
   /**
    * Constructor - GUI구성
	* @param user_name 사용자명
	*/
   public SinglePlayer(String user_name) {
	   
      this.setBounds(100, 200, 400, 500);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      dao = new CardPairDAO();
      
      // 사용자명세팅
      if(user_name == null) {
    	  while(true) {
    		  uname = JOptionPane.showInputDialog("사용자명?!");
    		  if(uname == null) {
    			  JOptionPane.showMessageDialog(null, "사용자명을 입력하라요!!!");
    		  } else {
    			  HashMap<String, Object> ret = dao.selectGameServer(uname);
    			  if(((String)ret.get("user_name")).equals("none")) {
    				  break;
    			  } else {
    				  JOptionPane.showMessageDialog(null, "이미 존재하는 사용자라우!!!");
    			  }
    		  }
    	  }
      } else {
    	  // 게임 재시작시 넘어온 사용자명을 멤버변수에 세팅
    	  uname = user_name;
      }
      
      int row = 0;
      int column = 0;
      
      // 행/열 값 세팅 (3~6)
      while(true) {
         while(true) {
            try {
               row = Integer.parseInt(JOptionPane.showInputDialog("행[3~6]"));
               if(2 < row && row < 7) break;
            } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(null, "숫자가 아니다요!!");
            }
         }
         
         while(true) {
            try {
               column = Integer.parseInt(JOptionPane.showInputDialog("열[3~6]"));
               if(2 < column && column < 7) break;
            } catch (NumberFormatException e) {
               JOptionPane.showMessageDialog(null, "숫자가 아니다요!!");
            }
         }
         
         if((row * column) % 2 == 0) {
        	 break;
         } else {
        	 JOptionPane.showMessageDialog(null, "행과 열의 곱이 짝수여야 한다요!!!");
         }
      }
      
      game_type = row + "X" + column; 
      
      makeNorth(uname);
	  makeCenter(row, column);
	  
      this.setVisible(true);
   }
   
   /**
    * 사용자명 / 점수 / 연속콤보 / 점수보기팝업 / 테마변경팝업
	* @param user_name
	*/
   public void makeNorth(String user_name) {
	   JPanel p_north = new JPanel();
	   p_north.setLayout(new GridLayout(1, 3));
	      
	   JLabel lb_name = new JLabel(String.format("사용자명: %s", user_name));
	   p_north.add(lb_name);
	   
	   JPanel p_north_score = new JPanel();
	   p_north_score.setLayout(new GridLayout(2, 1));
	   lb_score = new JLabel(String.format("점수: %d", total_score));
	   lb_combo = new JLabel(String.format("연속콤보: %d", continuously_cnt));
	   p_north_score.add(lb_score);
	   p_north_score.add(lb_combo);
	   p_north.add(p_north_score);
	      
	   JPanel p_north_btn = new JPanel();
	   p_north_btn.setLayout(new GridLayout(1, 1));
	   btn_all_score = new JButton("점수보기");
	   btn_all_score.addActionListener(new ButtonAction());
	   p_north_btn.add(btn_all_score);
	   p_north.add(p_north_btn);
	      
	   this.add(p_north, BorderLayout.NORTH);
   }
   
   /**
    * 카드 세팅
	* @param row 행
	* @param column 열
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
		if(turnup_cnt == 2) { // 1번카드
			obj1 = (JButton)e.getSource();
			val1 = Integer.parseInt(((JComponent) e.getSource()).getName());
			setIconImage(obj1, val1);
		} else if (turnup_cnt == 1) { // 2번카드
			obj2 = (JButton)e.getSource();
			val2 = Integer.parseInt(((JComponent) e.getSource()).getName());
			if(obj1 != obj2) {
				setIconImage(obj2, val2);
			} else {
				turnup_cnt = 2;
			}
		} else if (turnup_cnt == 0) { // 1번-2번의 결과 (3번째클릭)
			turnup_cnt = 3;
			if(val1 != val2) { // 뒤집은 카드의 두 값이 같지 않을때
				setIconImage(obj1, 99);
				setIconImage(obj2, 99);
				fail_cnt++;
				continuously = 0;
				continuously_cnt = 0;
				lb_combo.setText(String.format("연속콤보: %d", continuously_cnt));
			} else { // 뒤집은 카드의 두 값이 같을때
				if(obj1 != obj2) { // 한장의 카드를 연속 3번누르기 방지
					obj1.removeActionListener(this); // disable임에도 클릭event가 존재하므로 listener 제거
					obj2.removeActionListener(this);
					obj1.setEnabled(false);
					obj2.setEnabled(false);
					total_score += 100; // 
					end_point--;
					success_cnt++;

					if(success_cnt > 0 && continuously > 0) { // 연속으로 맞춘 경우
						continuously_cnt++;
						if(success_cnt == continuously) {
							total_score += continuously_cnt*20;
						}
					}
 
					lb_score.setText(String.format("점수: %d", total_score));
					lb_combo.setText(String.format("연속콤보: %d", continuously_cnt));
 
					continuously = success_cnt;
					success_cnt = 0;

					// 카드의 짝을 모두 맞춘 경우
					if(end_point == 0) {
						// 리더보드 DB저장
						dao.insertLeaderBoard(uname, game_type, total_score);
						//게임종료메시지
						int ret = JOptionPane.showConfirmDialog(null, 
								String.format("終わりました！！\n최종점수:: %d \n다시 시작하시겠습니까?!", total_score), 
								"카드 짝 맞추기!!", JOptionPane.YES_NO_OPTION);
						if(ret != 0) {
							System.exit(0); // main종료
						} else {
							// 재시작시
							this.setVisible(false); // 현재창 닫기
							new SinglePlayer(uname); // 현재 사용자명과 함께 생성자 재호출
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
    * @param obj 카드 object
    * @param val 해당 카드 object의 name
    */
   public void setIconImage(JButton obj, int val) {
	   obj.setIcon(null); // 현재 카드가 가지고 있는 이미지 제거
	   obj.setIcon(new ImageIcon(map_icon.get(val))); // 새로운 이미지 세팅
   }
   
   public static void main(String[] args) {
	   new SinglePlayer("1111111");
   }
   
}