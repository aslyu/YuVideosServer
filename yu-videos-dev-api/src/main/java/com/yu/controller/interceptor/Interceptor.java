package com.yu.controller.interceptor;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.yu.utils.JSONResult;
import com.yu.utils.JsonUtils;
import com.yu.utils.RedisOperator;

public class Interceptor implements HandlerInterceptor{
	
	@Autowired
	public RedisOperator redis;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";

	@SuppressWarnings("static-access")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userId = request.getHeader("userId");
		String userToken = request.getHeader("userToken");
		System.out.println(userId+":"+userToken);

		
		if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
			

			String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
			if(StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
				System.out.println("账号过期，请登录");
				returnErrorResponse(response, new JSONResult().errorTokenMsg("账号过期，请登录"));
				return false;
			}else {
				if(!uniqueToken.equals(userToken)) {
					System.out.println("账号被挤出...");
					returnErrorResponse(response, new JSONResult().errorTokenMsg("账号被挤出..."));
					return false;
				}
			}
			return true;
		}else {
			System.out.println("请登录~~~");
			System.out.println(request.getContextPath());
			returnErrorResponse(response, new JSONResult().errorTokenMsg("请登录..."));
			return false;
		}
	}
	
	public void returnErrorResponse(HttpServletResponse response,JSONResult result) throws Exception{
		OutputStream out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if(out != null) {
				out.close();
			}
		}
	}
	

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
	
		

}
