package org.itmasters.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
	private static Properties properties = new Properties();
	
	static {
		try {
			properties.load(new FileInputStream("config/config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Properties getProperties() {
		return properties;
	}
}
