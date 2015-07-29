package com.aesemailclient.db;

public class UserEntity {
	
	private String email;
	private String password;
	private String email_type;
	
	public UserEntity() {
		// TODO Auto-generated constructor stub
		this.email = "";
		this.password = "";
		this.email_type = "";
	}
	
	public UserEntity(String email, String password, String email_type){
		this.email = email;
		this.password = password;
		this.email_type = email_type;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setEmailType(String email_type) {
		this.email_type = email_type;
	}
	
	public String getEmailType() {
		return this.email_type;
	}

}
