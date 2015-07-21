package com.aesemailclient.email;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import com.sun.mail.imap.IMAPFolder;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class MailReader {
	private String TAG = "mail.reader";
	MailAuthenticator authenticator;

	public IMAPFolder inbox;
	public static String LOG;

	public MailReader() {
		authenticator = new MailAuthenticator("januar.srt@gmail.com",
				"ibrani11:6", "smtp.gmail.com", "465", "465");
	}

	private void init() {
		try {
			LOG = "No found data.";
			PasswordAuthentication auth = authenticator
					.getPasswordAuthentication();
			Properties props = System.getProperties();
			props.setProperty("mail.imap.ssl.enable", "true");
			Session session = Session.getInstance(props, null);
			session.setDebug(true);

			Store store = session.getStore("imaps");
			store.connect("imap.gmail.com", -1, auth.getUserName(),
					auth.getPassword());

			inbox = (IMAPFolder) store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
		} catch (AuthenticationFailedException ae) {
			Log.e(TAG, ae.getMessage());
			LOG = ae.getMessage();
		} catch (NetworkOnMainThreadException ne) {
			Log.e(TAG, ne.getMessage());
			LOG = ne.getMessage();
		} catch (RuntimeException re) {
			Log.e(TAG, re.getMessage());
			LOG = re.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
			LOG = e.getMessage();
		}
	}

	public Message[] getMail(Date from, Date to) {
		try {
			init();
			Calendar cal = Calendar.getInstance();
			cal.setTime(from);
			cal.add(Calendar.DATE, 1);
			from = cal.getTime();

			cal.setTime(to);
			cal.add(Calendar.DATE, 1);
			to = cal.getTime();
			SearchTerm olderThan = new ReceivedDateTerm(ComparisonTerm.LT, from);
			SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, to);
			Message[] msg = inbox.search(new AndTerm(olderThan, newerThan));
			Log.i(TAG, "from : " + from.toString() + " to : " + to.toString());
			Log.i(TAG, "length : " + msg.length);

//			Message[] orderMsg = new Message[msg.length];
//			for (int i = 0; i < orderMsg.length; i++) {
//				orderMsg[i] = msg[(msg.length - 1) - i];
//			}
			return msg;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return null;
	}

	public Message GetEmailByUUID(Long UUID) {
		init();
		Message msg = null;
		try {
			msg = inbox.getMessageByUID(UUID);
			if (msg == null) {
				SearchTerm term = new MessageIDTerm(String.valueOf(UUID));
				Message[] message = inbox.search(term);
				if (message.length > 0) {
					msg = message[0];
				}
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
	}

	public String GetEmailContent(Part p) {
		String content = "";

		try {
			if (p.isMimeType("text/plain") || p.isMimeType("text/html")) {
				content = (String) p.getContent();
			} else if (p.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) p.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++) {
					content = GetEmailContent(mp.getBodyPart(i));
					if (content != "")
						break;
				}
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}
}
