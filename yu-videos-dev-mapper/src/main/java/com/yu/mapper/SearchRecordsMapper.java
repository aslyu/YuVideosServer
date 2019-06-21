package com.yu.mapper;

import java.util.List;

import com.yu.utils.MyMapper;
import com.yu.pojo.SearchRecords;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	List<String> getHotwords();
	
}