package com.aesemailclient.db;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InboxEntity implements Serializable{
	private int id_inbox;
	private String subject;
	private String from;
	private String to;
	private String date;
	private String content;
	private Long uuid;
	private Boolean isdownload;
	
	public InboxEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public InboxEntity(int id, String subject, String from, String to, String date, String content, Long uuid, Boolean isdownload){
		this.id_inbox = id;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.date = date;
		this.content = content;
		this.isdownload = isdownload;
		this.uuid = uuid;
	}
	
	// setter getter
	public void setId(int id) {
		id_inbox = id;
	}
	public int getId() {
		return id_inbox;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSubject() {
		return this.subject;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	public String getFrom() {
		return this.from;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	public String getTo() {
		return this.to;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return this.date;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return this.content;
	}
	
	public void setDownload(Boolean download) {
		this.isdownload = download;
	}
	public Boolean isDownload() {
		return this.isdownload;
	}
	
	public void setUUID(Long uuid) {
		this.uuid = uuid;
	}
	public Long getUUID() {
		return this.uuid;
	}
}
