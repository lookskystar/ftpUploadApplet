package com.image.applet.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.image.applet.util.UOperate;

public class MyApplet {

	/**
	 * Applet主界面
	 */
	public static void createAndShowGUI() {

		JFrame frame = new JFrame("FTP上传文件");
		
		UIManager manager = new UIManager(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(manager.createMenuBar());
		frame.setContentPane(manager.createContentPane());
		frame.setMinimumSize(new Dimension(400, 300));

		frame.pack();
		
		//打开界面同步时间
		UOperate uOperate = new UOperate();
		String uRootPath = uOperate.findURootPath();
		if(uRootPath!=null && !"".equals(uRootPath)){
			uOperate.syncTime(uRootPath);
		}
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
}
