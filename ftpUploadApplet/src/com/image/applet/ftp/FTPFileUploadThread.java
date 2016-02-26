package com.image.applet.ftp;

import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import com.image.applet.ui.FileInfoTableModel;
import com.image.applet.ui.UIManager;
import com.image.applet.util.AppletParam;
import com.image.applet.util.FTPConst;

public class FTPFileUploadThread extends Thread {
	
	private AppletParam param = AppletParam.getInstance();
	
	private UIManager manager;
	private Integer type;//0：手动上传 1：自动上传
	
	public FTPFileUploadThread(UIManager manager,Integer type) {
		super();
		this.manager = manager;
		this.type = type;
		if (this.manager == null) {
			throw new RuntimeException("构造FTPFileUploadThread(UIManager manager)的参数不能为空！");
		}
	}
	
	private FTPTransfer ftp = null;

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		JComponent selBtn = manager.getComponentByName("selectFileButton");
		selBtn.setEnabled(false);
		
		JProgressBar bar = (JProgressBar) manager.getComponentByName("progressBar");
		
		JTable table = (JTable)manager.getComponentByName("fileInfoTable");
		FileInfoTableModel model = (FileInfoTableModel) table.getModel();
		table.setEnabled(false);
		
		Object[][] data = model.getData();
		
		FTPConst.SUCCESS_VIDEO_COUNT = 0; //成功视频计数器
		FTPConst.SUCCESS_IMG_COUNT = 0; //成功图片计数器
		FTPConst.ERROR_GH_VIDEO_COUNT = 0; //工号错误视频数
		FTPConst.ERROR_GH_IMG_COUNT = 0; //工号错误图片数
		FTPConst.ERROR_CH_VIDEO_COUNT = 0; //车号错误视频     
		FTPConst.ERROR_CH_IMG_COUNT = 0; //车号错误 图片
		FTPConst.ERROR_GX_VIDEO_COUNT =0 ; //工序错误视频数      
		FTPConst.ERROR_GX_IMG_COUNT = 0; //工序错误图片数
		FTPConst.FILEINFO_MAP = new HashMap<String,Integer>();
		
		ftp = new FTPTransfer(true);
		try {
			
			ftp.connectAndLogin(param.host, param.port, param.userName, param.password);
//			ftp.addCopyStreamAdaptor(new FTPCopyStreamAdaptor(bar));
			
			JButton btn = (JButton)manager.getComponentByName("controlButton");
			JButton abtn = (JButton)manager.getComponentByName("autoBtn");
//			JButton sbtn = (JButton)manager.getComponentByName("syncBtn");
			JButton cbtn = (JButton)manager.getComponentByName("clearBtn");
			abtn.setEnabled(false);
//			sbtn.setEnabled(false);
			cbtn.setEnabled(false);
			if(type==1){
				btn.setEnabled(false);
			}
			
			long start = System.currentTimeMillis();
			
			while (data.length > 0) {
//				bar.setValue(0);
//				bar.setString(String.format("当前进度：%d%%.", bar.getValue()));
				bar.setStringPainted(true);
				bar.setIndeterminate(true);
				ftp.uploadFile((String)data[0][5]);
				model.removeFile(0);
				data = model.getData();
			}
			
			btn.setEnabled(false);
			if(type!=1){
				manager.switchControlButton(btn);
			}
			
			selBtn.setEnabled(true);
			table.setEnabled(true);
			
			abtn.setEnabled(true);
//			sbtn.setEnabled(true);
			cbtn.setEnabled(true);
			
			ftp.removeCopyStreamAdaptor();
			bar.setIndeterminate(false);
			bar.setStringPainted(false);
			
			long end = System.currentTimeMillis();
			NumberFormat format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			format.setMinimumFractionDigits(2);
			
			JOptionPane.showMessageDialog(table, "上传结束!  耗时约 "+format.format((end-start)/60000.00)+" 分\n"
						+"上传成功    视频：" + FTPConst.SUCCESS_VIDEO_COUNT + "      图片:" + FTPConst.SUCCESS_IMG_COUNT +"\n"
						+"工号错误    视频：" + FTPConst.ERROR_GH_VIDEO_COUNT + "      图片:" + FTPConst.ERROR_GH_IMG_COUNT + "\n"
						+"车号错误    视频：" + FTPConst.ERROR_CH_VIDEO_COUNT + "      图片:" + FTPConst.ERROR_CH_IMG_COUNT + "\n"
						+"工序错误    视频：" + FTPConst.ERROR_GX_VIDEO_COUNT + "      图片:" + FTPConst.ERROR_GX_IMG_COUNT + "\n"
					, "提示信息", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(table, e.getMessage(), "提示信息", JOptionPane.ERROR_MESSAGE);
		} finally {
			FTPConst.FILEINFO_MAP = null;
			try {
				ftp.logoutAndDisconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void cancel() {
		try {
			ftp.disconnect();
		} catch (FTPTransferException e) {
			e.printStackTrace();
		}
	}

}
