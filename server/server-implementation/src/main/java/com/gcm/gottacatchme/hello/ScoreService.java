package com.gcm.gottacatchme.hello;

import java.util.HashMap;
import java.util.Map;

public class ScoreService implements IScoreService
{
	Map<String, Integer> _scores;
	
	public ScoreService()
	{
		_scores = new HashMap<>();
		
		_scores.put("Jan", 75);
		_scores.put("Jasper", 40);
		_scores.put("Nico", 45);
		_scores.put("Randolf", 80);
	}
	
	@Override
	public Map<String, Integer> getScores() 
	{
		return _scores;
	}

}
