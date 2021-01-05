package com.kimleysoft.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kimleysoft.annotaion.RedisCache;
import com.kimleysoft.entity.User;
import com.kimleysoft.mapper.UserMapper;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@RedisCache()
	public User getUser(User record){
		return userMapper.select(record).get(0);
	}
	
	@RedisCache()
	public List<User> getUsers(User record){
		return userMapper.select(record);
	}
	
	@RedisCache()
	public Map<String,List<User>> getUserMap(User record){
		 List<User> userList = userMapper.select(record);
		 Map<String, List<User>> map = new HashMap<>();
		 map.put("userList", userList);
		 return map;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void addUser(User record){
		userMapper.insert(record);
		User user2 = new User();
		user2.setId(Long.valueOf("3"));
		user2.setAge(18);
		user2.setName("田七");
		user2.setSex(true);
	//	int i = 1/0;
		userMapper.insert(user2);
	}
}
