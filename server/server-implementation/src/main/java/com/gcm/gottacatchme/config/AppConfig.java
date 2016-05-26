package com.gcm.gottacatchme.config;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import com.gcm.gottacatchme.hello.HelloService;
import com.gcm.gottacatchme.hello.HintsService;
import com.gcm.gottacatchme.hello.IHelloService;
import com.gcm.gottacatchme.hello.IHintsService;
import com.gcm.gottacatchme.hello.IThingStatus;
import com.gcm.gottacatchme.hello.ThingStatusService;
import com.gcm.gottacatchme.thingyclient.*;
import com.gcm.gottacatchme.messageservice.*;

public class AppConfig
{
	@Bean
	@Qualifier("javax.ws.rs.provider")
	public JacksonJsonProvider jsonProvider()
	{
		ObjectMapper objectMapper = new ObjectMapper();

		JacksonJsonProvider provider = new JacksonJsonProvider();
		provider.setMapper(objectMapper);

		return provider;
	}

	@Bean
	@Qualifier("com.wccgroup.elise.servicebean")
	public IHelloService match()
	{
		return new HelloService();
	}
	
	@Bean
	@Qualifier("com.wccgroup.elise.servicebean")
	public IThingStatus thingStatus(ThingyClient client)
	{
		return new ThingStatusService(client);
	}
	
	@Bean
	@Qualifier("com.wccgroup.elise.servicebean")
	public IHintsService hintservice()
	{
		return new HintsService();
	}
	
	@Bean
	public ThingyClient thingyClient()
	{
		return new ThingyClient();
	}
	
	@Bean
	public MessageService messageService()
	{
		return new MessageService();
	}
}
