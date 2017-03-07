package org.itmasters.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CardPairDAO {
	private PreparedStatement pstmt;
	
	/**
	 * 1. ���� ȭ�鿡�� ǥ������ ����� �� ��⿩�� ����Ʈ
	 * game_server insert
	 * 2. ���� ȭ�� ��ǥ�� ����
	 * game_server_detail insert
	 * 
	 * @param map �������� HashMap
	 * @return insert ���
	 */
	public int insertGameServer(HashMap<String, Object> map) {
		int ret = 0;
		Connection conn = ConnectionManager.getConnection();
		try {
			String user_name = (String)map.get("user_name");
			String ip = (String)map.get("ip");
			int row = (int)map.get("row");
			int column = (int)map.get("column");
			int game_port = (int)map.get("game_port");
			
			String sql = "INSERT INTO game_server (user_name, type) "
					+ "VALUES (?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_name);
			pstmt.setString(2, row + "X" + column);
			ret = pstmt.executeUpdate();
			if(ret > 0) {
				pstmt.close();
				sql = "INSERT INTO game_server_detail (user_name, ip_add, port_no, row_no, col_no) "
						+ "VALUES (?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, user_name);
				pstmt.setString(2, ip);
				pstmt.setInt(3, game_port);
				pstmt.setInt(4, row);
				pstmt.setInt(5, column);
				
				ret = pstmt.executeUpdate();
				if(ret > 0) {
					pstmt.close();
					game_port++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)
					pstmt.close();
				
				ConnectionManager.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * @param name ����ڸ�
	 * @return game_server, detail�� join �����
	 */
	public HashMap<String, Object> selectGameServer(String name) {
		HashMap<String, Object> ret = new HashMap<>();
		ret.put("user_name", "none");
		Connection conn = ConnectionManager.getConnection();
		ResultSet rs = null;
		
		try {
			String sql = "SELECT gs.user_name, type, wait, ip_add, port_no, row_no, col_no  "
					+ "FROM game_server gs "
					+ "INNER JOIN game_server_detail gsd on gs.user_name = gsd.user_name "
					+ "WHERE gs.user_name = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				
				ret.put("user_name", rs.getString(1));
				ret.put("type", rs.getString(2));
				ret.put("wait", rs.getString(3));
				ret.put("ip", rs.getString(4));
				ret.put("port", rs.getInt(5));
				ret.put("row", rs.getInt(6));
				ret.put("col", rs.getInt(7));
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					if(pstmt != null)
						pstmt.close();
					ConnectionManager.close(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ret;
	}
	
	/**
	 * @return ��������Ʈ
	 */
	public ArrayList<HashMap<String, Object>> selectGameServerList() {
		ArrayList<HashMap<String, Object>> ret = new ArrayList<>();
		Connection conn = ConnectionManager.getConnection();
		ResultSet rs = null;
		
		try {
			String sql = "SELECT gs.user_name, type, ip_add, port_no, row_no, col_no, "
					+ "  CASE wait WHEN 'T' THEN '�����' "
					+ "                 WHEN 'F' THEN '������' "
					+ "                 END wait_status "
					+ "FROM game_server gs "
					+ "INNER JOIN game_server_detail gsd on gs.user_name = gsd.user_name ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				HashMap<String, Object> retMap = new HashMap<>();
				retMap.put("user_name", rs.getString(1));
				retMap.put("type", rs.getString(2));
				retMap.put("ip", rs.getString(3));
				retMap.put("port", rs.getInt(4));
				retMap.put("row", rs.getInt(5));
				retMap.put("col", rs.getInt(6));
				retMap.put("wait", rs.getString(7));
				
				ret.add(retMap);
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					if(pstmt != null)
						pstmt.close();
					ConnectionManager.close(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ret;
	}
	
	/**
	 * ������ ������ ��⿩�� ���¸� �����Ѵ�.
	 * @param user_name ����ڸ�
	 * @return table update���
	 */
	public int updateGameServer(String user_name) {
		int ret = 0;
		Connection conn = ConnectionManager.getConnection();
		
		try {
			String sql = "UPDATE game_server "
					+ "     SET wait = ? "
					+ "     WHERE user_name = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "F");
			pstmt.setString(2, user_name);
			
			ret = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)
					pstmt.close();
				ConnectionManager.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 * @param user_name ����ڸ�
	 * @param game_type ����Ÿ�� (3X4 ~ 6X6)
	 * @param total_score
	 */
	public void insertLeaderBoard(String user_name, String game_type, int total_score) {
		Connection conn = ConnectionManager.getConnection();
		
		try {
			String sql = "INSERT INTO leaderboard (name, type, score) VALUES (?, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_name);
			pstmt.setString(2, game_type);
			pstmt.setInt(3, total_score);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)
					pstmt.close();
				ConnectionManager.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param gbn ���а� (0: ��ü 1: �̸��˻� 2: Ÿ�԰˻�)
	 * @param val gbn�� ���� �� 1: �̸� 2: Ÿ��
	 * @return �������� ��������Ʈ
	 */
	public ArrayList<HashMap<String, Object>> selectLeaderBoard(int gbn, String val) {
		ArrayList<HashMap<String, Object>> ret = new ArrayList<>();
		Connection conn = ConnectionManager.getConnection();
		ResultSet rs = null;
		
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT rownum, res.* ");
			sql.append("FROM ");
			sql.append("( ");
			sql.append("SELECT name, type, score, today ");
			sql.append("FROM leaderboard ");
			if(val != null) {
				if(gbn == 1) {
					sql.append("WHERE name = ? ");
				} else if (gbn == 2) {
					sql.append("WHERE type = ? ");
				}
			}
			sql.append("ORDER BY score DESC");
			sql.append(") res ");
			
			pstmt = conn.prepareStatement(sql.toString());
			if(val != null) {
				pstmt.setString(1, val);
			}
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				HashMap<String, Object> retMap = new HashMap<>();
				retMap.put("grade", rs.getInt(1));
				retMap.put("user_name", rs.getString(2));
				retMap.put("type", rs.getString(3));
				retMap.put("score", rs.getInt(4));
				retMap.put("today", rs.getString(5));
				
				ret.add(retMap);
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
				try {
					if(rs != null)
						rs.close();
					if(pstmt != null)
						pstmt.close();
					ConnectionManager.close(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return ret;
	}
	
	/**
	 * @param user_name ����ڸ�
	 * @return DB���� �����
	 */
	public int deleteGameServer(String user_name) {
		int ret = 0;
		Connection conn = ConnectionManager.getConnection();
      
		try {
			String sql = "DELETE game_server WHERE user_name = ?";
         
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user_name);
         
			ret = pstmt.executeUpdate();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)
					pstmt.close();
				ConnectionManager.close(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
