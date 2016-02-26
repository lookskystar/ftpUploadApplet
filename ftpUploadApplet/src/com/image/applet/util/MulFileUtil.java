package com.image.applet.util;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * 处理视频与图片信息
 * @author Administrator
 *
 */
public class MulFileUtil {
	
	/**
	 * 获取图片头文件信息
	 */
	public static Map<String,String> getImageInfo(File file){
		Map<String,String> map = new HashMap<String, String>();
		if(file!=null){
			try{
				Metadata metadata = JpegMetadataReader.readMetadata(file);
				ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);
				map.put("software", exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_SOFTWARE));
				ExifSubIFDDirectory subIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
				map.put("time", subIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 获取头文件信息--以文件流的形式读取视频文件，Byte数组获取头文件
	 *   循环数组，转换为char类型，解析得到头文件 
	 * @param filepath
	 * @throws IOException
	 */
	public static String getFileHeadInfo(File file){
		try{
		  FileInputStream fis =  new FileInputStream(file);
		  byte[] b = new byte[300];
		  StringBuilder sb = new StringBuilder();
		  fis.read(b,0,300);  
		  for (int i = 0; i < b.length; i++) {
			  sb.append((char)(Integer.parseInt(b[i]+"")));
		  }
		  fis.close();
		  
		  String temp = sb.toString().toUpperCase();
		  int offset = sb.indexOf("SQ");
		  return temp.substring(offset, offset+50).trim();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取视频时长
	 */
	public static double getVideoTime(File file){
		double time = 0.0;
		Encoder encoder = new Encoder();
        try {
             MultimediaInfo m = encoder.getInfo(file);
             long ls = m.getDuration();
             time = Double.parseDouble(new DecimalFormat("#.00").format(ls/(1000*60.0)));
             return time;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
	}
	
	/**
	 * 获取文件创建时间
	 * @param file
	 * @return
	 */
    public static String getFileCreateDate(File file) {
		try {
			String filePath = file.getAbsolutePath();
			Process ls_proc = Runtime.getRuntime().exec("cmd.exe /c dir " + filePath + " /tc");
			BufferedReader br = new BufferedReader(new InputStreamReader(ls_proc.getInputStream()));
			for (int i = 0; i < 5; i++) {
				br.readLine();
			}
			String result = br.readLine();
			br.close();
			String[] array = result.split("\\s+");
			String date = array[0].replaceAll("/", "-");
			String time = array[1];
			return date.concat(" ").concat(time);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		try{
//			long b = System.currentTimeMillis();
			String result;
			result = getFileHeadInfo(new File("F:\\test\\151751.AVI"));
			System.out.println(result);
//			result = getFileCreateDate(new File("F:\\test\\11112233\\3.AVI"));
//			System.out.println(result);
			Map<String,String> map = getImageInfo(new File("F:\\test\\120618.JPG"));
			System.out.println(map.get("software")+"--"+map.get("time"));
//			long e = System.currentTimeMillis();
//			System.out.println((e-b));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
