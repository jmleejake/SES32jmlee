package org.itmasters.manager;

import java.util.HashMap;

public class UtilClass {
	static String theme = null;
	
	static String[] themeArr = 
			new String[]{"animal", "country", "fruit", "country", "animal", "fruit", "animal"};
    
	/**
	 * random한 이미지 세팅 For SinglePlayer
	 * @param max_pair_icon 최대 카드 수
	 * @return button name을 key값으로하는 이미지를 value로 세팅한 hashmap
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
	 * random한 이미지 세팅 For MultiPlayer
	 * @param max_pair_icon 최대 카드 수
	 * @return button name을 key값으로하는 이미지를 value로 세팅한 hashmap
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
	 * @param i random이미지 세팅을 위한 random number배열
	 * @return 중뷁되지않은 수 n개
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
