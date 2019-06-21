package com.yu.controller;


import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yu.pojo.Users;
import com.yu.pojo.vo.UsersVO;
import com.yu.service.UserService;
import com.yu.utils.JSONResult;
import com.yu.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="用户登录注册接口",tags= {"注册和登录的controller"})
public class RegistLoginController extends BasicController{
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value="用户注册",notes="用户注册的接口")
	@PostMapping("/regist")
	public JSONResult regist(@RequestBody Users user) throws Exception {
		if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空！");
		}else if(userService.userNameIsExist(user.getUsername())) {
			return JSONResult.errorMsg("用户名已经存在");
		}
		user.setNickname(user.getUsername());
		user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
		user.setFollowCounts(0);
		user.setFansCounts(0);
		user.setReceiveLikeCounts(0);
		userService.regist(user);
		user.setPassword("");
	
		UsersVO uservo = getToken(user);
		
		return JSONResult.ok(uservo);
	}
	
	public UsersVO getToken(Users user) {
		String uniqueToken = UUID.randomUUID().toString();
		redis.set(USER_REDIS_SESSION+":"+user.getId(), uniqueToken, 1000*60*60*24*7);
		UsersVO uservo = new UsersVO();
		BeanUtils.copyProperties(user, uservo);
		uservo.setUserToken(uniqueToken);
		return uservo;
	}
	
	@ApiOperation(value="用户登录",notes="用户登录的接口")
	@PostMapping("/login")
	public JSONResult login(@RequestBody Users user) throws InterruptedException {
		if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
			return JSONResult.errorMsg("用户名和密码不能为空！");
		}
		Users currentUser = userService.login(user);
		if(currentUser == null) {
			return JSONResult.errorMsg("用户名或密码错误！");
		}
		currentUser.setPassword("");
		UsersVO uservo = getToken(currentUser);
		return JSONResult.ok(uservo);
	}
	
	@ApiOperation(value="用户注销",notes="用户注销的接口")
	@ApiImplicitParam(name="userId",value="用户id",required=true,
						dataType="String",paramType="query")
	@PostMapping("/logout")
	public JSONResult logout(String userId) throws InterruptedException {
		redis.del(USER_REDIS_SESSION+":"+userId);
		return JSONResult.ok();
	}
	
}
