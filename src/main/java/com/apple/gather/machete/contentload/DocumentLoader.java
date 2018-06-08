package com.apple.gather.machete.contentload;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.apple.gather.machete.utils.MacheteUtils;
import com.apple.gather.machete.utils.RestCallResponse;
//import com.jivesoftware.api.core.v3.entities.content.*;

@Component
public class DocumentLoader extends BaseLoader implements Loader{
	
	
	public DocumentLoader() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DocumentLoader(String baseUrl, String spaceId, int numOfRecords) {
		super(baseUrl, spaceId, numOfRecords);
		// TODO Auto-generated constructor stub
	}
	private static final Logger log = Logger.getLogger(DocumentLoader.class);

	
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
	public int postAttachmentToDocument(String docId, List<String> fileNames) throws Exception {

		
		String url = baseUrl + "attachments/contents/" + docId;
		log.info("postDocument URL:" + url);
		
		RestCallResponse response = MacheteUtils.postMultiFilePart(url, fileNames);
		return response.getStatus();

	}
	public int postDocumentWithAttachment(List<String> fileNames) throws Exception {

		

		String content = createContent("GANESH TEST DOCUMENT", "GANESH TEST DOCUMENT TITLE"+Instant.now());

		log.info("CONTENT:" + content);
		
		String url = baseUrl + "contents?fields=id";
		log.info("postDocument URL:" + url);
		
		RestCallResponse response = MacheteUtils.post(url, content);
		String docIdCreated = null;
		if (response != null) {
			
			docIdCreated = MacheteUtils.getIdFromResponse(response.getEntityAsString());
			if (docIdCreated != null)
				return postAttachmentToDocument(docIdCreated, fileNames);
		}
		return response.getStatus();

	}

	public int post() throws AuthenticationException, ClientProtocolException, IOException {

		

		String content = createContent("GANESH TEST DOCUMENT", "GANESH TEST DOCUMENT TITLE"+Instant.now());

		//log.info("CONTENT:" + content);
		
		String url = baseUrl + "contents" + "?fields=id";
		log.info("postDocument URL:" + url);
		
		RestCallResponse response = MacheteUtils.post(url, content);
		
		String docIdCreated = null;
		if (response != null) {
			
			docIdCreated = MacheteUtils.getIdFromResponse(response.getEntityAsString());
			if (docIdCreated != null)
				System.out.println(docIdCreated);
				postComment(docIdCreated);
		}
		return response.getStatus();

	}
	private String createContent(String s, String title) {
		boolean hasAnAttachment = false;

		
		
		StringBuffer sb = new StringBuffer();
		sb.append("{\"content\":{ \"type\": \"text/html\",\"text\": \"<body><p>");
		sb.append(title);
		sb.append("<br />");
		sb.append(s);

		sb.append("</p></body>\"}, \"subject\": \"");
		sb.append(title);
		sb.append("\"");
		// TODO KGP
		// sb.append(", \"parent\":
		// \"https://gather8-it.corp.apple.com/api/core/v3/places/1020\"");
		//sb.append(", \"parent\": \"http://localhost:8080/api/core/v3/places/3016\"");
		sb.append(", \"parent\": \"");
		sb.append(baseUrl);
		sb.append("places/");
		sb.append(spaceId);
		sb.append("\"");
		// sb.append(", \"visibility\": \"people\"");
		sb.append(", \"restrictComments\": \"true\"");
		sb.append(", \"tags\": [\"AuditReport\"]");
		sb.append(", \"type\": \"document\" ");
		if (hasAnAttachment) {
			// file://///123.123.12.1/test/abc.xls
			// String fileurl = "file:////Users/ganeshk/Documents/test1.xlsx";
			// String fileurl =
			// "file:///Users/ganeshk/gather-workspace/gather/machete-plugin/test1.xlsx";
			String fileurl = "file:////Users/ganeshk/gather-workspace/gather/machete-plugin/test1.xlsx";
			// 403
			// String fileurl="file:///localhost:8080/Users/ganeshk/Documents/test1.xlsx";
			sb.append(",\"attachments\": [ {\"doUpload\": true,\"url\": \"");
			sb.append(fileurl);
			sb.append("\"");
			sb.append("} ]");
		}
		sb.append(" }");

		return sb.toString();

	}


}
