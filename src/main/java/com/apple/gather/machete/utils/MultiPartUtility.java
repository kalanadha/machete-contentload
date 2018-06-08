package com.apple.gather.machete.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MultiPartUtility {
	private String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpURLConnection httpConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;
	StringWriter strWriter;

	public MultiPartUtility(String requestURL) throws IOException {
		boundary = "SNIP";
		String charset = "UTF-8";
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);

		httpConn.setRequestProperty("Content-Type", "multipart/form-data");
		String userCredentials = "admin:admin";
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
		httpConn.setRequestProperty("Authorization", basicAuth);
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
		strWriter = new StringWriter();
	}

	public MultiPartUtility(String requestURL, String charset) throws IOException {
		this.charset = charset;

		// creates a unique boundary based on time stamp
		boundary = "SNIP";
		// boundary ="SNIP" + System.currentTimeMillis() + "---";

		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
		httpConn.setDoInput(true);
		// httpConn.setRequestProperty("Content-Type",
		// "multipart/form-data; boundary=" + boundary);
		httpConn.setRequestProperty("Content-Type", "undefined");
		// httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
		// httpConn.setRequestProperty("Test", "Bonjour");
		String userCredentials = "admin:admin";
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
		httpConn.setRequestProperty("Authorization", basicAuth);
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
		strWriter = new StringWriter();
	}

	public void addFormFieldOrig(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		strWriter.write("--");
		strWriter.write(boundary);
		strWriter.write(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
		strWriter.write("Content-Disposition: form-data; name=\"");
		strWriter.write(name);
		strWriter.write("\"");
		strWriter.write(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);

		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	public void addFormField(String name, String value) {
		writer.append("\"");
		writer.append(name + "=");
		writer.append(value);
		writer.append("\"");
		writer.append(LINE_FEED);
		writer.flush();
	}

	public void addFormField4(String name, String value) {

		writer.append("'");
		writer.append(name + "=");
		writer.append(value);
		writer.append(";type=application/json");
		writer.append("'");
		writer.append(LINE_FEED);
		writer.flush();
	}

	
	public void addFilePart(String fieldName, File uploadFile) throws IOException {
		String fileName = uploadFile.getName();
		writer.append("--" + boundary).append(LINE_FEED);

		writer.append("Content-Disposition: form-data; ");
		writer.append("name=\"" + fieldName);
		writer.append("\";");
		writer.append("filename=\"" + fileName + "\"").append(LINE_FEED);

		/*
		 * writer.append( "Content-Disposition: form-data; name=\"" + fieldName +
		 * "\"; filename=\"" + fileName + "\"") .append(LINE_FEED);
		 */
		String ctype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String ct = URLConnection.guessContentTypeFromName(fileName) == null ? ctype
				: URLConnection.guessContentTypeFromName(fileName);
		writer.append("Content-Type: " + ct).append(LINE_FEED);

		// writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		// strWriter.write("Content-Transfer-Encoding: binary");
		writer.append(LINE_FEED);
		writer.flush();

		FileInputStream inputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		// writer.append(LINE_FEED);
		// strWriter.write(LINE_FEED);
		writer.flush();
	}

	public void addHeaderField(String name, String value) {
		writer.append(name + ": " + value).append(LINE_FEED);
		writer.flush();
	}

	public List<String> finish() throws IOException {
		List<String> response = new ArrayList<String>();
		// CONTENT\r\n--SNIP--\r\n'
		writer.append("CONTENT" + LINE_FEED + "--" + boundary + "--" + LINE_FEED);

		String sentMsg = strWriter.toString();
		System.out.println(sentMsg);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			reader.close();
			httpConn.disconnect();
		} else {
			// Object obj = httpConn.getContent();

			throw new IOException("Server returned non-OK status: " + status);
		}

		return response;
	}

	public List<String> finish2() throws IOException {
		List<String> response = new ArrayList<String>();
		writer.append(LINE_FEED + "--" + boundary + "--" + LINE_FEED);
		writer.close();

		// checks server's status code first
		int status = httpConn.getResponseCode();
		if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.add(line);
			}
			reader.close();
			httpConn.disconnect();
		} else {
			// Object obj = httpConn.getContent();

			throw new IOException("Server returned non-OK status: " + status);
		}

		return response;
	}

	public void addDocumentPart(String name, String content) {
		writer.append("--" + boundary).append(LINE_FEED);

		writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);

		writer.append(LINE_FEED);

		writer.append("Content-Type: application/json");

		writer.append(LINE_FEED);

		writer.append(LINE_FEED);

		writer.append(content).append(LINE_FEED);

		writer.flush();

	}

	public void addFilePartNew(String fieldName, File uploadFile) throws IOException {
		String fileName = uploadFile.getName();

		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data;");

		writer.append("name=\"" + fieldName);
		writer.append("\";");
		writer.append("filename=\"" + fileName + "\"");
		writer.append(LINE_FEED);

		String ctype = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String ct = URLConnection.guessContentTypeFromName(fileName) == null ? ctype
				: URLConnection.guessContentTypeFromName(fileName);
		writer.append("Content-Type: " + ct).append(LINE_FEED);

		writer.append(LINE_FEED);

		writer.flush();

		FileInputStream inputStream = new FileInputStream(uploadFile);
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		writer.flush();
	}
}