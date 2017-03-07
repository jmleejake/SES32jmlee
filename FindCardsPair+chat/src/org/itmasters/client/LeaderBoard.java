package org.itmasters.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.itmasters.manager.CardPairDAO;
import org.itmasters.manager.ConnectionManager;

/**
 * @author no-chan-hyuk
 * @since 16.12.08
 */
public class LeaderBoard extends JFrame implements ActionListener {
	private JTable table_lb_center;
	private DefaultTableModel dtm;
	private JScrollPane scrollPane_lb;
	
	private JButton button_lb_south_name;
	private JButton button_lb_south_type;
	private JButton button_lb_south_all;
	
	private CardPairDAO dao;
	
	public LeaderBoard(){
		this.setSize(700,500);
		this.dispose();
		JLabel label_lb_north = new JLabel("Leader Board");
		Font f = new Font("�ü�", Font.BOLD, 35);  //���� ��Ʈ
		label_lb_north.setFont(f);
		label_lb_north.setHorizontalAlignment(JLabel.CENTER);
		this.add(label_lb_north, BorderLayout.NORTH);
		
		scrollPane_lb = new JScrollPane();
		this.add(scrollPane_lb, BorderLayout.CENTER);
		table_lb_center = new JTable();
		table_lb_center.setFont(new Font("Serif", Font.BOLD, 15)); //Į�� ��Ʈ
		dtm = new DefaultTableModel(new Object[]{"����", "�̸�", "Ÿ��", "����", "��¥"}, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTableHeader header = table_lb_center.getTableHeader();
		header.setPreferredSize(new Dimension(20, 40));  //��� ĭ ũ��
		header.setFont(new Font("SansSerif", Font.BOLD, 20));  //��� ��Ʈ
		table_lb_center.setModel(dtm);
		scrollPane_lb.setViewportView(table_lb_center);
		table_lb_center.setRowHeight(55);
		
		dao = new CardPairDAO();
		
		printLeaderBoard(0, null);
		
		JPanel panel_lb_south = new JPanel();
		panel_lb_south.setPreferredSize(new Dimension(30, 40));
		panel_lb_south.setLayout(new BoxLayout(panel_lb_south, BoxLayout.X_AXIS));
		this.add(panel_lb_south, BorderLayout.SOUTH);
		panel_lb_south.setLayout(new GridLayout(1, 3));
		button_lb_south_name = new JButton("�̸����� �˻�");
		button_lb_south_name.setFont(new Font("SansSerif", Font.BOLD, 12));
		button_lb_south_name.addActionListener(this);
		panel_lb_south.add(button_lb_south_name);
		button_lb_south_type = new JButton("Ÿ������ �˻�");
		button_lb_south_type.setFont(new Font("SansSerif", Font.BOLD, 12));
		button_lb_south_type.addActionListener(this);
		panel_lb_south.add(button_lb_south_type);
		button_lb_south_all = new JButton("��ü����");
		button_lb_south_all.setFont(new Font("SansSerif", Font.BOLD, 12));
		button_lb_south_all.addActionListener(this);
		panel_lb_south.add(button_lb_south_all);
		
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		switch (s) {
		case "�̸����� �˻�":
			String name = JOptionPane.showInputDialog("�̸��� �Է��ϼ��� ");
			printLeaderBoard(1, name);
			break;
		case "Ÿ������ �˻�":
			while(true){
				String type = JOptionPane.showInputDialog("Ÿ���� �Է��ϼ���").toUpperCase();
				if(type != null && (type.equals("3X4") || type.equals("4X3") || type.equals("4X4") || type.equals("5X4") || 
						type.equals("4X5") || type.equals("5X6") || type.equals("6X5") || type.equals("6X6"))){
					printLeaderBoard(2, type);
					break;
				} else{
					JOptionPane.showMessageDialog(null, "3x4 ~ 6x6 ���̸� �Է��ϼ���");
				}
			}
			break;
		case "��ü����":
			printLeaderBoard(0, null);
			break;
		}
	}
	
	/**
	 *  �����������!!
	 * @param gbn ���а� (0: ��ü 1: �̸��˻� 2: Ÿ�԰˻�)
	 * @param val gbn�� ���� �� 1: �̸� 2: Ÿ��
	 */
	public void printLeaderBoard(int gbn, String val) {
		ArrayList<HashMap<String, Object>> list = dao.selectLeaderBoard(gbn, val);
		
		dtm.setNumRows(0);
		for (HashMap<String, Object> map : list) {
			int rownum = (int)map.get("grade");
			String name = (String)map.get("user_name");
			String type = (String)map.get("type");
			int score = (int)map.get("score");
			String date = (String)map.get("today");
			
			Object rowData[] = {rownum, name, type, score, date};
			dtm.addRow(rowData);
		}
	}
}
