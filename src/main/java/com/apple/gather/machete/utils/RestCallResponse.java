package com.apple.gather.machete.utils;

import org.apache.http.HttpEntity;

public class RestCallResponse {
		private org.apache.http.HttpEntity entity;
	private String entityAsString;
	private int status;
	
	public RestCallResponse(HttpEntity entity, String entityAsString, int status) {
		super();
		this.entity = entity;
		this.entityAsString = entityAsString;
		this.status = status;
	}
	public RestCallResponse() {
		// TODO Auto-generated constructor stub
	}
	public org.apache.http.HttpEntity getEntity() {
		return entity;
	}
	public void setEntity(org.apache.http.HttpEntity entity) {
		this.entity = entity;
	}
	public String getEntityAsString() {
		return entityAsString;
	}
	public void setEntityAsString(String entityAsString) {
		this.entityAsString = entityAsString;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	


}
