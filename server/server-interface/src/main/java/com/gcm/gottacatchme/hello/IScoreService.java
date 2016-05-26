package com.gcm.gottacatchme.hello;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/scores")
public interface IScoreService
{
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<ScoreRow> getScores();
	
	@POST
	@Path("/{user}")
	void addScore(@PathParam("user") String user, @QueryParam("score") int score);
	
	@DELETE
	public void resetScores();
	
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
		
		public void setScore(int score)
		{
			_score = score;
		}
	}
}
