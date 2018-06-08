package com.apple.gather.machete.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class MacheteUtils {
	private static final Logger log = Logger.getLogger(MacheteUtils.class);

	private static int timeout = 5;
	private static RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
			.setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();

	private MacheteUtils() {
		throw new AssertionError();
	}
	
	public static String toJSON(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = null;
		try {
			jsonInString = mapper.writeValueAsString(obj);
		} catch (IOException e) {
			log.error(e);
		}
		return jsonInString;
	}
	/*public static toObject<T>(String jsonInString, String className) {
		ObjectMapper mapper = new ObjectMapper();
		Class claz = Class.forName(className);
		T obj = mapper.readValue(jsonInString, claz);
	}*/

	public static String readString(InputStream inputStream) throws IOException {

		ByteArrayOutputStream into = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		for (int n; 0 < (n = inputStream.read(buf));) {
			into.write(buf, 0, n);
		}
		into.close();
		return new String(into.toByteArray(), "UTF-8"); // Or whatever encoding
	}

	public static RestCallResponse post(String url, String content)
			throws AuthenticationException, ClientProtocolException, IOException {
		CloseableHttpClient client = null;
		HttpPost httpPost = null;
		try {
			// TODO: pull out into properties

			// RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout *
			// 1000)
			// .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout *
			// 1000).build();
			client = HttpClientBuilder.create().setDefaultRequestConfig(MacheteUtils.config).build();
			httpPost = new HttpPost(url);

			// TODO: move to properties the userid pswd
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
			Header bs = new BasicScheme().authenticate(creds, httpPost, null);
			httpPost.addHeader(bs);
			StringEntity entity = new StringEntity(content);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			Header[] headers = httpPost.getAllHeaders();

			for (int i = 0; i < headers.length; i++) {
				Header h = headers[i];
				log.info("HEADER:" + h.getName() + ":" + h.getValue());
			}

			CloseableHttpResponse response = client.execute(httpPost);
			Optional<CloseableHttpResponse> respOptional = Optional.ofNullable(response);
			RestCallResponse resp = null;
			if (respOptional.isPresent()) {
				org.apache.http.HttpEntity ent = (org.apache.http.HttpEntity) response.getEntity();
				String entStr = MacheteUtils.readString(ent.getContent());
				log.info("RESPONSE ENTITY" + entStr);
				int status = response.getStatusLine().getStatusCode();
				resp = new RestCallResponse(ent, entStr, status);
			}
			return resp;

		} finally {
			if (httpPost != null)
				httpPost.releaseConnection();
			if (client != null)
				client.close();
		}
	}

	/*
	 * public static RestCallResponse postMultipart(String url, String content,
	 * String filename) throws AuthenticationException, ClientProtocolException,
	 * IOException { CloseableHttpClient client = null; HttpPost httpPost = null;
	 * File targetFile = new File(filename); try { // TODO: pull out into properties
	 * String str = "TEST"; Path path = Paths.get(filename); byte[] strToBytes =
	 * str.getBytes();
	 * 
	 * Files.write(path, strToBytes);
	 * 
	 * // Stream to read file FileInputStream fin;
	 * 
	 * try { // Open an input stream fin = new FileInputStream(filename);
	 * 
	 * java.nio.file.Files.copy(fin, targetFile.toPath(),
	 * StandardCopyOption.REPLACE_EXISTING); // Read a line of text //
	 * System.out.println( new DataInputStream(fin).readLine() );
	 * 
	 * // Close our input stream fin.close(); } // Catches any error conditions
	 * catch (IOException e) { System.err.println("Unable to read from file");
	 * System.exit(-1); } String read = Files.readAllLines(path).get(0);
	 * System.out.println("READ FILE:" + read);
	 * 
	 * // Content-Type: multipart/form-data; boundary=something
	 * 
	 * // build multipart upload request HttpEntity data =
	 * MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
	 * .addBinaryBody("filename", targetFile, ContentType.MULTIPART_FORM_DATA,
	 * targetFile.getName()) .addTextBody("document", content,
	 * ContentType.APPLICATION_JSON).build();
	 * 
	 * // build http request and assign multipart upload data HttpUriRequest request
	 * = RequestBuilder.post(url).setEntity(data).build();
	 * 
	 * client =
	 * HttpClientBuilder.create().setDefaultRequestConfig(MacheteUtils.config).build
	 * (); httpPost = new HttpPost(url);
	 * 
	 * // TODO: move to properties the userid pswd UsernamePasswordCredentials creds
	 * = new UsernamePasswordCredentials("admin", "admin"); Header bs = new
	 * BasicScheme().authenticate(creds, httpPost, null); request.addHeader(bs); //
	 * request.setHeader(HttpHeaders.CONTENT_TYPE, MULTIPART_MIXED_BOUNDARY);
	 * 
	 * request.setHeader("Accept", "application/json");
	 * request.setHeader("Content-Type", "multipart/form-data");
	 * 
	 * Header[] headers = request.getAllHeaders();
	 * 
	 * for (int i = 0; i < headers.length; i++) { Header h = headers[i];
	 * log.info("HEADER:" + h.getName() + ":" + h.getValue()); }
	 * 
	 * System.out.println("Executing request: " + request.getRequestLine());
	 * CloseableHttpResponse response = client.execute(request);
	 * Optional<CloseableHttpResponse> respOptional = Optional.ofNullable(response);
	 * RestCallResponse resp = null; if (respOptional.isPresent()) {
	 * org.apache.http.HttpEntity ent = (org.apache.http.HttpEntity)
	 * response.getEntity(); String entStr =
	 * MacheteUtils.readString(ent.getContent()); log.info("RESPONSE ENTITY" +
	 * entStr); int status = response.getStatusLine().getStatusCode(); resp = new
	 * RestCallResponse(ent, entStr, status); } return resp;
	 * 
	 * } finally { if (httpPost != null) httpPost.releaseConnection(); if (client !=
	 * null) client.close(); } }
	 */

	public static RestCallResponse get(String url)
			throws AuthenticationException, ClientProtocolException, IOException {
		CloseableHttpClient client = null;
		HttpGet httpGet = null;
		try {
			client = HttpClientBuilder.create().setDefaultRequestConfig(MacheteUtils.config).build();
			httpGet = new HttpGet(url);
			// TODO: move to properties the userid pswd
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials("admin", "admin");
			httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));

			CloseableHttpResponse response = client.execute(httpGet);

			Optional<CloseableHttpResponse> respOptional = Optional.ofNullable(response);
			RestCallResponse resp = null;
			if (respOptional.isPresent()) {
				org.apache.http.HttpEntity ent = (org.apache.http.HttpEntity) response.getEntity();
				String entStr = MacheteUtils.readString(ent.getContent());
				log.info("RESPONSE ENTITY" + entStr);
				int status = response.getStatusLine().getStatusCode();
				resp = new RestCallResponse(ent, entStr, status);
			}
			return resp;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	public static String preProcessCellContent(String s) {
		s = s.replaceAll("\"", "&quot;");
		return s;
	}

	/*
	 * public static RestCallResponse postMultipart2(String url, String content,
	 * String filename) {
	 * 
	 * try {
	 * 
	 * URL obj = new URL(url);
	 * 
	 * HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
	 * 
	 * conn.setRequestProperty("Content-Type",
	 * "multipart/form-data; boundary=SNIP");
	 * 
	 * conn.setDoOutput(true);
	 * 
	 * conn.setRequestMethod("POST");
	 * 
	 * String userpass = "admin" + ":" + "admin";
	 * 
	 * String basicAuth = "Basic " +
	 * javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8")
	 * );
	 * 
	 * conn.setRequestProperty("Authorization", basicAuth);
	 * 
	 * String dataA =
	 * "--SNIP\r\nContent-Disposition:  form-data; filename=\"Machine.xlsx\";name=\"foo\"\r\nContent-Type:application/json\r\n\r\n{\"type\":\"document\","
	 * +
	 * 
	 * "\"subject\":\"Machine\",\"content\":{\"type\":\"text/html\",\"text\":\"<body><p>Machine data</p></body>\"}}\r\n--SNIP\r\nContent-Disposition: form-data;"
	 * +
	 * 
	 * "filename=\"Machine.xlsx\";name=\"foo\"\r\nContent-Type: application/vnd.ms-excel\r\n\r\n"
	 * ;
	 * 
	 * String dataC = "\r\n--SNIP--\r\n";
	 * 
	 * PrintWriter writer2;
	 * 
	 * OutputStream outputStream2;
	 * 
	 * outputStream2 = conn.getOutputStream();
	 * 
	 * writer2 = new PrintWriter(new OutputStreamWriter(outputStream2, "UTF-8"),
	 * true);
	 * 
	 * writer2.append(dataA);
	 * 
	 * writer2.flush();
	 * 
	 * FileInputStream inputStream = new
	 * FileInputStream("/Users/ganeshk/Documents/test1.xlsx");
	 * 
	 * byte[] buffer = new byte[20096];
	 * 
	 * int bytesRead = -1;
	 * 
	 * while ((bytesRead = inputStream.read(buffer)) != -1) {
	 * 
	 * outputStream2.write(buffer, 0, bytesRead);
	 * 
	 * }
	 * 
	 * outputStream2.flush();
	 * 
	 * inputStream.close();
	 * 
	 * writer2.append(dataC).flush();
	 * 
	 * writer2.close();
	 * 
	 * List<String> response = new ArrayList<String>();
	 * 
	 * BufferedReader reader = new BufferedReader(new InputStreamReader(
	 * 
	 * conn.getInputStream()));
	 * 
	 * String line = null;
	 * 
	 * while ((line = reader.readLine()) != null) {
	 * 
	 * response.add(line);
	 * 
	 * }
	 * 
	 * reader.close();
	 * 
	 * conn.disconnect();
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace();
	 * 
	 * } return new RestCallResponse(); }
	 */

	/*
	 * public static RestCallResponse postMultipart3(String url, String content,
	 * String filename) throws IOException { String charset = "UTF-8";
	 * 
	 * File uploadFile1 = new File(filename); String requestURL = url;
	 * 
	 * try { MultiPartUtility multipart = new MultiPartUtility(requestURL, charset);
	 * // multipart.addHeaderField("Content-Type", "multipart/form-data; //
	 * boundary=SNIP");
	 * 
	 * multipart.addFormField4("json", content); multipart.addFormField("file1", "@"
	 * + filename); List<String> response = multipart.finish2();
	 * 
	 * // multipart.addDocumentPart("json",content); //
	 * multipart.addFilePart("file1", uploadFile1); // List<String> response =
	 * multipart.finish();
	 * 
	 * System.out.println("SERVER REPLIED:");
	 * 
	 * for (String line : response) { System.out.println(line); } } catch
	 * (IOException ex) { System.err.println(ex); } return new RestCallResponse(); }
	 */

	public static RestCallResponse postMultiFilePart(String url, List<String> filenames) throws IOException {

		try {
			MultiPartUtility multipart = new MultiPartUtility(url);
			for (int i = 0; i < filenames.size(); i++) {
				File uploadFile1 = new File(filenames.get(i));
				multipart.addFilePartNew("file" + i, uploadFile1);
			}
			List<String> response = multipart.finish2();
			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println("RESPONSE FROM POST MULTIPART4:" + line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return new RestCallResponse();
	}

	public static String getIdFromResponse(String response) {
		if (response == null) {
			return null;
		}
		String docIdCreated = null;
		Pattern MY_PATTERN = Pattern.compile("contents/(\\d+)");
		Matcher m = MY_PATTERN.matcher(response);
		while (m.find()) {
			String val = response.substring(m.start(), m.end());
			String[] vals = val.split("/");
			//System.out.println(vals[0] + ":" + vals[1]);
			vals[1] = vals[1].trim();
			vals[1] = vals[1].replaceAll("\"", "");
			docIdCreated = vals[1];

		}
		return docIdCreated;
	}

}
