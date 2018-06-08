package com.apple.gather.machete.contentload;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Component;

import com.apple.gather.machete.utils.MacheteUtils;
import com.apple.gather.machete.utils.RestCallResponse;

@Component
public class BaseLoader {
	public String baseUrl;
	public String spaceId;
	public int numOfRecords;
	
	public BaseLoader(String baseUrl, String spaceId, int numOfRecords) {
		super();
		this.baseUrl = baseUrl;
		this.spaceId = spaceId;
		this.numOfRecords = numOfRecords;
	}

	public BaseLoader() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int postComment(String docId) throws AuthenticationException, ClientProtocolException, IOException{
		StringBuilder  url = new StringBuilder();
		url.append(baseUrl);
		url.append("contents/");
		url.append(docId);
		url.append("/comments");
		StringBuilder sb = new StringBuilder();
		sb.append("{\"content\": {\"type\": \"text/html\", \"text\": \"This is Ganesh\'s greatest comment\"}}");
		RestCallResponse response = MacheteUtils.post(url.toString(), sb.toString());
		
		return response.getStatus();
	}
	
}
