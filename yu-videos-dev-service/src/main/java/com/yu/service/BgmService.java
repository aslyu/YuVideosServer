package com.yu.service;

import java.util.List;

import com.yu.pojo.Bgm;

public interface BgmService {
	
	/**
	 * 查询背景音乐列表
	 */
	List<Bgm> queryBgmList();
	
	/**
	 * 根据背景音乐id查询背景音乐
	 */
	Bgm queryBgm(String bgmId);
}
