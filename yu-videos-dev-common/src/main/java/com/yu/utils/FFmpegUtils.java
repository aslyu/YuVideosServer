package com.yu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 用于处理音频的工具类
 * @author H8u
 *
 */
public class FFmpegUtils {
	
	
	
//	private String ffmpegPath = "D:\\FFmpeg\\ffmpeg\\bin\\ffmpeg.exe";
	
	/**
	 * 将背景音乐和视频合并
	 * (用FFmpeg的cmd命令为：C:/ffmpeg.exe -i bgm.mp3 -i video.mp4 -t 5 -y newVideo.mp4)
	 * @param bgmPath 源背景音乐路径
	 * @param videoPath	源视频路径
	 * @param videoSeconds 合成后的视频时长
	 * @param finalPath	合成后的路径
	 * @throws IOException
	 */
	public  void mergeVideoBgm(String ffmpegPath,String bgmPath,String videoPath,double videoSeconds,String finalPath) throws IOException {
		List<String> command = new ArrayList<>();
		command.add(ffmpegPath);
		command.add("-i");
		command.add(bgmPath);
		command.add("-i");
		command.add(videoPath);
		command.add("-t");
		command.add(videoSeconds+"");
		command.add("-y");
		command.add(finalPath);
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		
		@SuppressWarnings("unused")
		String line = "";
		while((line = br.readLine()) != null) {
		}
		if(br != null) {
			br.close();
		}
		if(inputStreamReader != null) {
			inputStreamReader.close();
		}
		if(errorStream != null) {
			errorStream.close();
		}
	}
	
	/**
	 * 截取视频某一帧并生成图片
	 * (用FFmpeg的cmd命令为：C:/ffmpeg.exe -ss 00:00:01 -y -i video.mp4 -vframes 1 image.jpg)
	 * @param videoPath 源视频路径
	 * @param imagePath	生成图片的路径
	 * @param imageFormat 图片格式(如jpg,png等)
	 * @return 返回生成图片的路径
	 * @throws IOException
	 */
	public  String fetchVideoCover(String ffmpegPath,String videoPath,String imagePath,String imageFormat) throws IOException {
		imagePath = imagePath.substring(0, imagePath.lastIndexOf(".")+1);
		List<String> command = new ArrayList<>();
		command.add(ffmpegPath);
		command.add("-ss");
		command.add("00:00:01");
		command.add("-y");
		command.add("-i");
		command.add(videoPath);
		command.add("-vframes");
		command.add("1");
		command.add(imagePath+imageFormat);
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		
		InputStream errorStream = process.getErrorStream();
		InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(inputStreamReader);
		
		@SuppressWarnings("unused")
		String line = "";
		while((line = br.readLine()) != null) {
		}
		if(br != null) {
			br.close();
		}
		if(inputStreamReader != null) {
			inputStreamReader.close();
		}
		if(errorStream != null) {
			errorStream.close();
		}
		return imagePath+imageFormat;
	}
	
	public static void main(String[] args) {
	}

}
