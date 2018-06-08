package com.apple.gather.machete.contentload;

import java.io.IOException;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Component;

import com.apple.gather.machete.utils.MacheteUtils;
import com.apple.gather.machete.utils.RestCallResponse;

@Component
public class IdeaLoader extends BaseLoader implements Loader{
	

	public IdeaLoader() {
		super();
		// TODO Auto-generated constructor stub
	}
	public IdeaLoader(String baseUrl, String spaceId, int numOfRecords) {
		super(baseUrl, spaceId, numOfRecords);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void load() {
			try {
				for (int i=0 ; i< numOfRecords ; i++ ) {
					post();
				}
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public int post() throws AuthenticationException, ClientProtocolException, IOException {

		StringBuilder sb = new StringBuilder();
		sb.append("{ ");
				sb.append("  \"type\" : \"idea\",\n" + 
				"   \"subject\" : \"Idea  Apple will touch trillion\",\n" + 
				"   \"visibility\" : \"place\"");
		sb.append(", \"parent\": \"");
		sb.append(baseUrl);
		sb.append("places/");
		sb.append(spaceId);
		sb.append("\"");	
		sb.append( "}");
		String url = baseUrl+"contents";
		RestCallResponse response = MacheteUtils.post(url, sb.toString());
		String docIdCreated = null;
		if (response != null) {
			
			docIdCreated = MacheteUtils.getIdFromResponse(response.getEntityAsString());
			if (docIdCreated != null)
				System.out.println(docIdCreated);
				postComment(docIdCreated);
		}
		return response.getStatus();
		

	}

}
