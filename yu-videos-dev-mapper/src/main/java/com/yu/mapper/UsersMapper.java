package com.yu.mapper;

import com.yu.utils.MyMapper;

import org.apache.ibatis.annotations.Param;

import com.yu.pojo.Users;

public interface UsersMapper extends MyMapper<Users> {
	
	Users login(@Param("username") String username,@Param("password") String password);
	
	void addReceiveLikeCount(String userId);
	
	void reduceReceiveLikeCount(String userId);
	
	void addFansCount(String userId);
	
	void reduceFansCount(String userId);
	
	void addFollersCount(String userId);
	
	void reduceFollersCount(String userId);
}