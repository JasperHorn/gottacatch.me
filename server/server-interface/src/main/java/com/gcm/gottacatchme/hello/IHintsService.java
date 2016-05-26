package com.gcm.gottacatchme.hello;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public interface IHintsService {
	
	public class Hint
	{
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		String text;
		String imageUrl;
	}
	
	@GET
	@Path("/hints")
	Hint[] getHints();
	
	@POST
	@Path("/nexthint")
	String addNextHint();
	
	@POST
	@Path("/resethints")
	String resetHints();
}
