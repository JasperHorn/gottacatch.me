package com.gcm.gottacatchme.hello;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScoreService implements IScoreService
{
	Map<String, ScoreRow> _scoresMap;
	List<ScoreRow> _scores;
	
	public ScoreService()
	{
		_scoresMap = new HashMap<>();
		
		_scoresMap.put("Jan", new ScoreRow("Jan", 75));
		_scoresMap.put("Jasper", new ScoreRow("Jasper", 40));
		_scoresMap.put("Nico", new ScoreRow("Nico", 45));
		_scoresMap.put("Randolf", new ScoreRow("Randolf", 80));
		
		_scores = new LinkedList<>();
		
		for (ScoreRow scoreRow : _scoresMap.values())
		{
			_scores.add(scoreRow);
		}
	}
	
	@Override
	public List<ScoreRow> getScores() 
	{
		return _scores;
	}

}
