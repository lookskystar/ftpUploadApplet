package com.image.applet.ui;

import java.awt.Dimension;

import javax.swing.JApplet;

import com.image.applet.util.AppletParam;
import com.image.applet.util.UOperate;
/**
 * 主界面
 * @author Administrator
 *
 */
public class AppletMain extends JApplet {

	private static final long serialVersionUID = -2617155745803108084L;

	private static AppletParam param = AppletParam.getInstance();
	
	@Override
	public void init() {
		param.setApp(this);
		super.init();
	}

	@Override
	public void start() {
		super.start();
		setSize(400, 300);
		
		//打开界面同步时间
		UOperate uOperate = new UOperate();
		String uRootPath = uOperate.findURootPath();
		if(uRootPath!=null && !"".equals(uRootPath)){
			uOperate.syncTime(uRootPath);
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.applet.Applet#destroy()
	 */
	@Override
	public void destroy() {
		
		//打开界面同步时间
		UOperate uOperate = new UOperate();
		String uRootPath = uOperate.findURootPath();
		if(uRootPath!=null && !"".equals(uRootPath)){
			uOperate.syncTime(uRootPath);
		}
		
		super.destroy();
	}

	/**
	 *  创建界面
	 */
	public void createAndShowGUI() {
		UIManager manager = new UIManager(this);
		setJMenuBar(manager.createMenuBar());
		setContentPane(manager.createContentPane());
		setMinimumSize(new Dimension(400, 300));
		
		setVisible(true);
	}

}
