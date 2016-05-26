package com.gcm.gottacatchme.hello;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/scores")
public interface IScoreService
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	Map<String, Integer> getScores();
}
