package com.yu.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yu.idworker.Sid;
import com.yu.mapper.UsersFansMapper;
import com.yu.mapper.UsersLikeVideosMapper;
import com.yu.mapper.UsersMapper;
import com.yu.mapper.UsersReportMapper;
import com.yu.pojo.Users;
import com.yu.pojo.UsersFans;
import com.yu.pojo.UsersLikeVideos;
import com.yu.pojo.UsersReport;
import com.yu.service.UserService;
import com.yu.utils.MD5Utils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;


@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UsersMapper userMapper;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private UsersFansMapper usersFansMapper;
	
	@Autowired
	private UsersReportMapper usersReportMapper;
	
	@Autowired
	private Sid sid;
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public boolean userNameIsExist(String userName) {
		Users user = new Users();
		user.setUsername(userName);
		Users result = userMapper.selectOne(user);
		return result ==null ? false : true;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void regist(Users user) {
		String userId = sid.nextShort();
		user.setId(userId);
		userMapper.insert(user);
		
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Users login(Users user) {
		try {
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Users currentUser = userMapper.login(user.getUsername(), user.getPassword());
		return currentUser;
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateUserInfo(Users user) {
		// TODO Auto-generated method stub
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", user.getId());
		userMapper.updateByExampleSelective(user, userExample);
	}
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public Users queryUserInfo(String userId) {
		// TODO Auto-generated method stub
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("id", userId);
		Users user = userMapper.selectOneByExample(userExample);
		return user;
	}

	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public boolean isUserLikeVideo(String userId, String videoId) {
		
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}
		
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId",userId);
		criteria.andEqualTo("videoId",videoId);
		
		List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
		
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void saveUserFanRelation(String userId, String fanId) {
		
		UsersFans usersFans = new UsersFans(); 
		usersFans.setId(sid.nextShort());
		usersFans.setUserId(userId);
		usersFans.setFanId(fanId);
		usersFansMapper.insert(usersFans);
		
		userMapper.addFansCount(userId);
		userMapper.addFollersCount(fanId);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void deleteUserFanRelation(String userId, String fanId) {
		
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		
		usersFansMapper.deleteByExample(example);
		
		userMapper.reduceFansCount(userId);
		userMapper.reduceFollersCount(fanId);
		
	}
	
	
	@Transactional(propagation=Propagation.SUPPORTS)
	@Override
	public boolean queryIfFollow(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		
		List<UsersFans> list = usersFansMapper.selectByExample(example);
		if(list != null && !list.isEmpty() && list.size() > 0) {
			return true;
		}
		return false;
	}

	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void reportUser(UsersReport usersReport) {
		
		usersReport.setId(sid.nextShort());
		usersReport.setCreateDate(new Date());
		
		usersReportMapper.insert(usersReport);
	}

}
