package com.image.applet.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

public class UOperate {
	
	/**
	 * 查询图像采集仪U盘
	 */
	public String findURootPath(){
		FileSystemView sys = FileSystemView.getFileSystemView();
		File[] files = File.listRoots(); //循环盘符
		File file = null;
		String path = null;
        for(int i = 0; i < files.length; i++) { 
            if(sys.getSystemDisplayName(files[i]).contains(UConst.ROOT_NAME)){
            	file = files[i];
            	break;
            }
        }
        if(file!=null){
        	path = file.getPath();
        }
//path = "F:\\test";
        return path;
	}
	
	/**
	 * 查找标识文件
	 * DEVICE,001,000002;
	 */
	public String findDEVCID(){
		String uRootPath = findURootPath();
		try{
			FileReader fr = new FileReader(uRootPath+File.separator+UConst.DEVICENM_FILE_NAME);
			BufferedReader br=new BufferedReader(fr);
			String line = br.readLine();         
			br.close(); 
			fr.close();
			line = line.substring(line.lastIndexOf(",")+1, line.indexOf(";")).trim();
			return line;
		}catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 清空图像采集仪的文件
	 */
	public boolean clear(String uRootPath){
		try{
			File root = new File(uRootPath);
			File[] files = root.listFiles();
			for (int i = 0; i < files.length; i++) {
				if(files[i].isDirectory()){
					deleteDir(files[i]);                
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 递归删除目录
	 */
    private boolean deleteDir(File dir) throws Exception{        
    	if (dir.isDirectory()) {            
    		String[] children = dir.list();//递归删除目录中的子目录下            
    		for (int i=0; i<children.length; i++) {                
    			boolean success = deleteDir(new File(dir, children[i]));                
    			if (!success) {                    
    				return false;                
    			}            
    		}        
    	} // 目录此时为空，可以删除        
    	return dir.delete();    
    }
    
    /**
     * 插入时间文件
     */
    public boolean syncTime(String uRootPath) {
    	SimpleDateFormat format = new java.text.SimpleDateFormat(UConst.TIME_KEY);
    	try{
    		File file = new File(uRootPath+File.separator+UConst.TIME_FILE_NAME);
    		String result = UConst.TIME_STR.replace(UConst.TIME_KEY, format.format(new Date()));
    		if(!file.exists()){
    			file.createNewFile();
    		}
    		FileWriter fwriter = new FileWriter(file);
    		fwriter.write(result);
    		fwriter.flush();
    		fwriter.close();
        	return true;
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * 查询采集仪一级目录
     */
    public File[] listRootFile(String uRootPath){
    	File root = new File(uRootPath);
    	return root.listFiles();
    }
}
