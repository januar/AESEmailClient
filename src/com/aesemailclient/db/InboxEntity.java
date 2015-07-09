package com.aesemailclient.db;

public class InboxEntity {
	private int id_inbox;
	private String subject;
	private String from;
	private String to;
	private String date;
	private String content;
	private Boolean isread;
	
	public InboxEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public InboxEntity(int id, String subject, String from, String to, String date, String content, Boolean isread){
		this.id_inbox = id;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.date = date;
		this.content = content;
		this.isread = isread;
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
	
	public void setRead(Boolean read) {
		this.isread = read;
	}
	public Boolean isRead() {
		return this.isread;
	}

}
