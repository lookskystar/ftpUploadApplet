package com.image.applet.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.image.applet.ftp.FTPFileUploadThread;
import com.image.applet.util.AppletParam;
import com.image.applet.util.UOperate;

public class UIManager implements ItemListener, ActionListener {
	
	private AppletParam param = AppletParam.getInstance();
	private UOperate uOperate = new UOperate();

	private Map<String, JComponent> compMap = new HashMap<String, JComponent>();
	
	private Component component;
	
	public UIManager() {
		super();
	}

	public UIManager(Component component) {
		super();
		this.component = component;
	}

	public JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("选择（S）");
		menu.setName("selectFileButton");
		menu.setToolTipText("打开文件选择窗口，选择要上传的一个或多个文件（目录）。");
		registerComponent(menu.getName(), menu);
		menu.setMnemonic(KeyEvent.VK_S);
		menu.addItemListener(this);
		menuBar.add(menu);

		menu = new JMenu("删除（D）");
		menu.setName("deleteFileButton");
		menu.setToolTipText("删除选中的文件，点击后将直接删除。");
		registerComponent(menu.getName(), menu);
		menu.setMnemonic(KeyEvent.VK_D);
		menu.addItemListener(this);
		menu.setEnabled(false);
		menuBar.add(menu);

		return menuBar;
	}

	public JPanel createContentPane() {
		JPanel panel = new JPanel();
		panel.setOpaque(true); 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(createTable());

		panel.add(scrollPane);
		panel.add(createProgressBar());
		panel.add(createControlPanel());

		return panel;
	}
	
	/**
	 * 创建表格
	 */
	private JTable createTable() {

		JTable table = new FileInfoDropTargetTable();
		table.setName("fileInfoTable");
		// 监听是否有选中的数据，如果有选中的数据，删除按钮才可用
		table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getSource() instanceof FileInfoTableModel) {
					FileInfoTableModel model = (FileInfoTableModel) e
							.getSource();
					
					boolean hasSelectedRow = model.hasSelectedRow();
					
					getComponentByName("deleteFileButton").setEnabled(hasSelectedRow);
					
					getComponentByName("controlButton").setEnabled(model.getRowCount() > 0);
					
					getComponentByName("autoBtn").setEnabled(model.getRowCount() < 0);
				}
			}
		});
		registerComponent(table.getName(), table);

		return table;
	}

	/**
	 * 创建进度条
	 * @return
	 */
	private JPanel createProgressBar() {
		JPanel barPanel = new JPanel(new GridLayout(1, 1));

		JProgressBar bar = new JProgressBar();
//		bar.setValue(100);
		bar.setString("文件上传中,请等待...");
		bar.setStringPainted(false);
		bar.setName("progressBar");
		bar.setIndeterminate(false);

		
		registerComponent(bar.getName(), bar);

//		bar.setString(String.format("当前进度：%d%%.", bar.getValue()));

		barPanel.add(bar);

		return barPanel;
	}
	
	private static final String UPLOAD_TEXT = "手动上传(U)";
	private static final String CANCEL_TEXT = "取消(Q)";
	private static final String AUTO_TEXT = "自动上传(A)";
//	private static final String SYNC_TEXT = "同步时间(T)";
	private static final String CLEAR_TEXT = "清空文件(C)";
	private static final String TOTAL_TEXT = "文件统计(F)";

	/**
	 * 创建文件列表面板
	 * @return
	 */
	private JPanel createControlPanel() {

		JPanel ctrlPanel = new JPanel();
		
		JButton autoBtn = new JButton(AUTO_TEXT);
		autoBtn.setName("autoBtn");
		autoBtn.setMnemonic(KeyEvent.VK_A);
		registerComponent(autoBtn.getName(), autoBtn);
		ctrlPanel.add(autoBtn);
		autoBtn.addActionListener(this);
		

		JButton controlBtn = new JButton(UPLOAD_TEXT);
		controlBtn.setName("controlButton");
		controlBtn.setEnabled(false);
		controlBtn.setToolTipText("点击上传列表中所有文件。");
		controlBtn.setMnemonic(KeyEvent.VK_U);
		registerComponent(controlBtn.getName(), controlBtn);
		ctrlPanel.add(controlBtn);
		controlBtn.addActionListener(this);
		
//		JButton syncBtn = new JButton(SYNC_TEXT);
//		syncBtn.setName("syncBtn");
//		syncBtn.setMnemonic(KeyEvent.VK_T);
//		registerComponent(syncBtn.getName(), syncBtn);
//		ctrlPanel.add(syncBtn);
//		syncBtn.addActionListener(this);
		
		JButton clearBtn = new JButton(CLEAR_TEXT);
		clearBtn.setName("clearBtn");
		clearBtn.setMnemonic(KeyEvent.VK_C);
		registerComponent(clearBtn.getName(), clearBtn);
		ctrlPanel.add(clearBtn);
		clearBtn.addActionListener(this);
		
		JButton countBtn = new JButton(TOTAL_TEXT);
		countBtn.setName("totalButton");
		countBtn.setMnemonic(KeyEvent.VK_F);
		registerComponent(countBtn.getName(), countBtn);
		ctrlPanel.add(countBtn);
		countBtn.addActionListener(this);

		return ctrlPanel;
	}
	
	public void switchControlButton(JButton btn) {
		
		if (UPLOAD_TEXT.equals(btn.getText())) {
			btn.setText(CANCEL_TEXT);
			btn.setMnemonic(KeyEvent.VK_Q);
			btn.setToolTipText("停止上传。");
		} else {
			btn.setText(UPLOAD_TEXT);
			btn.setMnemonic(KeyEvent.VK_U);
			btn.setToolTipText("点击上传列表中所有文件。");
		}
	}
	
	/**
	 * Open a file chooser.
	 */
	private void openFileChooser() {
		String uRootPath = uOperate.findURootPath();
//		if(uRootPath==null || "".equals(uRootPath)){
//			JOptionPane.showMessageDialog(component, "请插入图像采集仪!", "提示框", JOptionPane.ERROR_MESSAGE);
//		}else{
			JFileChooser chooser = new JFileChooser(uRootPath);
			if (param.useFileExtension()) {
				String extension = param.fileNameExtension.trim();
	
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"可上传的文件格式：" + extension, param.getFileNameExtensionArray());
	
				chooser.setAcceptAllFileFilterUsed(param.acceptAllFileFilterUsed);
				chooser.setFileFilter(filter);
			}
	
			if (param.isDirectorySelectionEnabled && param.isFileSelectionEnabled) {
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			} else if (param.isDirectorySelectionEnabled) {
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			} else {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			}
	
			chooser.setFileHidingEnabled(param.isFileHidingEnabled);
			chooser.setMultiSelectionEnabled(param.isMultiSelectionEnabled);
			
			ImagePreviewPanel preview = new ImagePreviewPanel();//缩略图显示
            chooser.setAccessory(preview);
            chooser.addPropertyChangeListener(preview);
	
			int returnVal = chooser.showOpenDialog(component);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File[] files = chooser.getSelectedFiles();
				JTable table = (JTable) getComponentByName("fileInfoTable");
				FileInfoTableModel model = (FileInfoTableModel) table.getModel();
				for (File file : files) {
					model.addFile(file);
				}
			}
//		}
	}
	
	public void registerComponent(String name, JComponent comp) {
		compMap.put(name, comp);
	}

	public JComponent getComponentByName(String name) {
		return compMap.get(name);
	}
	
	/******************************** 事件处理区域 ***************************/

	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getItem() instanceof JMenu) {
			JMenu menu = (JMenu) e.getItem();

			if (e.getStateChange() == ItemEvent.SELECTED) {
				if ("selectFileButton".equals(menu.getName())) { // 选择文件
					openFileChooser();
				} else if ("deleteFileButton".equals(menu.getName())) {
					JTable table = (JTable) getComponentByName("fileInfoTable");
					FileInfoTableModel model = (FileInfoTableModel) table
							.getModel();
					model.removeFiles();
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String uRootPath = uOperate.findURootPath();
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();
			if((uRootPath==null || "".equals(uRootPath))&& !"controlButton".equals(btn.getName())){
				JOptionPane.showMessageDialog(component, "请插入图像采集仪!", "提示框", JOptionPane.ERROR_MESSAGE);
			}else{
				if("autoBtn".equals(btn.getName())){  //自动上传
					File[] files = uOperate.listRootFile(uRootPath);
					JTable table = (JTable) getComponentByName("fileInfoTable");
					FileInfoTableModel model = (FileInfoTableModel) table.getModel();
					for (File file : files) {
						model.addFile(file);
					}
					uploadFiles(1);
				}
	
				if ("controlButton".equals(btn.getName())) {
					if (UPLOAD_TEXT.equals(e.getActionCommand())) { //手动上传
						switchControlButton(btn);
						uploadFiles(0);
					} else { // 停止
						switchControlButton(btn);
						cancelUpload();
					}
				}
				
				if("syncBtn".equals(btn.getName())){ //服务器同步时间
					boolean result = new UOperate().syncTime(uRootPath);
					if(result){
						JOptionPane.showMessageDialog(component, "同步时间成功!", "提示框", JOptionPane.INFORMATION_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(component, "同步时间异常,请稍后再试!", "提示框", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				if("clearBtn".equals(btn.getName())){ //清空采集仪
					boolean result = new UOperate().clear(uRootPath);
					if(result){
						JOptionPane.showMessageDialog(component, "删除成功!", "提示框", JOptionPane.INFORMATION_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(component, "删除异常,请手动删除文件!", "提示框", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				if("totalButton".equals(btn.getName())){//查询统计
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new ReportTable();
						}
					});
					
				}
			}
		}
	}
	
	private FTPFileUploadThread th;
	/**
	 * 调用上传接口
	 */
	private void uploadFiles(Integer type) {
		th = new FTPFileUploadThread(this,type);
		th.start();
	}
	
	/**
	 * 停止上传
	 */
	private void cancelUpload() {
		if (th != null) {
			th.cancel();
		}
		getComponentByName("selectFileButton").setEnabled(true);
		getComponentByName("fileInfoTable").setEnabled(true);
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

}
