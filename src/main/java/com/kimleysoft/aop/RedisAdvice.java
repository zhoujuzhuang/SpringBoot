package com.kimleysoft.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.kimleysoft.annotaion.RedisCache;
import com.kimleysoft.annotaion.RedisEvict;
import com.kimleysoft.utils.Handle;

@Aspect
@Component
public class RedisAdvice {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Pointcut("execution(* com.kimleysoft.service..*.*(..))")
	private void pt1() {
	}

	@SuppressWarnings("rawtypes")
	@Around("pt1()")
	public Object before(ProceedingJoinPoint jp) throws Throwable {
			String clazzName = jp.getTarget().getClass().getSimpleName();
			String methodName = jp.getSignature().getName();
			Object[] args = jp.getArgs();

			Object result = null;
			Method me = ((MethodSignature) jp.getSignature()).getMethod();
			String value = null;
			Type entityType = null;
			boolean annotationPresent = me.isAnnotationPresent(RedisCache.class);
			if(annotationPresent){
				String key = genKey(clazzName, methodName, args);
				
				Type genericReturnType = me.getGenericReturnType();
				if(genericReturnType instanceof ParameterizedType){
					entityType = getType((ParameterizedType)genericReturnType);
				}else{
					entityType = genericReturnType;
				}
				value = (String) redisTemplate.opsForHash().get(entityType.getTypeName(), key);
				
				System.out.println("查询缓存：" + value);
				if (null == value) {
					result = jp.proceed(args);
					System.out.println("查询数据库：" + result);
					String json = serialize(result);
					if(annotationPresent) {
						redisTemplate.opsForHash().put(entityType.getTypeName(), key, json);
					}
				} else {
					Class returnType = ((MethodSignature) jp.getSignature()).getReturnType();
					result = deserialize(value, returnType, (Class)entityType);
				}
				return result;
			}else {
				result = jp.proceed(args);
				return result;
			}
	}
	
	@SuppressWarnings("rawtypes")
	@Around("pt1()")
	public Object evictCache(ProceedingJoinPoint jp) throws Throwable {

        Method me = ((MethodSignature) jp.getSignature()).getMethod();
        if(me.isAnnotationPresent(RedisEvict.class)){
        	Class entity = me.getAnnotation(RedisEvict.class).entity();
        	redisTemplate.delete(entity.getName());
        }
        return jp.proceed(jp.getArgs());
    }

	protected String genKey(String clazzName, String methodName, Object[] args) throws Exception {
		StringBuilder sb = new StringBuilder(clazzName);
		sb.append("|");
		sb.append(methodName);
		sb.append("|");
		for (Object obj : args) {
			if(obj == null) {
				continue;
			}
			sb.append(obj.toString());
			sb.append("|");
		}
		String key = Handle.getMD5String(sb.toString());
	    return key;
	}

	protected String serialize(Object target) {
		return JSON.toJSONString(target);
	}

	@SuppressWarnings({"unchecked", "rawtypes" })
	protected Object deserialize(String jsonString, Class clazz, Class modelType) {
		if (clazz.isAssignableFrom(List.class)) {
			return JSON.parseArray(jsonString, modelType);
		}
		return JSON.parseObject(jsonString, clazz);
	}
	
	public Type getType(ParameterizedType parameterizedType) {
		Type entityType = null;
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		if(actualTypeArguments.length == 1){
			entityType = actualTypeArguments[0];
		}
		if(entityType instanceof ParameterizedType) {
			getType((ParameterizedType)entityType);
		}
		return entityType;
	}
}
