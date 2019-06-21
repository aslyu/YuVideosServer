package com.yu.service.impl;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yu.idworker.Sid;
import com.yu.mapper.CommentsMapper;
import com.yu.mapper.CommentsMapperCustom;
import com.yu.mapper.SearchRecordsMapper;
import com.yu.mapper.UsersLikeVideosMapper;
import com.yu.mapper.UsersMapper;
import com.yu.mapper.VideosMapper;
import com.yu.mapper.VideosMapperCustom;
import com.yu.pojo.Comments;
import com.yu.pojo.SearchRecords;
import com.yu.pojo.UsersLikeVideos;
import com.yu.pojo.Videos;
import com.yu.pojo.vo.CommentsVO;
import com.yu.pojo.vo.VideosVO;
import com.yu.service.VideoService;
import com.yu.utils.PagedResult;
import com.yu.utils.TimeAgoUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;


@Service
public class VideoServiceImpl implements VideoService {
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private CommentsMapper commentsMapper;
	
	@Autowired
	private CommentsMapperCustom commentsMapperCustom;
	
	@Autowired
	private UsersMapper usersMapper;
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		String id = sid.nextShort();
		video.setId(id);
		videosMapper.insertSelective(video);
		return id;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String coverPath) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videosMapper.updateByPrimaryKeySelective(video);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page, Integer pageSize) {
		String desc = video.getVideoDesc();
		String userId = video.getUserId();
		
		if(isSaveRecord != null && isSaveRecord == 1) {
			SearchRecords sr = new SearchRecords(); 
			sr.setId(sid.nextShort());
			sr.setContent(desc);
			searchRecordsMapper.insert(sr);
		}
		PageHelper.startPage(page, pageSize);
		
		List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
		
		 PageInfo<VideosVO> pageList = new PageInfo<>(list);
		 
		 PagedResult pagedResult = new PagedResult();
		 pagedResult.setPage(page);
		 pagedResult.setRecords(pageList.getTotal());
		 pagedResult.setRow(list);
		 pagedResult.setTotal(pageList.getPages());
		
		return pagedResult;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS	)
	@Override
	public List<String> getHotWords() {
		
		return searchRecordsMapper.getHotwords();
	}

	@Transactional(propagation=Propagation.REQUIRED	)
	@Override
	public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
		//1.保存用户和视频的点赞关联关系表
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(sid.nextShort());
		ulv.setVideoId(videoId);
		ulv.setUserId(userId);
		usersLikeVideosMapper.insert(ulv);
		
		//2.视频喜欢数量的累加
		videosMapperCustom.addVideoLikeCount(videoId);
		
		//3.用户受喜欢数量的累加
		usersMapper.addReceiveLikeCount(userId);
	}
	
	@Transactional(propagation=Propagation.REQUIRED	)
	@Override
	public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
		//1.删除用户和视频的点赞关联关系表
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		
		usersLikeVideosMapper.deleteByExample(example);
				
		//2.视频喜欢数量的累减
		videosMapperCustom.reduceVideoLikeCount(videoId);
				
		//3.用户受喜欢数量的累减
		usersMapper.reduceReceiveLikeCount(userId);
		
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page,pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);
		
		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		 
		 PagedResult pagedResult = new PagedResult();
		 pagedResult.setPage(page);
		 pagedResult.setRecords(pageList.getTotal());
		 pagedResult.setRow(list);
		 pagedResult.setTotal(pageList.getPages());
		
		return pagedResult;
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {
		PageHelper.startPage(page,pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);
		
		PageInfo<VideosVO> pageList = new PageInfo<>(list);
		 
		 PagedResult pagedResult = new PagedResult();
		 pagedResult.setPage(page);
		 pagedResult.setRecords(pageList.getTotal());
		 pagedResult.setRow(list);
		 pagedResult.setTotal(pageList.getPages());
		
		return pagedResult;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void saveComment(Comments comment) {
		comment.setId(sid.nextShort());
		comment.setCreateTime(new Date());
		commentsMapper.insert(comment);
	}

	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
		
		PageHelper.startPage(page,pageSize);
		List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
		
			for(CommentsVO cv: list) {
				cv.setTimeAgoStr(TimeAgoUtils.format(cv.getCreateTime()));
			}
		
		PageInfo<CommentsVO> pageList = new PageInfo<>(list);
		 
		 PagedResult pagedResult = new PagedResult();
		 pagedResult.setPage(page);
		 pagedResult.setRecords(pageList.getTotal());
		 pagedResult.setRow(list);
		 pagedResult.setTotal(pageList.getPages());
		
		return pagedResult;
	}

	
	
}
