package com.kimleysoft.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.kimleysoft.datasource.DBContextHolder;

@Aspect
@Component
public class DataSourceAdvice{
	
	@Pointcut("execution(* com.kimleysoft.service..*.*(..))")
	private void pt1(){}
	
	// service方法执行之前被调用
	@Before("pt1()")
	public void before(JoinPoint jp) {
      String methodName = jp.getSignature().getName();
      System.out.println("方法名:"+methodName);
      if (methodName.startsWith("add") 
				|| methodName.startsWith("auto") 
				|| methodName.startsWith("setentity") 
				|| methodName.startsWith("create") 
				|| methodName.startsWith("save") 
				|| methodName.startsWith("txnew")
				|| methodName.startsWith("update") 
				|| methodName.startsWith("delete") 
				|| methodName.startsWith("remove") 
				|| methodName.startsWith("modify")
				|| methodName.startsWith("getlanguagename") 
				|| methodName.startsWith("push") 
				|| methodName.startsWith("chang") 
				|| methodName.startsWith("check")
				|| methodName.startsWith("set")
				|| methodName.startsWith("reset")
				|| methodName.startsWith("print")
				|| methodName.startsWith("calcula")
				|| methodName.startsWith("arrange")
				|| methodName.startsWith("insert")		
				|| methodName.startsWith("obj")) {
          DBContextHolder.master();
      }else {
          DBContextHolder.slave();
      }
  }

	// 抛出Exception之后被调用
	@AfterThrowing("pt1()")
	public void afterThrowing() throws Throwable {
		System.out.println("--------------------出現異常,切換到: master--------------------");
		DBContextHolder.master();
	}

}
