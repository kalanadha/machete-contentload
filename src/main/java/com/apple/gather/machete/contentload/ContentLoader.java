package com.apple.gather.machete.contentload;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

import com.apple.gather.machete.utils.MacheteUtils;
import com.apple.gather.machete.utils.RestCallResponse;
//import com.jivesoftware.api.core.v3.entities.AbstractEntity;
//import com.jivesoftware.api.core.v3.entities.places.SpaceEntity;

@ComponentScan(basePackages = "com.apple.gather.machete")
public class ContentLoader {
	private static final Logger log = Logger.getLogger(ContentLoader.class);

	private static final String path = "/api/core/v3/";
	
	//rn2-gathert-lapp31.rno.apple.com:11443/

	 @Value( "${jive.api.protocol:https}" )
	//@Value("${jive.api.protocol:http}")
	public String protocol;
	//@Value("${jive.api.hostname:localhost}")
	 @Value( "${jive.api.hostname:gather9-sh-it.corp.apple.com}")
	public String hostname;
	//@Value("${jive.api.port:8080}")
	// @Value( "${jive.api.port:11443}" )
	public String port;
	 
	 @Value( "${gather.machete.contentload.numRecords:10}" )
	 public int numOfRecords;
	

	public String baseUrl;
	public String spaceId;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ContentLoader cl = new ContentLoader();
		cl.setBaseUrl();
		cl.setMacheteSpaceId();
		cl.setNumOfRecords(100);
		
		cl.generateContent();
	}
	public int getNumOfRecords() {
		return numOfRecords;
	}

	public void setNumOfRecords(int numOfRecords) {
		this.numOfRecords = numOfRecords;
	}
	@Autowired
	DocumentLoader documentLoader;
	@Autowired
	DiscussionLoader discussionLoader;
	private void generateContent() {
		//documentLoader.load(baseUrl);
		DiscussionLoader discussionLoader = new DiscussionLoader(baseUrl, spaceId, numOfRecords);
		PollLoader pollLoader = new PollLoader(baseUrl, spaceId, numOfRecords);
		DocumentLoader documentLoader = new DocumentLoader(baseUrl, spaceId, numOfRecords);
		if (discussionLoader!=null) {
			discussionLoader.load();
		}else {
			System.out.println("DISCUSSIONLOADER IS NULL");
		}
		if (pollLoader!=null) {
			pollLoader.load();
		}else {
			System.out.println("POLLLOADER IS NULL");
		}
		if (documentLoader!=null) {
			documentLoader.load();
		}else {
			System.out.println("DOCUMENTLOADER IS NULL");
		}
		
	}

	private void setBaseUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append("://");
		sb.append(hostname);
		if (port != null) {
			if (port.length() > 0) {
				sb.append(":" + port);
			}
		}
		sb.append(path);
		//TODO KGP
		//baseUrl = sb.toString();
		//baseUrl = "https://rn2-gathert-lapp31.rno.apple.com:11443/api/core/v3/";
		//IT
		baseUrl = "https://gather8-it.corp.apple.com/api/core/v3/";
		//local
		//baseUrl = "http://localhost:8080/api/core/v3/";
		
	}

	public  void setMacheteSpaceId() {
		//IT machete space
		spaceId = "46994";
		//local
		//spaceId = "1404";
		//String url = baseUrl + "search/places?filter=type(place)&search(machete)";
		
		/*String url = baseUrl + "places?filter=type(space)";
		System.out.println("URL:"+url);
		try {
			RestCallResponse rcr = MacheteUtils.get(url);
			String s = rcr.getEntityAsString();
			System.out.println(s);
			if (s.indexOf("Machete")>0) {
				System.out.println("MACHETE FOUND");
			}
			//AbstractEntity[] myObjects = new ObjectMapper().readValue(rcr.getEntityAsString(), AbstractEntity[].class);
			//PlaceEntity pe = new ObjectMapper().readValue(rcr.getEntityAsString(), PlaceEntity.class);
			//return pe.getPlaceID();
		} catch (AuthenticationException | IOException e) {
			log.error(e);
		}*/
		
		//return spaceId;
	}
	//Space se = new SpaceEntity();
	

}
