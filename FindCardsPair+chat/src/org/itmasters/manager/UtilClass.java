package org.itmasters.manager;

import java.util.HashMap;

public class UtilClass {
	static String theme = null;
	
	static String[] themeArr = 
			new String[]{"animal", "country", "fruit", "country", "animal", "fruit", "animal"};
    
	/**
	 * random�� �̹��� ���� For SinglePlayer
	 * @param max_pair_icon �ִ� ī�� ��
	 * @return button name�� key�������ϴ� �̹����� value�� ������ hashmap
	 */
	public static HashMap<Integer, String> getRandomIconImgForSingle(int max_pair_icon) {
			
		int[] numbers = new int[max_pair_icon];
		int randomNo = (int)(Math.random()*7);
		HashMap<Integer, String> ret = new HashMap<>();
		    
		theme = String.format("icons/%s/%s_", themeArr[randomNo], themeArr[randomNo]);
		    
		while(true) {
			for(int i=0; i<numbers.length; i++) {
				numbers[i] = (int)(Math.random()*40) + 1;
			}
		       
			if(!checkDuplicate(numbers)) {
				for (int i = 0; i < numbers.length; i++) {
					ret.put(i, theme + numbers[i] + ".png");
				}
				return ret;
			}
		}
	}
	
	/**
	 * random�� �̹��� ���� For MultiPlayer
	 * @param max_pair_icon �ִ� ī�� ��
	 * @return button name�� key�������ϴ� �̹����� value�� ������ hashmap
	 */
	public static HashMap<String, String> getRandomIconImgForMulti(int max_pair_icon) {
	    
		int[] numbers = new int[max_pair_icon];
		int randomNo = (int)(Math.random()*7);
		HashMap<String, String> ret = new HashMap<>();
		    
		theme = String.format("icons/%s/%s_", themeArr[randomNo], themeArr[randomNo]);
		    
		while(true) {
			for(int i=0; i<numbers.length; i++) {
				numbers[i] = (int)(Math.random()*40) + 1;
			}
		       
			if(!checkDuplicate(numbers)) {
				for (int i = 0; i < numbers.length; i++) {
					ret.put(i + "-1", theme + numbers[i] + ".png");
				}
		          
				for (int i = 0; i < numbers.length; i++) {
					ret.put(i + "-2", theme + numbers[i] + ".png");
				}
				return ret;
			}
		}
	}
	   
	/**
	 * @param i random�̹��� ������ ���� random number�迭
	 * @return �ߔ�������� �� n��
	 */
	public static boolean checkDuplicate(int i[]) {
		boolean ret = false;
			
		for(int n=0; n<i.length; n++) {
			for(int k=n+1; k<i.length; k++) {
				if(i[n] == i[k]) ret = true;
			}
		}
			
		return ret;
	}
}
