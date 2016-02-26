package com.image.applet.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计某个文件夹下文件 Java文件  以及
 * 统计指定目录下以及其子目录下的所有java文件中代码行数 注释数 空行数等
 */

public class FileCounter {

    /**
     * 统计文件数
     * @param path 指定路径
     * @return 返回统计数据[图片数,视频数]
     */
    public static long[] countFile(File file){
    	
    	long totalImageCount = 0;  //总图片数目
        long totalVideoCount = 0;//总视频数目
    	
    	
    	long[] result = new long[2];
        //存储文件的数据
        List<File> al = listFile(file,new ArrayList<File>());
        for (File f : al) {
            if(f.getName().matches(".*\\.((?i)jpg)$")){ // 匹配java格式的文件
               totalImageCount++;
            }else if(f.getName().matches(".*\\.((?i)avi)$")){
            	totalVideoCount++;
            }
        }
    	result[0] = totalImageCount;
    	result[1] = totalVideoCount;
    	return result;
    }
    
    /**
     * 列出文件
     * @param args
     */
    public static List<File> listFile(File f,List<File> fileArray) {
       if(f.isFile()){
    	   fileArray.add(f);
       }else{
	       File[] ff = f.listFiles();
	       for(File child : ff){
	           if(child.isDirectory()){
	        	   listFile(child,fileArray);
	           }else{
	              fileArray.add(child);
	           }
	       }
       }
       return fileArray;
    }
    
    public static void main(String[] args) {
    	countFile(new File("F:\\test"));
    	List<File> list = listFile(new File("F:\\test"),new ArrayList<File>());
    	for(File f : list){
    		System.out.println(f.getAbsolutePath());
    	}
	}
 
}
