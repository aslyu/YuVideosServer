package com.yu;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yu.config.ResourceConfig;
import com.yu.enums.BGMOperatorTypeEnum;
import com.yu.utils.JsonUtils;

@Component
public class ZKCuratorClient {

	// zk客户端
	private CuratorFramework client = null;	
//	final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

	
	@Autowired
	private  ResourceConfig resourceConfig;
	
	public void init() {
		
		
		if (client != null) {
			return;
		}
		
		// 重试策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		// 创建zk客户端
		client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
		// 启动客户端
		client.start();
		
		try {
			addChildWatch("/bgm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void addChildWatch(String nodePath) throws Exception {
		
		@SuppressWarnings("resource")
		final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
		cache.start();
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) 
					throws Exception {
				
				if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
//					log.info("监听到事件 CHILD_ADDED");
					
					// 1. 从数据库查询bgm对象，获取路径path
					// 1. updated 修改为从zk获取
					String path = event.getData().getPath();
					String operatorObjStr = new String(event.getData().getData(), "UTF-8");
					@SuppressWarnings("unchecked")
					Map<String, String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
					String operatorType = map.get("operType");
					String songPath = map.get("path");
					
					// 3. 定义下载的路径（播放url）
//					String arrPath[] = songPath.split("/");			// linux
//					String arrPath[] = songPath.split("\\\\");		// windows
					String arrPath[] = songPath.split(resourceConfig.getSplit());
					String finalPath = "";
					String finalPath2 = "";
					// 3.1 处理url的斜杠以及编码
					for(int i = 0; i < arrPath.length ; i ++) {
						if (StringUtils.isNotBlank(arrPath[i])) {
							finalPath += "/";
							finalPath += arrPath[i];
							finalPath2 += "/";
							finalPath2 += URLDecoder.decode(arrPath[i], "UTF-8") ;
							
						}
					}
					
					
//					log.info("2. ============ finalPath:{}", finalPath);
					
					String filePath = resourceConfig.getFileSpace() + finalPath2;
					String bgmUrl = resourceConfig.getBgmServer() + finalPath;
//					log.info("1. ============ filePath:{}", filePath);
//					log.info("3. ============ bgmUrl:{}", bgmUrl);
					System.out.println("这里"+finalPath);
					System.out.println("这里"+finalPath2);
					System.out.println("这里"+filePath);
					System.out.println("这里"+bgmUrl);
					if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
						// 下载bgm到spingboot服务器
						URL url = new URL(bgmUrl);
						File file = new File(filePath);
						FileUtils.copyURLToFile(url, file);
						client.delete().forPath(path);
					} else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
						File file = new File(filePath);
						FileUtils.forceDelete(file);
						client.delete().forPath(path);
					}
				}
			}
		});
	}
	
	
	public static void main(String[] args) {
		
		try {
			System.out.println(URLDecoder.decode("%2Fbgm%2FAki%E9%98%BF%E6%9D%B0%2C%E9%93%B6%E4%B8%B4%20-%20%E7%89%B5%E4%B8%9D%E6%88%8F", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
