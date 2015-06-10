package com.aesemailclient;

public class InboxItem {
	
	String Subject;
	String EmailDetail;
	int imgId;
	
	public InboxItem() {
		// TODO Auto-generated constructor stub
	}
	
	public InboxItem(String subject, String emailDetail, int imgId) {
		super();
		this.Subject = subject;
		this.EmailDetail = emailDetail;
		this.imgId = imgId;
	}
	
	public String getSubject() {
		return this.Subject;
	}
	
	public void setSubject(String subject) {
		this.Subject = subject;
	}
	
	public String getEmailDetail() {
		return this.EmailDetail;
	}
	
	public void setEmailDetail(String emailDetail) {
		this.EmailDetail = emailDetail;
	}
	
	public int getImgResID() {
		return imgId;
	}

	public void setImgResID(int imgResID) {
		this.imgId = imgResID;
	}

}
