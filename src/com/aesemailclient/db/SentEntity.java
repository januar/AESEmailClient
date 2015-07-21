package com.aesemailclient.db;

public class SentEntity {
	
	private int id_sent;
	private String subject;
	private String from;
	private String to;
	private String content;
	private String date;

	public SentEntity() {
		// TODO Auto-generated constructor stub
	}
	
	public SentEntity(int id, String subject, String from, String to, String content, String date){
		this.id_sent = id;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.content = content;
		this.date = date;
	}
	
	public SentEntity(String subject, String from, String to, String content, String date){
		this(0, subject, from, to, content, date);
	}
	
	//setter getter
	public void setId(int id) {
		this.id_sent = id;
	}
	public int getId() {
		return this.id_sent;
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
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return this.content;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return this.date;
	}

}
