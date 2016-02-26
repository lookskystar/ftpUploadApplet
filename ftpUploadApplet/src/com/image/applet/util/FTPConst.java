package com.image.applet.util;

import java.util.Map;

public final class FTPConst {
	/**
	 * 连接服务器异常
	 */
	public static final int ERROR_CONNECTSERVER = 0x010101;
	
	/**
	 * 断开服务器异常
	 */
	public static final int ERROR_DISCONNECTSERVER = 0x010102;
	
	
	
	/**
	 * 登录服务器异常
	 */
	public static final int ERROR_LOGINSERVER = 0x010201;
	
	/**
	 * 登出服务器异常
	 */
	public static final int ERROR_LOGOUTSERVER = 0x010202;
	
	
	
	/**
	 * 创建目录异常
	 */
	public static final int ERROR_DIRECTORY_CREATE = 0x020101;
	
	/**
	 * 删除目录异常
	 */
	public static final int ERROR_DIRECTORY_DELETE = 0x020102;
	
	/**
	 * 切换目录异常
	 */
	public static final int ERROR_DIRECTORY_CHANGE = 0x020103;
	
	
	
	/**
	 * 删除文件异常
	 */
	public static final int ERROR_FILE_DELETE = 0x030101;
	
	/**
	 * 文件上传异常
	 */
	public static final int ERROR_UPLOAD_FILE = 0x040101;
	
	/**
	 * 其他未知异常
	 */
	public static final int ERROR_OTHER = 0x010000;
	
	
	/**
	 * 文件存在
	 */
	public static final int STATE_FILE_EXITS = 0x090101;
	
	/**
	 * 远程文件大小大于本地文件大小
	 */
	public static final int STATE_REMOTE_BIGGERTHAN_LOCAL = 0x090102;
	
	/**
	 * 从断点处继续续传
	 */
	public static final int STATE_UPLOAD_FROM_BREAK = 0x090103;
	
	/**
	 * 文件上传成功
	 */
	public static final int STATE_UPLOAD_NEWFILE_SUCCESS = 0x090104;
	
	
	public static int SUCCESS_VIDEO_COUNT = 0;//成功视频数
	public static int SUCCESS_IMG_COUNT = 0;//成功图片数
	public static int ERROR_GH_VIDEO_COUNT = 0; //工号错误视频数
	public static int ERROR_GH_IMG_COUNT = 0; //工号错误图片数
	public static int ERROR_CH_VIDEO_COUNT = 0; //车号错误视频数
	public static int ERROR_CH_IMG_COUNT = 0; //车号错误图片数
	public static int ERROR_GX_VIDEO_COUNT = 0; //工序错误视频数
	public static int ERROR_GX_IMG_COUNT = 0; //工序错误图片数
	
	/**
	 * 工号错误
	 */
	public static final int ERROR_GONGHAO = 1;
	/**
	 * 车号错误
	 */
	public static final int ERROR_CHEHAO = 2;
	/**
	 * 工序错误
	 */
	public static final int ERROR_GONGXU = 3;
	
	/**
	 * 存放文件临时路径
	 */
	public static Map<String,Integer> FILEINFO_MAP = null;
	
	
	public static final String getMessageByCode(int code) {
		String msg = "";
		switch (code) {
		case ERROR_CONNECTSERVER:
			msg = "服务器连接异常！";
			break;
		case ERROR_DISCONNECTSERVER:
			msg = "断开服务器连接异常！";
			break;
		case ERROR_LOGINSERVER:
			msg = "登录服务器异常！";
			break;
		case ERROR_LOGOUTSERVER:
			msg = "登出服务器异常！";
			break;
		case ERROR_DIRECTORY_CREATE:
			msg = "创建目录异常！";
			break;
		case ERROR_DIRECTORY_DELETE:
			msg = "删除目录异常！";
			break;
		case ERROR_DIRECTORY_CHANGE:
			msg = "切换目录异常！";
			break;
		case ERROR_FILE_DELETE:
			msg = "删除文件异常！";
			break;
		case ERROR_UPLOAD_FILE:
			msg = "文件上传异常！";
			break;
		case ERROR_OTHER:
			msg = "其它未知异常！";
			break;
		}
		return msg;
	}
}
