package com.image.applet.ftp;

import java.io.File;

import javax.swing.JProgressBar;

import org.apache.commons.net.io.CopyStreamAdapter;

public class FTPCopyStreamAdaptor extends CopyStreamAdapter {
	
	private File currFile;
	
	private long transferred;
	
	private JProgressBar bar;
	
	public FTPCopyStreamAdaptor(JProgressBar bar) {
		super();
		this.bar = bar;
	}

	@Override
	public void bytesTransferred(long transferredBytes, int arg1, long arg2) {
		File file = getCurrFile();
		if (file != null) {
			
			int percent = (int) ((transferred + transferredBytes) * 100 / file.length());
			if (bar.getValue() != percent) {
				bar.setValue(percent);
				bar.setString("文件：" + file.getName() + String.format(" 当前进度：%d%%.", bar.getValue()));
			}
		}
	}

	/**
	 * @return the currFile
	 */
	public File getCurrFile() {
		return currFile;
	}

	/**
	 * @param currFile the currFile to set
	 */
	public void setCurrFile(File currFile) {
		this.currFile = currFile;
	}

	/**
	 * @return the bar
	 */
	public JProgressBar getBar() {
		return bar;
	}

	/**
	 * @param bar the bar to set
	 */
	public void setBar(JProgressBar bar) {
		this.bar = bar;
	}

	/**
	 * @return the transferred
	 */
	public long getTransferred() {
		return transferred;
	}

	/**
	 * @param transferred the transferred to set
	 */
	public void setTransferred(long transferred) {
		this.transferred = transferred;
	}

}
