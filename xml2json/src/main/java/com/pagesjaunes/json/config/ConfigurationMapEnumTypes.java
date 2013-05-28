/**
 *
 */
package com.pagesjaunes.json.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author pagesjaunes
 * 
 */
public class ConfigurationMapEnumTypes{

	protected static final Logger LOG = Logger.getLogger(ConfigurationMapEnumTypes.class);

	private Map<String, JsonConf> properties = new HashMap<String, JsonConf>();

	public ConfigurationMapEnumTypes(String pFilename){
		try {
			this.loadProperties(pFilename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void loadProperties(String filename)
			throws IOException, FileNotFoundException {
		
		Properties tProp= new Properties();
		
//		FileInputStream input = new FileInputStream(filename);
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(filename);
		
		try {
			tProp.load(input);
		} finally {
			input.close();
		}
		
		Set<Object> keys = tProp.keySet();
		for (Object key : keys) {
			String value = (String) tProp.get(key);
			JsonConf jsonConf = new JsonConf();
			jsonConf.setTypes(Types.UNKNOW);
			try {
				String[] values = value.split("\\|");
				if (values.length == 1
						&& Types.valueOf(values[0].toUpperCase()) != Types.DATE) {
					jsonConf.setTypes(Types.valueOf(values[0].toUpperCase()));
				} else if (values.length == 2
						&& Types.valueOf(values[0].toUpperCase()) == Types.DATE) {
					jsonConf.setTypes(Types.valueOf(values[0].toUpperCase()));
					jsonConf.setFormat(values[1]);
				} else {
					LOG.warn("Error loading the key "
							+ key
							+ " with value "
							+ value
							+ ". A datatype DATE description must contains a pattern declaration (before the '|' caracter). '|' is forbidden for other types.");
				}
			} catch (Exception ignore) {
				LOG.warn("Error loading the key "
						+ key
						+ " with value " + value, ignore);
			}
			getProperties().put((String) key, jsonConf);
		}		
	}

	public Map<String, JsonConf> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, JsonConf> properties) {
		this.properties = properties;
	}

}
