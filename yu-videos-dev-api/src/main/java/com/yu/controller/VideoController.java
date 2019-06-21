package com.yu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yu.config.ResourceConfig;
import com.yu.enums.VideoStatusEnum;
import com.yu.idworker.Sid;
import com.yu.pojo.Bgm;
import com.yu.pojo.Comments;
import com.yu.pojo.Videos;
import com.yu.service.BgmService;
import com.yu.service.VideoService;
import com.yu.utils.JSONResult;
import com.yu.utils.PagedResult;
import com.yu.utils.FFmpegUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
@RestController
@Api(value="视频相关业务的接口",tags= {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController {
	
	@Autowired
	private  ResourceConfig resourceConfig;
	
	
	@Autowired
	private Sid sid;

	@Autowired
	private BgmService bgmService;
	
	@Autowired
	private VideoService videosService;
	
	@ApiOperation(value="上传视频",notes="上传视频的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name="userId",value="用户id",required=true,
				dataType="String",paramType="form"),
		@ApiImplicitParam(name="bgmId",value="背景音乐id",required=false,
				dataType="String",paramType="form"),
		@ApiImplicitParam(name="videoSeconds",value="背景音乐播放长度",required=true,
		dataType="String",paramType="form"),
		@ApiImplicitParam(name="videoWidth",value="视频宽度",required=true,
		dataType="String",paramType="form"),
		@ApiImplicitParam(name="videoHeight",value="视频高度",required=true,
		dataType="String",paramType="form"),
		@ApiImplicitParam(name="desc",value="视频描述",required=false,
		dataType="String",paramType="form")
	})
	@PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
	public JSONResult upload(String userId,String desc,
			String bgmId,double videoSeconds,int videoWidth,int videoHeight,
			@ApiParam(value="短视频",required=true)
			MultipartFile file) throws Exception {
		
		if(StringUtils.isBlank(userId)) {
			return JSONResult.errorMsg("用户id不能为空！");
		}
		
		
		FileOutputStream  fileOutputStream = null;
		InputStream inputStream =null;
//		String fileSpace = "F:\\Local Data\\YuVideos Data";
		String fileSpace = resourceConfig.getFileSpace();
		String uploadPathDB = "/"+userId+"/video";
		String coverPathDB = "/"+userId+"/video";
		String temporaryPath = null;
		
		String finalVideoPath = null;
		String fileName = null;
		
		try {
			if(file != null) {
				
				fileName = file.getOriginalFilename();
				if(StringUtils.isNotBlank(fileName)) {
					finalVideoPath = fileSpace + uploadPathDB +"/"+ fileName;
					temporaryPath  = uploadPathDB +("/"+ fileName);
					
					File outFile = new File(finalVideoPath);
					if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}
					
					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
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
		uploadPathDB += ("/" + "V" + fileName);
		coverPathDB += ("/" + "P" + fileName);
		
		FFmpegUtils fFmpegUtils = new FFmpegUtils();
		
		if(StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgm(bgmId);
			String bgmPath = bgm.getPath();
			
			fFmpegUtils.mergeVideoBgm(resourceConfig.getFfmpegPath(),fileSpace+bgmPath, fileSpace+temporaryPath, videoSeconds, fileSpace+uploadPathDB);
			File temporaryFile = new File(fileSpace+temporaryPath);
			if (temporaryFile.isFile() && temporaryFile.exists()) {  
				temporaryFile.delete();  
			}
		}else {
			new File(finalVideoPath).renameTo(new File(fileSpace + uploadPathDB));
		}
		
		coverPathDB = fFmpegUtils.fetchVideoCover(resourceConfig.getFfmpegPath(),fileSpace + uploadPathDB, fileSpace +coverPathDB, "jpg").substring(fileSpace.length());
		
		//保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setVideoDesc(desc);
		video.setVideoWidth(videoWidth);
		video.setVideoHeight(videoHeight);
		video.setVideoSeconds((float) videoSeconds);
		video.setStatus(VideoStatusEnum.SUCCESS.getValue());
		video.setCreateTime(new Date());
		
		videosService.saveVideo(video);
		
		return JSONResult.ok();
	}
	
	
	
	@PostMapping(value = "/showAll")
	public JSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
							  Integer page) throws Exception {
	
		if(page == null) {
			page = 1;
		}
		
		PagedResult allVideos = videosService.getAllVideos(video,isSaveRecord,page, 5);
		return JSONResult.ok(allVideos);
	}
	
	@PostMapping(value = "/showMyFollow")
	public JSONResult showMyFollow(String userId,Integer page) throws Exception {
		if(StringUtils.isBlank(userId)) {
			return JSONResult.ok();
		}
		if(page == null) {
			page = 1;
		}
		int pageSize = 5;
		
		PagedResult videosList = videosService.queryMyFollowVideos(userId,page,pageSize);
		return JSONResult.ok(videosList);
	}
	
	
	@PostMapping(value = "/showMyLike")
	public JSONResult showMyLike(String userId, Integer pageSize,Integer page) throws Exception {
		if(StringUtils.isBlank(userId)) {
			return JSONResult.ok();
		}
		if(page == null) {
			page = 1;
		}
		if(pageSize == null) {
			pageSize = 5;
		}
		
		PagedResult videosList = videosService.queryMyLikeVideos(userId,page,pageSize);
		return JSONResult.ok(videosList);
	}
	
	@PostMapping(value = "/hot")
	public JSONResult hot() throws Exception {
		return JSONResult.ok(videosService.getHotWords());
	}
	
	@PostMapping(value = "/userLike")
	public JSONResult userLike(String userId,String videoId,String videoCreaterId) throws Exception {
		videosService.userLikeVideo(userId, videoId, videoCreaterId);
		return JSONResult.ok();
	}
	
	@PostMapping(value = "/userUnLike")
	public JSONResult userUnLike(String userId,String videoId,String videoCreaterId) throws Exception {
		videosService.userUnLikeVideo(userId, videoId, videoCreaterId);
		return JSONResult.ok();
	}
	
	@PostMapping(value = "/saveComment")
	public JSONResult saveComment(@RequestBody Comments comment,String fatherCommentId,String toUserId) throws Exception {
		if(fatherCommentId != null && fatherCommentId != ""
			&&	toUserId != null && toUserId != "") {
			comment.setFatherCommentId(fatherCommentId);
			comment.setToUserId(toUserId);
		}
		videosService.saveComment(comment);
		return JSONResult.ok();
	}
	
	@PostMapping(value = "/getVideoComments")
	public JSONResult getVideoComments(String videoId,Integer page,Integer pageSize) throws Exception {
		
		if(StringUtils.isBlank(videoId)) {
			return JSONResult.ok();
		}
		if(page == null) {
			page = 1;
		}
		if(pageSize == null) {
			pageSize = 10;
		}
		
		PagedResult list = videosService.getAllComments(videoId,page,pageSize);
		return JSONResult.ok(list);
	}

}
