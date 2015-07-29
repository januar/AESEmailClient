package com.aesemailclient.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {

	private String user;
	private String password;

	private String host;
	private String port;
	private String sport;

	public MailAuthenticator() {
		// TODO Auto-generated constructor stub
	}

	public MailAuthenticator(String _user, String _password, String _host,
			String _port, String _sport) {
		// TODO Auto-generated constructor stub
		this();
		this.user = _user;
		this.password = _password;
		this.host = _host;
		this.port = _port;
		this.sport = _sport;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		// TODO Auto-generated method stub
		return new PasswordAuthentication(this.user, this.password);
	}

	public String getHost() {
		return this.host;
	}

	public Properties setProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.socketFactory.port", sport);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		return props;
	}

	public static String getHostByEmailType(String email_type, String type) {
		String host = "";
		if (email_type.equals("gmail")) {
			if (type.equals("imap")) {
				host = "imap.gmail.com";
			} else if (type.equals("smtp")) {
				host = "smtp.gmail.com";
			}
		} else if (email_type.equals("yahoo")) {
			if (type.equals("imap")) {
				host = "imap.gmail.com";
			} else if (type.equals("smtp")) {
				host = "smtp.gmail.com";
			}
		}

		return host;
	}

}
