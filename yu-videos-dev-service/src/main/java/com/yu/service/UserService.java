package com.yu.service;

import com.yu.pojo.Users;
import com.yu.pojo.UsersReport;

public interface UserService {
	
	/**
	 * 判断用户名是否存在
	 * @param userName
	 * @return
	 */
	boolean userNameIsExist(String userName);
	
	/**
	 * 注册用户到数据库中
	 * @param user
	 */
	void regist(Users user);
	
	/**
	 * 进行登录
	 * @param user
	 * @return
	 */
	Users login(Users user);
	
	/**
	 * 更新用户信息
	 * @param user
	 */
	void updateUserInfo(Users user);
	
	/**
	 * 根据ID查询用户信息
	 * @param userId
	 * @return
	 */
	Users queryUserInfo(String userId);
	
	/**
	 * 查询用户是否点赞了该视频
	 */
	boolean isUserLikeVideo(String userId,String videoId);
	
	void saveUserFanRelation(String userId,String fanId);
	
	void deleteUserFanRelation(String userId,String fanId);
	
	boolean queryIfFollow(String userId,String fanId);

	void reportUser(UsersReport usersReport);
}
