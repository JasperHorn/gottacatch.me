package com.gcm.gottacatchme.hello;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/thingstatus")
public interface IThingStatus
{
	public class ThingStatus
	{
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		String latitude;
		String longitude;
		String status;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ThingStatus thingstatus();
}
