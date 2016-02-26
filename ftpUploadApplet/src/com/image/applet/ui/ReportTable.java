package com.image.applet.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;

import org.json.JSONArray;
import org.json.JSONObject;

import com.image.applet.util.AppletParam;
import com.image.applet.util.FileCounter;
import com.image.applet.util.MulFileUtil;
import com.image.applet.util.UOperate;
/**
 * 统计报表
 * @author Administrator
 *
 */
public class ReportTable {

	/**
	 * 表格列字段名称
	 */
	private String[] totalColumnNames = {"日期","机车","工序","工次","作业者","图片数量","视频数量","视频总时长"};
	private String[] detailColumnNames = {"序号","机车", "工序","工次","作业者","时间","文件","类型","视频时长","状态"};
	private AppletParam param = AppletParam.getInstance();
	
	private static SimpleDateFormat YMDHMS_SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static SimpleDateFormat YMD_SDF = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat HMS_SDF = new SimpleDateFormat("hhmmss");
	
	JTabbedPane tabbedPane = null;
	
	public ReportTable(){
		
		JFrame frame = new JFrame("信息统计汇总");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
		frame.setContentPane(createTablePanel());
		frame.setMinimumSize(new Dimension(800, 400));
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * 创建内容面板
	 */
	private JTabbedPane createTablePanel(){
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		List<Object[][]> list = totalFileInfo(new UOperate().findURootPath());//获取表格数据
		Object[][] total = null;
		Object[][] detail = null;
		if(list!=null && list.size()>0){
			total = list.get(0);
			detail = list.get(1);
		}
		tabbedPane.add("新增汇总",new JScrollPane(createTotalTable(total)));
		tabbedPane.add("详情列表",new JScrollPane(createDetailTable(detail)));
		return tabbedPane;
	}
	
	/**
	 * 创建汇总表格
	 */
	private JTable createTotalTable(Object[][] rows){
		TableModel model = new DefaultTableModel(rows, totalColumnNames);  
		JTable table = new JTable(model);
		table.setEnabled(false);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);  
		table.setRowSorter(sorter);  
		return table;
	}
	
	
	/**
	 * 创建详细表格
	 */
	private JTable createDetailTable(Object[][] rows) {
		TableModel model = new DefaultTableModel(rows, detailColumnNames);  
		JTable table = new JTable(model);
		table.setEnabled(false);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);  
		table.setRowSorter(sorter);  
		table.getColumnModel().getColumn(9).setCellRenderer(new MyTableCellRenderer());
		return table;
	}
	
	/**
	 * 统计文件
	 * 1、解析本地文件的头文件 获取机车、工号、工序、时长等信息
	 * 2、请求服务器,获取显示信息
	 * 3、解析获取的字符串，并拼装成表格展示形式
	 */
	private List<Object[][]> totalFileInfo(String path) {
		List<File> fileList = FileCounter.listFile(new File(path), new ArrayList<File>());//获取本地文件
		
		JSONArray array = new JSONArray();
		JSONObject json = null;
		String tempStr = null;
		String[] tempArr = null;
		String type = null;
		Date date = null;
//		//解析本地文件
		for (File file : fileList) {
			try{
				json = new JSONObject();
				if(file.getName().matches(".*\\.((?i)jpg)$") || file.getName().matches(".*\\.((?i)avi)$")){
					date = new Date(file.lastModified());
					json.put("area_id", param.areaid);
					json.put("local_url", file.getAbsolutePath());
					json.put("take_time", YMDHMS_SDF.format(date));
					if(file.getName().matches(".*\\.((?i)jpg)$")){ // 匹配jpg格式的文件
						tempStr = MulFileUtil.getImageInfo(file).get("software");  //SQ909_5530900991_10000 0111 22 0003
						type = ".JPG";
						
						json.put("type", "0");
						json.put("video_time", "0.0");
					}else if(file.getName().matches(".*\\.((?i)avi)$")){ // 匹配avi格式的文件
						tempStr = MulFileUtil.getFileHeadInfo(file);
						type = ".AVI";
						
						json.put("type", "1");
						json.put("video_time", MulFileUtil.getVideoTime(file)+"");
					}
					tempArr = tempStr.split("_");
					//年月日/工号/车型车号+工序+工序次数/地区编码_手电编码_时分秒.avi
					json.put("server_url", "/"+YMD_SDF.format(date)+"/"+tempArr[2].substring(0,5)+"/"+tempArr[2].substring(5)+"/"+param.areaid+"_"+tempArr[1]+"_"+HMS_SDF.format(date)+type);
					json.put("take_gh", tempArr[2].substring(0,5));//工号 5位
					json.put("jc_num", tempArr[2].substring(5,9));//车号 4位
					json.put("pro_num", tempArr[2].substring(9,11));//工序2位
					json.put("pro_rank", tempArr[2].substring(11));//工次4位
					array.put(json);
				}
			}catch (Exception e) {
				continue;
			}
		}
		
		//请求服务器
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(param.getDateUrl);
	    NameValuePair valuePair = new NameValuePair("jsonStr",array.toString());
	    post.setRequestBody(new NameValuePair[] {valuePair});
	    String result = "";
	    try {
			client.executeMethod(post);
			result = post.getResponseBodyAsString();
	    }catch(Exception e) {
			e.printStackTrace();
		}
	    
		//解析字符串
		try {
			List<Object[][]> list = new ArrayList<Object[][]>();
			Object[][] total = null;
			Object[][] detail = null;
			
			JSONArray resultArray = new JSONArray(result);
			Map<String,List<Object>> map = new HashMap<String, List<Object>>();
			String jc = null;
			String proNum = null;
			String proName = null;
			String proRank = null;
			String takeName = null;
			String takeTime = null;
			double videoTime = 0.0;
			String takeDate = null;
			
			
			if(!resultArray.isNull(0)){
				JSONObject obj = null;
				detail = new Object[resultArray.length()][detailColumnNames.length];
				List<Object> tempList = null;
				for(int i=0;i<resultArray.length();i++){
					obj=resultArray.optJSONObject(i);
					
					jc = obj.optString("jc_type")+"-"+obj.optString("jc_num");
					proNum = obj.optString("pro_num");
					proName = obj.optString("pro_name");
					proRank = obj.optString("pro_rank");
					takeName = obj.optString("take_name");
					takeTime = obj.optString("take_time");
					videoTime = obj.optDouble("video_time", 0.0);
					takeDate = takeTime.substring(0, 10);
					
					if(obj.optInt("status")!=1){
						takeDate = takeTime.substring(0, 10);
						//"日期","机车","工序","工次","作业者","图片数量","视频数量","视频总时长"
						tempList = map.get(takeDate+"-"+jc+"-"+proNum+"-"+proRank);
						if(tempList==null){
							tempList = new ArrayList<Object>();
							map.put(takeDate+"-"+jc+"-"+proNum+"-"+proRank, tempList);
						}
						tempList.add(0, takeDate);
						tempList.add(1, jc);
						tempList.add(2, proName);
						tempList.add(3, proRank);
						tempList.add(4, takeName);
						if(obj.optInt("type")==0){
							tempList.add(5, extractNum(0,tempList.size()<6?null:tempList.get(5),1));
							tempList.add(6, extractNum(0,tempList.size()<7?null:tempList.get(6),0));
							tempList.add(7, extractNum(1,tempList.size()<8?null:tempList.get(7),0));
						}else{
							tempList.add(5, extractNum(0,tempList.size()<6?null:tempList.get(5),0));
							tempList.add(6, extractNum(0,tempList.size()<7?null:tempList.get(6),1));
							tempList.add(7, extractNum(1,tempList.size()<8?null:tempList.get(7),videoTime));
						}
						
					}
					//{"序号","机车", "工序","工次","作业者","时间","文件","类型","视频时长","状态"};
					detail[i][0] = i+1;
					detail[i][1] = jc;
					detail[i][2] = proName;
					detail[i][3] = proRank;
					detail[i][4] = takeName;
					detail[i][5] = takeTime;
					detail[i][6] = obj.optString("local_url").replaceAll("/", "//");
					detail[i][7] = obj.optInt("type")==0?"图片":"视频";
					detail[i][8] = videoTime;
					int status=obj.optInt("status");
					switch(status){
					case 0:
						detail[i][9] ="新增";
						break;
					case 1:
						detail[i][9] ="已存在";
						break;
					case 2:
						detail[i][9] ="工号错误";
						break;
					case 3:
						detail[i][9] ="机车号错误";
						break;
					case 4:
						detail[i][9] ="工序错误";
						break;
					default:
						break;
					}
		         }
				
				total = new Object[map.size()][totalColumnNames.length];
				int i = 0;
				for(List<Object> temp : map.values()){
					total[i] = temp.toArray();
					i++;
				}
				list.add(total);
				list.add(detail);
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("serial")
	private class MyTableCellRenderer extends DefaultTableCellRenderer{
		
		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			String data=(String)value;
			if(data.equals("新增")){
				setBackground(Color.GREEN);
			}else if(data.equals("工号错误")||data.equals("机车号错误")||data.equals("工序错误")){
				setBackground(Color.RED);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
					row, column);
		}
	}
	
	/**
	 * 数值转换
	 * type : 0: int类型   1:double 类型
	 * obj 原始变量
	 * addValue 增加值
	 */
	private Object extractNum(int type,Object obj,Object addValue){
		if(obj==null){
			obj = 0;
		}
		if(type==0){
			return Integer.parseInt(String.valueOf(obj))+Integer.parseInt(String.valueOf(addValue));
		}else{
			
			return Double.parseDouble(String.valueOf(obj))+Double.parseDouble(String.valueOf(addValue));
		}
	}
}
