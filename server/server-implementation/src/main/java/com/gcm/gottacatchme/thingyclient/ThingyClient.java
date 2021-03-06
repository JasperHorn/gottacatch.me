package com.gcm.gottacatchme.thingyclient;

import java.util.HashMap;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcm.gottacatchme.hello.IScoreService;

public class ThingyClient
{
	public enum ThingStatus {
		IN_TRANSIT,
		AT_LOCATION,
		AWAITING_TRANSIT
	}
	
	private ThingStatus status = ThingStatus.AT_LOCATION;
	private String latitude;
	private String longitude;
	
	private IScoreService _scoreService;
	
	public ThingyClient(IScoreService scoreService)
	{
		_scoreService = scoreService;
		
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					mainLoop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
		
		t.setName("ThingyClient main loop");
		t.setDaemon(true);
		t.start();
	}
	
	public void mainLoop() throws Exception{
		MQTT mqtt = new MQTT();
		mqtt.setHost("staging.thethingsnetwork.org", 1883);
		mqtt.setUserName("70B3D57ED00001F2");
		mqtt.setPassword("DiqmvULgznOVVokHP5q0wxXhKe4g4exzC39P9AuuIZE=");
		
		System.out.println("connecting");
		BlockingConnection connection = mqtt.blockingConnection();
		connection.connect();
		
		Topic[] topics = {new Topic("+/devices/+/up", QoS.AT_LEAST_ONCE)};
		connection.subscribe(topics);
		System.out.println("now connected; entering event loop");
		
		while(true)
		{
			Message message = connection.receive();
			byte[] payload = message.getPayload();
			String device = message.getTopic().replace("up", "down");
			
			HashMap<String,Object> result = new ObjectMapper().readValue(payload, HashMap.class);
			HashMap<String,Object> stuff = (HashMap<String,Object>)result.get("fields");
			
//			System.out.println(stuff);
			
			if (stuff.get("msgtype").equals("login"))
			{
				boolean loginSuccess = stuff.get("success").equals("true");
				
				System.out.println("Got a login message; login was " + (loginSuccess ? "successful":"Unsuccessful"));
				
				if (loginSuccess)
				{
					_scoreService.addScore("Jan", 10);
					
					// TODO: Call the Uber API
					this.setStatus(ThingStatus.IN_TRANSIT);
				}
			}
			else if (stuff.get("msgtype").equals("location"))
			{
				System.out.println("Got a location message; location was (" + 
						stuff.get("latitude") + ", " + stuff.get("longitude") + ")");
				
				this.setLatitude(stuff.get("latitude"));
				this.setLongitude(stuff.get("longitude"));
				this.setStatus(ThingStatus.AT_LOCATION);
			}
			else
			{
				System.out.println("Got an unrecognized message!");
			}
			
			message.ack();
			
//			System.out.println("sending");
//			connection.publish(device, "hi".getBytes(), QoS.AT_LEAST_ONCE, false);
//			System.out.println("sent");
		}
	}

	public ThingStatus getStatus() {
		return status;
	}

	public void setStatus(ThingStatus status) {
		this.status = status;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(Object latitude) {
		this.latitude = latitude.toString();
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(Object longitude) {
		this.longitude = longitude.toString();
	}
}
