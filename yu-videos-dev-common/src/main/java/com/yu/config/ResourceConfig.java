package com.yu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
//@Configuration
@ConfigurationProperties(prefix="com.yu")
@PropertySource("classpath:resource-prod.properties")
public class ResourceConfig {

	private String zookeeperServer;
	private String bgmServer;
	private String fileSpace;
	
	private String ffmpegPath;
	
	private String directory;
	
	private String split;

	public String getZookeeperServer() {
		return zookeeperServer;
	}
	public void setZookeeperServer(String zookeeperServer) {
		this.zookeeperServer = zookeeperServer;
	}
	public String getBgmServer() {
		return bgmServer;
	}
	public void setBgmServer(String bgmServer) {
		this.bgmServer = bgmServer;
	}
	public String getFileSpace() {
		return fileSpace;
	}
	public void setFileSpace(String fileSpace) {
		this.fileSpace = fileSpace;
	}
	public String getFfmpegPath() {
		return ffmpegPath;
	}
	public void setFfmpegPath(String ffmpegPath) {
		this.ffmpegPath = ffmpegPath;
	}
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getSplit() {
		return split;
	}
	public void setSplit(String split) {
		this.split = split;
	}
	
}
