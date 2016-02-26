package com.image.applet.ui;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class FileInfoDropTargetTable extends JTable implements
		DropTargetListener {

	private static final long serialVersionUID = -4954196816239038823L;

	public FileInfoDropTargetTable() {
		init();
		setDropTarget(new DropTarget(this, this));
	}

	public FileInfoDropTargetTable(TableModel dm) {
		super(dm);
		setDropTarget(new DropTarget(this, this));
	}

	/**
	 * 对表格进行一些设置， 包括：设置表头字段名称、表格数据模型、对表头做宽度调整、表格大小调整以及选择模式的设置
	 */
	protected void init() {
		String[] columnNames = { "操作", "文件名", "类型", "图片数量","视频数量"};
		FileInfoTableModel model = new FileInfoTableModel(columnNames);
		setModel(model);

		TableColumn column = getColumn(columnNames[0]);
		column.setPreferredWidth(60);
		column = getColumn(columnNames[1]);
		column.setPreferredWidth(200);
		column = getColumn(columnNames[2]);
		column.setPreferredWidth(100);
		column = getColumn(columnNames[3]);
		column.setPreferredWidth(120);
		column = getColumn(columnNames[4]);
//		column.setPreferredWidth(120);
//		column.setCellRenderer(new FileSizeRenderer());

		setPreferredScrollableViewportSize(new Dimension(500, 240));
		setFillsViewportHeight(true);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		String tip = null;
		java.awt.Point p = event.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);

		if (rowIndex != -1 && colIndex != -1) {
			int realColumnIndex = convertColumnIndexToModel(colIndex);
			System.err.println(rowIndex + " " + colIndex);
			if (realColumnIndex == 1) { // Sport column
				tip = "原文件名: " + getValueAt(rowIndex, colIndex);
			} else if (realColumnIndex == 2) { // Veggie column
				tip = "上传文件名: " + getValueAt(rowIndex, colIndex);
			} else if (realColumnIndex == 4) {
				tip = "文件大小：" + getValueAt(rowIndex, colIndex) + "byte";
			} else {
				// You can omit this part if you know you don't
				// have any renderers that supply their own tool
				// tips.
				tip = super.getToolTipText(event);
			}
		}
		return tip;
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		System.out.println("dragEnter......");

		if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			dtde.rejectDrag();
		}
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	@Override
	public void dragExit(DropTargetEvent dte) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {
		Transferable tr = dtde.getTransferable();

		List<File> files = null;
		try {
			if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				files = (List<File>) tr
						.getTransferData(DataFlavor.javaFileListFlavor);

				FileInfoTableModel model = (FileInfoTableModel) getModel();
				int count = model.addFiles(files);
				String msg = "已成功添加" + count + "个文件（目录）至列表中。";
				int type = JOptionPane.INFORMATION_MESSAGE;
				if (files.size() > count) {
					msg += "\n其它" + (files.size() - count) + "个不符合条件，未被加入！";
					type = JOptionPane.WARNING_MESSAGE;
				}
				JOptionPane.showMessageDialog(this, msg, "提示信息", type);
			}
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
