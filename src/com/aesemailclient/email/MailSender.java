package com.aesemailclient.email;

public class MailSender {
	MailAuthenticator authenticator;

	public MailSender() {
		// TODO Auto-generated constructor stub
		
		authenticator = new MailAuthenticator("januar.srt@gmail.com", "ibrani11:6", "smtp.gmail.com", "465", "465");
	}
	
	public Boolean send() {
		try {
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}

}
