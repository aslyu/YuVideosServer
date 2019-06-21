package com.yu.mapper;

import com.yu.utils.MyMapper;

import java.util.List;


import com.yu.pojo.Videos;
import com.yu.pojo.vo.CommentsVO;

public interface CommentsMapperCustom extends MyMapper<Videos> {
	
	List<CommentsVO> queryComments(String videoId);
}