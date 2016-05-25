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
		
		while(true)
		{
			System.out.println("Waiting for a message");
			Message message = connection.receive();
			System.out.println(message.getTopic());
			byte[] payload = message.getPayload();
			System.out.println("Got message of " + payload.length + " bytes");
			
			HashMap<String,Object> result = new ObjectMapper().readValue(payload, HashMap.class);
			
			HashMap<String,Object> stuff = (HashMap<String,Object>)result.get("fields");
			if (stuff.get("msgtype").equals("login"))
			{
				System.out.println("Got a login message; login was " + (stuff.get("success").equals("true") ? "successful":"Unsuccessful"));
			}
			else
			{
				System.out.println("Got an unrecognized message!");
			}
			
			System.out.println();
			// process the message then:
			message.ack();
		}
	}
}
