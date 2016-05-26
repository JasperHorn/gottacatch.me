package com.gcm.gottacatchme.hello;

import java.util.List;
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
	List<ScoreRow> getScores();
	
	public static class ScoreRow
	{
		private String _name;
		private int _score;
		
		public ScoreRow(String name, int score) {
			_name = name;
			_score = score;
		}
		
		public String getName()
		{
			return _name;
		}
		
		public int getScore()
		{
			return _score;
		}
	}
}
