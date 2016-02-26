package com.image.applet.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;

public class AppletParam {
	
	public String host ="192.168.1.254"; // FTP地址
	public int port = 21; // 端口
	public String userName = "image"; //"anonymous"; // 用户名
	public String password = "123456"; // 密码

	public boolean acceptAllFileFilterUsed = false; // 是否在选择文件时，使用“所有文件”
	public boolean isDirectorySelectionEnabled = true; // 目录是否可选
	public boolean isFileSelectionEnabled = true; // 文件是否可选
	public String fileNameExtension = "avi,jpg"; // 可上传文件扩展名
	public boolean isFileHidingEnabled = true; // 是否显示隐藏文件
	public boolean isMultiSelectionEnabled = true; // 是否可多选文件
	public long maxFileSize = 8 * 1024 * 1024 * 1024L; //8GB
	public String areaid = "1";
	
	public String validateUrl = "http://localhost:8080/image/uploadAction!ajaxUploadInfo.do";//判断上传信息URL
	
	public String getDateUrl = "http://localhost:8089/image/queryAction!ajaxAddJSONData.do";//ajax获取数据
	
	// 该类的唯一实例
	private static AppletParam instance;
	
	private JApplet app;

	private AppletParam() {
		super();
	}

	/**
	 * 获取该参数类单例
	 * 
	 * @return
	 */
	public static AppletParam getInstance() {
		if (instance == null) {
			instance = new AppletParam();
		}
		return instance;
	}

	/**
	 * 获取参数
	 * 
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		return app.getParameter(key);
	}
	
	/**
	 * 获取long型参数
	 * @param key
	 * @return
	 */
	public Long getLongParam(String key) {
		String value = getParam(key);
		if (value != null && value.length() > 0) {
			return Long.parseLong(value);
		}
		return null;
	}
	
	/**
	 * 获取boolean型参数
	 * @param key
	 * @return
	 */
	public Boolean getBooleanParam(String key) {
		String value = getParam(key);
		if (value != null && value.length() > 0) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}
	
	/**
	 * 获取integer型参数
	 * @param key
	 * @return
	 */
	public Integer getIntegerParam(String key) {
		String value = getParam(key);
		if (value != null && value.length() > 0) {
			return Integer.parseInt(value);
		}
		return null;
	}
	
	/**
	 * 获取double型参数
	 * @param key
	 * @return
	 */
	public Double getDoubleParam(String key) {
		String value = getParam(key);
		if (value != null && value.length() > 0) {
			return Double.parseDouble(value);
		}
		return null;
	}

	/**
	 * 是否使用文件扩展名
	 * 
	 * @return
	 */
	public boolean useFileExtension() {
		return fileNameExtension != null
				&& fileNameExtension.trim().length() > 0;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return
	 */
	public List<String> getFileNameExtension() {
		String extension = fileNameExtension.trim();
		List<String> suffixList = new ArrayList<String>();

		String[] suffixs = extension.split(",");

		String suf = null;
		for (String suffix : suffixs) {
			suf = suffix.trim();
			if (suf.length() > 0) {
				suffixList.add(suf.toLowerCase());
			}
		}
		return suffixList;
	}

	public String[] getFileNameExtensionArray() {
		List<String> list = getFileNameExtension();
		return list.toArray(new String[list.size()]);
	}

	/**
	 * @return the app
	 */
	public JApplet getApp() {
		return app;
	}

	/**
	 * @param app the app to set
	 */
	public void setApp(JApplet app) {
		this.app = app;
		if (this.app != null) {
			initParam();
		}
	}
	
	/**
	 * 初始化一些参数
	 */
	private void initParam() {
		Boolean bValue = getBooleanParam("acceptAllFileFilterUsed");
		if (bValue != null) {
			acceptAllFileFilterUsed = bValue;
		}
		
		bValue = getBooleanParam("isDirectorySelectionEnabled");
		if (bValue != null) {
			isDirectorySelectionEnabled = bValue;
		}
		
		bValue = getBooleanParam("isFileSelectionEnabled");
		if (bValue != null) {
			isFileSelectionEnabled = bValue;
		}
		
		String sValue = getParam("fileNameExtension");
		if (sValue != null) {
			fileNameExtension = sValue;
		}
		
		bValue = getBooleanParam("isFileHidingEnabled");
		if (bValue != null) {
			isFileHidingEnabled = bValue;
		}
		
		bValue = getBooleanParam("isMultiSelectionEnabled");
		if (bValue != null) {
			isMultiSelectionEnabled = bValue;
		}
		
		Long lValue = getLongParam("maxFileSize");
		if (lValue != null) {
			maxFileSize = lValue;
		}
		
		host = getParam("host");
		Integer iValue = getIntegerParam("port");
		if (iValue != null) {
			port = iValue;
		}
		
		sValue = getParam("userName");
		if (sValue != null) {
			userName = sValue;
		}
		
		sValue = getParam("password");
		if (sValue != null) {
			password = sValue;
		}
		
		areaid = getParam("areaid");
		
		validateUrl = getParam("validateUrl");
		
		getDateUrl = getParam("getDateUrl");
	}
}
