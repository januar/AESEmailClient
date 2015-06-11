package com.aesemailclient.email;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MailReader {
	private Context context;
	
	MailAuthenticator authenticator;
	
	public MailReader(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		authenticator = new MailAuthenticator("januar.srt@gmail.com", "ibrani11:6", "smtp.gmail.com", "465", "465");
	}
	
	public Message getMail() {
		try {
			PasswordAuthentication auth = authenticator.getPasswordAuthentication();
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
//			props.setProperty("mail.imap.host", "imap.gmail.com");
			Session session = Session.getDefaultInstance(props, null);
			
            Store store = session.getStore("imaps");
            
            store.connect("imap.gmail.com", auth.getUserName(), auth.getPassword());
//            store.connect();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message msg = inbox.getMessage(10);
            return msg;
		}catch(AuthenticationFailedException ae){
			Toast.makeText(context, ae.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	
	public boolean authenticate() {
		try {
			PasswordAuthentication auth = this.authenticator.getPasswordAuthentication();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e("mail", e.getMessage());
		}
		return true;
	}

}
