package com.gcm.gottacatchme.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public interface IHelloService
{
	@GET
	public String hello();
}
