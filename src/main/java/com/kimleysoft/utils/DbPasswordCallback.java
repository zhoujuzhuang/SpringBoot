package com.kimleysoft.utils;

import java.util.Properties;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;

public class DbPasswordCallback extends DruidPasswordCallback{

	@Override
	public void setProperties(Properties properties){
		super.setProperties(properties);
		//获取application.yml 里面配置的密码和公钥
        String password = (String) properties.get("password");
        String publickey = (String) properties.get("publicKey");
        try {
            String dbpassword = ConfigTools.decrypt(publickey, password);
            setPassword(dbpassword.toCharArray());
        } catch (Exception e) {
        }
	}
}
