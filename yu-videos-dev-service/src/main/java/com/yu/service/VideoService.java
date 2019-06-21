package com.yu.service;


import java.util.List;

import com.yu.pojo.Comments;
import com.yu.pojo.Videos;
import com.yu.utils.PagedResult;

public interface VideoService {
	
	/**
	 * 保存视频到数据库
	 */
	String saveVideo(Videos video);
	
	/**
	 * 修改视频封面
	 * @param videoId
	 * @param coverPath
	 */
	void updateVideo(String videoId,String coverPath);
	
	/**
	 * 分页查询视频列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	PagedResult getAllVideos(Videos video,Integer idSaveRecord,Integer page, Integer pageSize);
	
	/**
	 * 获取热搜词列表
	 */
	List<String> getHotWords();
	
	void userLikeVideo(String userId,String videoId,String videoCreaterId);
	
	void userUnLikeVideo(String userId,String videoId,String videoCreaterId);

	/**
	 * 根据用户Id查询收藏视频列表
	 * @param video
	 * @param page
	 * @param pageSize
	 * @return
	 */
	PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

	PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize);
	
	/**
	 * 保存留言
	 * @param comment
	 */
	void saveComment(Comments comment);
	
	/**
	 * 获取留言列表
	 * @param videoId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
	
	
}
