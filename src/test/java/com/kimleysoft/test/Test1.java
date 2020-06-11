package com.kimleysoft.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.kimleysoft.RpcServerApplication;
import com.kimleysoft.entity.User;
import com.kimleysoft.mapper.UserMapper;
import com.kimleysoft.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=RpcServerApplication.class)
public class Test1 {

	@Autowired
	private UserService userService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
    private MessageSource messageSource;
//	@Autowired
//	private ActiveMqTemplate sendQueueMessage;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate redisTemplate;
	@Test
//	@Transactional
	public void testDatasource(){
		
		User user1 = new User();
		user1.setId(Long.valueOf("1"));
		user1.setAge(18);
		user1.setName("赵六2");
		user1.setSex(true);
		userService.addUser(user1);
		
//		User user = new User();
//		user.setId(1L);
//		user.setAge(15);
//		List<User> selectUser = userMapper.selectUser(user);
//		System.out.println(selectUser);
	}
	
	//@Test
	public void testI18n(){
		Locale locale = LocaleContextHolder.getLocale();
		//Locale local = new Locale("en","US");
		String message = messageSource.getMessage("1406856997540559", null, locale);
		System.out.println(message);
	}
	
	//@Test
//	public void testMq(){
//		sendQueueMessage.sendQueueMessage("queue message");
//		sendQueueMessage.sendTopicMessage("topic message");
//	}
	
	@SuppressWarnings("unchecked")
	//@Test
	public void testRedis(){
		List<List<User>> list = new ArrayList<List<User>>();
		List<User> userList = new ArrayList<User>();
		User user1 = new User(1L,"张三",18,false);
		User user2 = new User(2L,"李四",19,true);
		userList.add(user1);
		userList.add(user2);
		list.add(userList);
		BoundHashOperations<String, String, List<List<User>>> boundHashOps = redisTemplate.boundHashOps("userhash");
		boundHashOps.put("list", list);

		List<List<User>> l = (List<List<User>>)boundHashOps.get("list");
		
		System.out.println(l);
//		String aa = stringRedisTemplate.opsForValue().get("aa");
//		System.out.println(aa);
		
		
	}
	
//	@Test
//	public void testJson(){
//		User user1 = new User();
//		user1.setId(Long.valueOf("3"));
//		user1.setAge(18);
//		user1.setName("赵六2");
//		user1.setSex(true);
//		
//		List<User> list = new ArrayList<User>();
//		list.add(user1);
//		
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put(user1.toString(), user1);
//		
//		String jsonString = JSONObject.toJSONString(map);
//		System.out.println(jsonString);
//		
//		Map<String,Object> parseMap = JSONObject.parseObject(jsonString, Map.class);
//		System.out.println(parseMap);
//		
//	}
	
	//@Test
	public void testRedisAop(){
		User user = new User();
		User u = userService.getUser(user);
		System.out.println(u);
		List<User> userList = userService.getUsers(user);
		userList.forEach(uu->{
			System.out.println(uu);
		});
	}
}
