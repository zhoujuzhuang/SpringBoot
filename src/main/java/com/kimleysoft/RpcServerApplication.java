package com.kimleysoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@MapperScan(basePackages = "com.kimleysoft.mapper")
public class RpcServerApplication /* extends SpringBootServletInitializer */{
	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder
	// application) {
	// return application.sources(RpcServerApplication.class);
	// }

	public static void main(String[] args) {
		SpringApplication.run(RpcServerApplication.class, args);
	}

	@Bean(name = "messageSource")
	public MessageSource initReloadableResourceBundleMessageSource() {
		ReloadableResourceBundleMessageSource rrbms = new ReloadableResourceBundleMessageSource();
		rrbms.setDefaultEncoding("UTF-8");
		rrbms.setBasename("classpath:lang/message");
		return rrbms;
	}
}
