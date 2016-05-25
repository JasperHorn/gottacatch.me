package com.gcm.gottacatchme.hello;

import com.gcm.gottacatchme.thingyclient.ThingyClient;

public class ThingStatusService implements IThingStatus
{
	private ThingyClient client;

	public ThingStatusService(ThingyClient client)
	{
		this.client = client;
	}
	
	@Override
	public ThingStatus thingstatus()
	{
		ThingStatus ts = new ThingStatus();
		
		ts.latitude = client.getLatitude();
		ts.longitude = client.getLongitude();
		ts.status = client.getStatus().toString();
		
		return ts;
	}
}
