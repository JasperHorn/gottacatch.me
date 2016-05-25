package gottacatch.me;

import java.util.HashMap;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import com.fasterxml.jackson.databind.ObjectMapper;

class ThingyClient
{
	public static void main(String... args) throws Exception{
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
				System.out.println("Got a login message; login was " + (stuff.get("success").equals("true") ? "successful":"Unsuccessful"));
			}
			else if (stuff.get("msgtype").equals("location"))
			{
				System.out.println("Got a location message; location was (" + 
						stuff.get("latitude") + ", " + stuff.get("longitude") + ")");
			}
			else
			{
				System.out.println("Got an unrecognized message!");
			}
			
			// process the message then:
			message.ack();
			
//			System.out.println("sending");
//			connection.publish(device, "hi".getBytes(), QoS.AT_LEAST_ONCE, false);
//			System.out.println("sent");
		}
	}
}
