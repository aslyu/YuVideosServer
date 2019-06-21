package com.yu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yu.mapper.BgmMapper;
import com.yu.pojo.Bgm;
import com.yu.service.BgmService;


@Service
public class BgmServiceImpl implements BgmService {
	
	@Autowired
	private BgmMapper bgmMapper;
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		return bgmMapper.selectAll();
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Bgm queryBgm(String bgmId) {
		
		return bgmMapper.selectByPrimaryKey(bgmId);
	}



}
