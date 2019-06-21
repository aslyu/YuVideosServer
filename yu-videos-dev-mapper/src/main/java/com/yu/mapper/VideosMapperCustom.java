package com.yu.mapper;

import com.yu.utils.MyMapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yu.pojo.Videos;
import com.yu.pojo.vo.VideosVO;

public interface VideosMapperCustom extends MyMapper<Videos> {
	
	List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
								  @Param("userId") String userId);
	
	void addVideoLikeCount(String videoId);
	
	void reduceVideoLikeCount(String videoId);

	List<VideosVO> queryMyLikeVideos(String userId);

	List<VideosVO> queryMyFollowVideos(String userId);
}