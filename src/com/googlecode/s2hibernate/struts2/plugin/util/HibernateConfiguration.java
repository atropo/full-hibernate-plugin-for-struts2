package com.googlecode.s2hibernate.struts2.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;

public class HibernateConfiguration {
	String fileName;
	Properties properties;
	
	/**
	 * @param fileName
	 * @param properties
	 */
	public HibernateConfiguration(String fileName, Properties properties) {
		super();
		this.fileName = fileName;
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		String out = fileName+": "+properties; 
		return out;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public TreeMap<Object, String> getSortedProperties() {
		TreeMap<Object, String> map = new TreeMap<Object, String>();
		for (Object key:properties.keySet()) {
			map.put(key, properties.getProperty((String) key));
		}
		return map;
	}
	
	public String getContent() throws IOException {
		String content = "";
		try {
			InputStream is = this.getClass().getResourceAsStream(fileName);
			content = IOUtils.toString(is);
		} catch (IOException e) {
			content=fileName+": "+e.getMessage();
			e.printStackTrace();
			throw new IOException(content);
		}
		content = content.replace("<", "&lt;").replace(">", "&gt;");
		content = content.replace(SystemUtils.LINE_SEPARATOR, "<br/>");
		return content;
	}
}
