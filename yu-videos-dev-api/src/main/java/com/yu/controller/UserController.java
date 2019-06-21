package com.yu.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yu.config.ResourceConfig;
import com.yu.idworker.Sid;
import com.yu.pojo.Users;
import com.yu.pojo.UsersReport;
import com.yu.pojo.vo.PublishVideoVO;
import com.yu.pojo.vo.UsersVO;
import com.yu.service.UserService;
import com.yu.utils.JSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value="用户相关业务的接口",tags= {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController{
	
	@Autowired
	private ResourceConfig resourceConfig;
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private UserService userService;
	
	@ApiOperation(value="用户上传头像",notes="用户上传头像的接口")
	@ApiImplicitParam(name="userId",value="用户id",required=true,
						dataType="String",paramType="query")
	@PostMapping("/uploadFace")
	public JSONResult uploadFace(String userId,@RequestParam("file") MultipartFile[] files) throws Exception {
		
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		
		FileOutputStream  fileOutputStream = null;
		InputStream inputStream =null;
//		String fileSpace = "F:\\Local Data\\YuVideos Data";
		String fileSpace = resourceConfig.getFileSpace();
		String uploadPathDB = "/"+userId+"/face";
		
		String finalFacePath = null;
		String fileName = null;
		
		try {
			if(files != null && files.length > 0 ) {
				
				fileName = files[0].getOriginalFilename();
				if(StringUtils.isNotBlank(fileName)) {
					finalFacePath = fileSpace + uploadPathDB +"/"+ fileName;
					
					File outFile = new File(finalFacePath);
					if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					
					
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
					
					
					
				}
			}else {
				return JSONResult.errorMsg("上传出错！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONResult.errorMsg("上传出错！");
		}finally {
			if(fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
				
			}
		}
		
		//给上传的文件重命名
		fileName = sid.nextShort() + fileName.substring(fileName.lastIndexOf("."),fileName.length());
		new File(finalFacePath).renameTo(new File(fileSpace + uploadPathDB +"/"+ fileName));
		uploadPathDB += ("/"+ fileName);
		
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		userService.updateUserInfo(user);
		
		return JSONResult.ok(uploadPathDB);
	}
	
	@ApiOperation(value="根据用户id查询用户信息",notes="根据用户id查询用户信息的接口")
	@ApiImplicitParam(name="userId",value="用户id",required=true,
						dataType="String",paramType="query")
	@PostMapping("/query")
	public JSONResult query(String userId,String fanId) throws Exception {
		
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		Users userInfo = userService.queryUserInfo(userId);
		UsersVO userVO = new UsersVO();
		BeanUtils.copyProperties(userInfo, userVO);	
		
		userVO.setFollow(userService.queryIfFollow(userId, fanId));
		
		return JSONResult.ok(userVO);
	}
	
	
	@PostMapping("/queryPublisher")
	public JSONResult queryPublisher(String loginUserId,String videoId,String publishUserId) throws Exception {
		
		if(StringUtils.isBlank(publishUserId)) {
			return JSONResult.errorMsg("");
		}
		
		Users userInfo = userService.queryUserInfo(publishUserId);
		UsersVO publishUserVO = new UsersVO();
		BeanUtils.copyProperties(userInfo, publishUserVO);	
		
		boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
		
		PublishVideoVO publishVideoVO = new PublishVideoVO();
		
		publishVideoVO.setPublisher(publishUserVO);
		publishVideoVO.setUserLikeVideo(userLikeVideo);
		
		return JSONResult.ok(publishVideoVO);
	}
	
	@PostMapping("/beyourfans")
	public JSONResult beyourfans(String userId,String fanId) throws Exception {
		
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.saveUserFanRelation(userId, fanId);
		return JSONResult.ok("关注成功~");
	}
	
	@PostMapping("/dontyourfans")
	public JSONResult dontyourfans(String userId,String fanId) throws Exception {
		
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return JSONResult.errorMsg("");
		}
		userService.deleteUserFanRelation(userId, fanId);
		return JSONResult.ok("已取消关注~");
	}
	
	@PostMapping("/reportUser")
	public JSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception {
		
		userService.reportUser(usersReport);
		return JSONResult.ok("举报成功~");
	}
}
