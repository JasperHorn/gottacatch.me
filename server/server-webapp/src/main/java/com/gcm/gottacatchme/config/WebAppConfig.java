package com.gcm.gottacatchme.config;

import java.util.Arrays;
import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationOutInterceptor;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:META-INF/cxf/cxf.xml" })
@Import(AppConfig.class)
@ComponentScan
public class WebAppConfig
{
	@Bean
	public Server serverJaxRS(
		@Qualifier("com.wccgroup.elise.servicebean")
		List<Object> serviceBeans,
		@Qualifier("javax.ws.rs.provider")
		List<Object> providers,
		Bus bus)
	{
		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
		factoryBean.setBus(bus);
		factoryBean.setAddress("/rest");
		factoryBean.setServiceBeans(serviceBeans);
		factoryBean.setProviders(providers);
		factoryBean.setInInterceptors(asInterceptorsList(new JAXRSBeanValidationInInterceptor()));
		factoryBean.setOutInterceptors(asInterceptorsList(new JAXRSBeanValidationOutInterceptor()));

		Server server = factoryBean.create();

		return server;
	}

	@SafeVarargs
	private static List<Interceptor<? extends Message>> asInterceptorsList(
		Interceptor<? extends Message>... interceptors)
	{
		return Arrays.asList(interceptors);
	}
}
