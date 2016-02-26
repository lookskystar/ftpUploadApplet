package com.image.applet.ftp;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamListener;

import com.image.applet.util.AppletParam;
import com.image.applet.util.FTPConst;
import com.image.applet.util.MulFileUtil;

public class FTPTransfer {

	private AppletParam param = AppletParam.getInstance();
	
	private static SimpleDateFormat YMD_SDF = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat HMS_SDF = new SimpleDateFormat("hhmmss");

	private FTPClient ftp;

	private ProtocolCommandListener listener;
	
	/**
	 * 可上传的文件格式
	 */
	private String[] fileExtension;

	public FTPTransfer() {
		ftp = new FTPClient();
	}

	/**
	 * @param openCommandListener
	 *            是否打开命令执行监听器
	 */
	public FTPTransfer(boolean openCommandListener) {
		ftp = new FTPClient();
		if (openCommandListener) {
			openCommandListerner();
		}
	}

	/**
	 * 打开命令执行监听器
	 */
	public void openCommandListerner() {
		listener = new PrintCommandListener(new PrintWriter(System.out), true);
		ftp.addProtocolCommandListener(listener);
		System.out.println("打开命令执行监听器！");
	}

	/**
	 * 关闭命令执行监听器
	 */
	public void closeCommandListener() {
		if (listener != null) {
			ftp.removeProtocolCommandListener(listener);
			System.out.println("关闭命令执行监听器！");
		}
	}

	public void addCopyStreamAdaptor(FTPCopyStreamAdaptor adaptor) {
		ftp.setCopyStreamListener(adaptor);
	}
	
	public void removeCopyStreamAdaptor() {
		ftp.setCopyStreamListener(null);
	}
	
	public FTPCopyStreamAdaptor getCopyStreamAdaptor() {
		if (getCopyStreamlistener() instanceof FTPCopyStreamAdaptor) {
			return (FTPCopyStreamAdaptor) getCopyStreamlistener();
		} else {
			return null;
		}
	}
	
	
	
	public void addCopyStreamListener(CopyStreamListener listener) {
		ftp.setCopyStreamListener(listener);
	}

	public CopyStreamListener getCopyStreamlistener() {
		return ftp.getCopyStreamListener();
	}

	/**
	 * 和FTP服务器建立连接
	 * 
	 * @param hostname
	 *            访问主机名
	 * @param port
	 *            访问端口
	 * @throws FTPTransferException
	 */
	public boolean connect(String hostname, int port)
			throws FTPTransferException {
		boolean state = false;
		try {
			ftp.connect(hostname, port);
			if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
				state = true;
				System.out.println("打开FTP连接！");

			} else {
				System.out.println("打开FTP连接失败！");
			}
			
		} catch (Exception e) {
			throw new FTPTransferException(FTPConst.ERROR_CONNECTSERVER);
		}

		return state;
	}

	/**
	 * 和FTP服务器建立连接并进行登录
	 * 
	 * @param hostname
	 *            访问主机名
	 * @param port
	 *            访问端口
	 * @param username
	 *            登录用户名
	 * @param password
	 *            登录密码
	 * @return true-成功 false-失败
	 * @throws FTPTransferException
	 */
	public boolean connectAndLogin(String hostname, int port, String username,
			String password) throws FTPTransferException {
		boolean state = false;

		if (connect(hostname, port)) {
			state = login(username, password);
		}

		return state;
	}

	/**
	 * 登录服务器
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws FTPTransferException
	 */
	public boolean login(String username, String password)
			throws FTPTransferException {
		boolean state = false;
		try {
			if (ftp.login(username, password)) {
				state = true;
				System.out.println("登录成功！");
				ftp.setKeepAlive(true);
			} else {
				System.out.println("登录失败！");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPTransferException(FTPConst.ERROR_LOGINSERVER);
		}
		return state;
	}

	/**
	 * 退出FTP登录
	 * 
	 * @return
	 * @throws FTPTransferException
	 */
	public boolean logout() throws FTPTransferException {
		boolean state = false;
		try {
			state = ftp.logout();
			if (state) {
				System.out.println("退出登录！");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPTransferException(FTPConst.ERROR_LOGOUTSERVER);
		}

		return state;
	}

	/**
	 * 退出FTP登录，并断开连接
	 * 
	 * @return
	 * @throws FTPTransferException
	 */
	public boolean logoutAndDisconnect() throws FTPTransferException {
		return logout() & disconnect();
	}

	/**
	 * 断开连接
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean disconnect() throws FTPTransferException {
		boolean state = false;
		try {
			closeCommandListener();
			if (ftp.isConnected()) {
				ftp.disconnect();
				state = true;
				System.out.println("关闭FTP连接！");
			}
		} catch (IOException e) {
			e.printStackTrace();
			state = false;
			throw new FTPTransferException(FTPConst.ERROR_DISCONNECTSERVER);
		}
		return state;
	}
	
	/**
	 * 上传文件
	 * @param local 本地文件路径
	 * @return
	 * @throws FTPTransferException
	 */
	public int uploadFile(String local) throws FTPTransferException {
		return uploadFile(local, null);
	}

	/**
	 * 上传文件
	 * 
	 * @param local
	 *            本地文件路径
	 * @param remote
	 *            服务端文件保存名称
	 * @return
	 * @throws FTPTransferException
	 */
	public int uploadFile(String local, String remote)
			throws FTPTransferException {
		System.out.println("上传文件。。。。。。。。。。。。。。。。。。。。。。。。。。");
		System.out.println("服务端文件保存路径：" + remote);
		int state = 0;

		try {

			ftp.enterLocalPassiveMode(); // 设置PassiveMode传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // 设置以二进制流的方式传输
			String fileName = remote;

			if (remote != null && remote.contains("/")) { // 如果包含文件路径分隔符，则创建对应的目录结构
				fileName = remote.substring(remote.lastIndexOf("/") + 1);
				System.out.println("服务端当前目录：" + ftp.printWorkingDirectory());
				if (fileName.contains(".")) {
					mkdirs(remote.substring(0, remote.lastIndexOf("/")));
				} else {
					fileName = "";
					mkdirs(remote);
				}
			}
			state = upload(local, fileName);

		} catch (IOException e) {
			e.printStackTrace();

		}
		System.out.println("上传文件结束。。。。。。。。。。。。。。。。。。。。。。。。。。");
		return state;
	}
	
	/**
	 * 上传多个文件
	 * @param fileInfos {{localPath, remotePath}, ...}
	 */
	public void uploadFiles(String[][] fileInfos) throws FTPTransferException {
		for (String[] info : fileInfos) {
			uploadFile(info[0], info[1]);
		}
	}

	/**
	 * 对远程文件目录进行创建
	 * 
	 * @param remote
	 */
	private void mkdirs(String remote) throws FTPTransferException {
		if (remote == null || remote.trim().length() == 0) {
			return;
		}

		try {
			String dir = remote; // 要创建的目录名称
			String subDir = "";
			if (remote.contains("/")) {
				int index = remote.indexOf("/");
				dir = remote.substring(0, index);
				subDir = remote.substring(index + 1);
			}
			dir = gbk2iso(dir);
			if (dir.length() > 0) {
				if (!ftp.changeWorkingDirectory(dir)) { // 切换到指定目录，如果无此目录则创建。
					if (ftp.makeDirectory(dir)) {
						System.out.println("创建目录：" + ftp.printWorkingDirectory() + dir);
						if (ftp.changeWorkingDirectory(dir)) { // 切换至创建的目录
							System.out.println("切换工作目录至：" + ftp.printWorkingDirectory());
						} else {
							System.out.println("切换至工作目录" + ftp.printWorkingDirectory()
									+ "失败！");
							throw new FTPTransferException(
									FTPConst.ERROR_DIRECTORY_CHANGE);
						}
					}
				} else {
					System.out.println("切换工作目录至：" + ftp.printWorkingDirectory());
				}
			}

			mkdirs(subDir);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始上传文件
	 * 
	 * @param local
	 *            本地文件路径
	 * @param fileName
	 *            服务端文件保存名称
	 * @return 返回状态
	 */
	private int upload(String local, String fileName)
			throws FTPTransferException {
		
		File file = new File(local);
		if (file.isDirectory()) {
			return uploadDirectory(local, fileName);
		} else {
			return uploadFile2(local, fileName);
		}
	}
	
	/**
	 * 上传目录
	 * @param local
	 * @param remote
	 * @return
	 * @throws FTPTransferException
	 */
	private int uploadDirectory(String local, String remote) throws FTPTransferException {
		File file = new File(local);
//		if (remote != null && remote.trim().length() > 0) {
//			mkdirs(remote);
//		} else {
//			mkdirs(file.getName());
//		}
		
		File[] files = file.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() || fileExtension == null || fileExtension.length == 0) {
					return true;
				}
				String fileName = pathname.getName();
				int index = fileName.lastIndexOf(".");
				if (index > 0) {
					String suffix = fileName.substring(index + 1);
					
					for (String ext : fileExtension) {
						if (suffix.toLowerCase().equals(ext)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		
		if (files != null && files.length > 0) {
			for (File f : files) {
				if (f.isDirectory()) {
					uploadDirectory(f.getAbsolutePath(), null);
				} else {
					uploadFile2(f.getAbsolutePath());
				}
			}
		}
		
		try {
			ftp.changeToParentDirectory();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPTransferException(FTPConst.ERROR_DIRECTORY_CHANGE);
		}
		
		return FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS;
	}
	
	private int uploadFile2(String local) throws FTPTransferException {
		return uploadFile2(local, null);
	}
	
	private int uploadFile2(String local, String fileName) throws FTPTransferException {
		File file = new File(local);
		
		//图片处理
		String info = "";
		String type = "";
		String[] tempArr = null;
		if(file.getName().matches(".*\\.((?i)jpg)$")){
			info = MulFileUtil.getImageInfo(file).get("software");
			type = ".JPG";
		}else if(file.getName().matches(".*\\.((?i)avi)$")){
			info = MulFileUtil.getFileHeadInfo(file);
			type = ".AVI";
		}else{
			return FTPConst.ERROR_UPLOAD_FILE;
		}
		tempArr = info.split("_");
		Date date = new Date(file.lastModified()); 
		String remote = "/"+YMD_SDF.format(date)+"/"+tempArr[2].substring(0,5)+"/"+tempArr[2].substring(5)+"/";
		try {
			ftp.changeWorkingDirectory("/");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPTransferException(FTPConst.ERROR_DIRECTORY_CHANGE);
		}
		mkdirs(remote);
		
		String fname = null;
		if (fileName == null || fileName.trim().length() == 0) {
			System.out.println("未指定上传到服务器后的文件名称。");
			fname = local.substring(local.lastIndexOf(File.separator) + 1);
		} else {
			fname = fileName;
		}

		System.out.println("开始上传文件" + fname);
		
		fname = param.areaid+"_"+tempArr[1]+"_"+HMS_SDF.format(date)+type;
		

		int result = FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS;

		InputStream is = null;
		OutputStream os = null;
		
		FTPCopyStreamAdaptor adaptor = getCopyStreamAdaptor();
		if (adaptor != null) {
			adaptor.setCurrFile(file);
		}
		
		try {
			long start = System.currentTimeMillis();
			System.err.println(ftp.printWorkingDirectory());
			FTPFile[] files = ftp.listFiles(fname);
			
			result = validate(local);//验证工号、车号、工序
			
			if(result==0){
				if (files.length == 1) { // 检查远程是否存在文件
					System.err.println("服务端已存在文件：" + fname);
					long remoteSize = files[0].getSize();

					long localSize = file.length();

					if (remoteSize == localSize) {
						System.out.println("该文件已经完整的存在于服务端。");
						return FTPConst.STATE_FILE_EXITS;
					} else if (remoteSize > localSize) {
						System.out.println("服务端的文件大小大于本地文件的大小。");
						return FTPConst.STATE_REMOTE_BIGGERTHAN_LOCAL;
					}

					// 尝试移动文件内读取指针,实现断点续传
					is = new FileInputStream(file);
					if (is.skip(remoteSize) == remoteSize) {
						if (adaptor != null) {
							adaptor.setTransferred(remoteSize);
						}
						ftp.setRestartOffset(remoteSize);
						if (ftp.storeFile(gbk2iso(fname), is)) {
							System.out.println("断点续传完成。");
							return FTPConst.STATE_UPLOAD_FROM_BREAK;
						}
					}

					// 如果断点续传没有成功，则删除服务器上文件，重新上传
					if (!ftp.deleteFile(fname)) {
						System.out.println("删除服务端文件" + fname + "失败！");
						return FTPConst.ERROR_FILE_DELETE;
					}
					is = new FileInputStream(file);
					
					
					if (ftp.storeFile(gbk2iso(fname), is)) {
						System.out.println("上传文件成功！");
						result = FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS;
					} else {
						System.out.println("上传文件失败！");
						result = FTPConst.ERROR_UPLOAD_FILE;
					}
				} else {
					is = new FileInputStream(local);
					
					result = FTPConst.ERROR_UPLOAD_FILE;
					
					if (ftp.storeFile(gbk2iso(fname), is)) {
						System.out.println("上传文件成功！");
						result = FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS;
					} else {
						System.out.println("上传文件失败！");
						result = FTPConst.ERROR_UPLOAD_FILE;
					}
				}
			}
			long end = System.currentTimeMillis();
			countResult(result,fname);//统计上传结果
			
			System.out.println("耗时："+(end-start)/1000+"秒!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPTransferException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("文件" + fileName + "上传结束");

		return result;
	}
	
	private String gbk2iso(String s) {
		try {
			return new String(s.getBytes("GBK"), "ISO-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * 根据文件路径，截取工号、车号、工序，并依次进行判断
	 */
	private int validate(String filePath){
		try{
			String[] arr = filePath.split(File.separator+File.separator);
			String gh = arr[2];
			String ch = arr[3].substring(0,4);
			String gx = arr[3].substring(4,6);
			int result = ajaxValidate("gh",gh);
			if(result==0){
				result = ajaxValidate("ch",ch);
				if(result==0){
					result = ajaxValidate("gx",gx);
				}
			}
			return result;
		}catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * 远程请求判断是否存在
	 */
	private int ajaxValidate(String name,String value) throws Exception {
//param.validateUrl = "http://localhost:8080/image/ftpValidate!validate.do";
		try{
			int result = 0;
			if(FTPConst.FILEINFO_MAP.get(name+"_"+value)==null){
				
				HttpClient client = new HttpClient();
				PostMethod post = new PostMethod(param.validateUrl);
			    NameValuePair valuePair = new NameValuePair(name,value);
			    post.setRequestBody(new NameValuePair[] {valuePair});
			    client.executeMethod(post);
			    
			    result = Integer.parseInt(post.getResponseBodyAsString());
			    post.releaseConnection();//释放连接
				FTPConst.FILEINFO_MAP.put(name+"_"+value,result);
			}else{
				result = FTPConst.FILEINFO_MAP.get(name+"_"+value);
			}
			return result;
		}catch(Exception e){
			return 0;
		}
	}
	
	private void countResult(int result,String fname){
		boolean flag = "AVI".equalsIgnoreCase(fname.substring(fname.lastIndexOf(".")+1));
		if(flag){//视频统计
			switch (result) {
				case FTPConst.ERROR_GONGHAO :
					FTPConst.ERROR_GH_VIDEO_COUNT++;
					break;
				case FTPConst.ERROR_CHEHAO :
					FTPConst.ERROR_CH_VIDEO_COUNT++;
					break;
				case FTPConst.ERROR_GONGXU :
					FTPConst.ERROR_GX_VIDEO_COUNT++;
					break;
				case FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS:
					FTPConst.SUCCESS_VIDEO_COUNT++;
					break;
			}
		}else{//图片统计
			switch (result) {
				case FTPConst.ERROR_GONGHAO :
					FTPConst.ERROR_GH_IMG_COUNT++;
					break;
				case FTPConst.ERROR_CHEHAO :
					FTPConst.ERROR_CH_IMG_COUNT++;
					break;
				case FTPConst.ERROR_GONGXU :
					FTPConst.ERROR_GX_IMG_COUNT++;
					break;
				case FTPConst.STATE_UPLOAD_NEWFILE_SUCCESS:
					FTPConst.SUCCESS_IMG_COUNT++;
					break;
			}
		}
	}

	/**
	 * @return the fileExtension
	 */
	public String[] getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	public void setFileExtension(String[] fileExtension) {
		this.fileExtension = fileExtension;
	}

}
