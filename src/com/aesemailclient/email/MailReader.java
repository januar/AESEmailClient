package com.aesemailclient.email;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.NewsAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import com.sun.mail.util.MailSSLSocketFactory;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

public class MailReader {
	private String TAG = "com.aesemailclient"; 
	private Context context;
	MailAuthenticator authenticator;
	
	public MailReader(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		authenticator = new MailAuthenticator("januar.srt@gmail.com", "ibrani11:6", "smtp.gmail.com", "465", "465");
	}
	
	public Message[] getMail() {
		try {
//			MailSSLSocketFactory sf = new MailSSLSocketFactory();
//			sf.setTrustAllHosts(true);
			
			PasswordAuthentication auth = authenticator.getPasswordAuthentication();
			Properties props = System.getProperties();
			Session session = Session.getInstance(props, null);
			session.setDebug(true);
			
			Store store = session.getStore("imaps");
//			props.put("mail.debug", true);
//			props.put("mail.store.protocol", "imaps");
//			props.setProperty("mail.imap.host", "imap.gmail.com");
//			props.setProperty("mail.imap.port", "993");
//			props.setProperty("mail.imap.connectiontimeout", "5000");
//			props.setProperty("mail.imap.timeout", "5000");
//			props.put("mail.imap.ssl.enable", "true");
//			props.put("mail.protocol.ssl.trust", "imap.gmail.com");
//			props.put("mail.imap.ssl.socketFactory", sf);
//			props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			
            
            store.connect("imap.gmail.com", -1, auth.getUserName(), auth.getPassword());
            Folder inbox = store.getDefaultFolder();
            inbox = inbox.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
//            int emailcount = inbox.getMessageCount();
//            Message[] msg = inbox.getMessages(emailcount-9, emailcount);
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            Date now = cal.getTime();
            cal.add(Calendar.DATE, -2);
            SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, now);
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, cal.getTime());
            Message[] msg = inbox.search(new AndTerm(olderThan, newerThan));
            Log.i(TAG, "length : " + msg.length);
            
            Message[] orderMsg = new Message[msg.length];
            for (int i = 0; i < orderMsg.length; i++) {
				orderMsg[i] = msg[(msg.length - 1) - i];
			}
            return orderMsg;
		}catch(AuthenticationFailedException ae){
			Log.e(TAG, ae.getMessage());
		}catch(NetworkOnMainThreadException ne){
			Log.e(TAG, ne.getMessage());
		}
		catch (RuntimeException re) {
			Log.e(TAG, re.getMessage());
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
		return null;
	}
}
