package com.aesemailclient.email;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	MailAuthenticator authenticator;
	
	public static String LOG;

	public MailSender() {
		// TODO Auto-generated constructor stub
		
		authenticator = new MailAuthenticator("januar.srt@gmail.com", "ibrani11:6", "smtp.gmail.com", "465", "465");
	}
	
	public Boolean send(String from, String to, String subject, String content) {
		try {
			LOG = "Unknow error.";
			Properties props = System.getProperties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			props.put("mail.debug", "true");
			
			Session session = Session.getDefaultInstance(props, authenticator);
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
			return true;
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOG = e.getMessage();
		}
		
		return false;
	}

}
