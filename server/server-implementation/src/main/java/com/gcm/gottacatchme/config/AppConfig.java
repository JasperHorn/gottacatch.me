package com.gcm.gottacatchme.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.gcm.gottacatchme.hello.HelloService;
import com.gcm.gottacatchme.hello.IHelloService;
import com.gcm.gottacatchme.thingyclient.*;

public class AppConfig
{
	@Bean
	@Qualifier("com.wccgroup.elise.servicebean")
	public IHelloService match()
	{
		return new HelloService();
	}
	
	@Bean
	public ThingyClient thingyClient()
	{
		return new ThingyClient();
	}
}
