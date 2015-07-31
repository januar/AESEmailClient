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
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import com.aesemailclient.db.UserEntity;
import com.sun.mail.imap.IMAPFolder;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

public class MailReader {
	private String TAG = "mail.reader";
	MailAuthenticator authenticator;

	public IMAPFolder inbox;
	public static String LOG;
	public static final int NEW = 1;
	public static final int OLD = 2;

	public MailReader(UserEntity user) {
		authenticator = new MailAuthenticator(user.getEmail(),
				user.getPassword(), MailAuthenticator.getHostByEmailType(
						user.getEmailType(), "imap"), "465", "465");
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
			store.connect(authenticator.getHost(), -1, auth.getUserName(),
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

	public Boolean login() {

		try {
			LOG = "No found data.";
			PasswordAuthentication auth = authenticator
					.getPasswordAuthentication();
			Properties props = System.getProperties();
			props.setProperty("mail.imap.ssl.enable", "true");
			Session session = Session.getInstance(props, null);
			session.setDebug(true);

			Store store;
			store = session.getStore("imaps");
			store.connect(authenticator.getHost(), -1, auth.getUserName(),
					auth.getPassword());
			return true;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		}catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
			LOG = e.getMessage();
		}

		return false;
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

			// Message[] orderMsg = new Message[msg.length];
			// for (int i = 0; i < orderMsg.length; i++) {
			// orderMsg[i] = msg[(msg.length - 1) - i];
			// }
			return msg;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return null;
	}
	
	public Message[] getMail(Long UUID, int status){
		try {
			init();
			Message [] msg = null;
			
			Log.i(TAG, "UUID : " + UUID + " status: " + status);
			if (status == MailReader.OLD) {
				msg = inbox.getMessagesByUID(UUID - 10, UUID-1);
			}else if(status == MailReader.NEW)
			{
				Message last = inbox.getMessage(inbox.getMessageCount());
				Long newUUID = inbox.getUID(last);
				Log.i(TAG, "newUUID : " + newUUID);
				if (newUUID > UUID) {
					msg = inbox.getMessagesByUID(UUID+1, newUUID);
				}
			}

			return msg;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return null;
	}
	
	public Message[] getMail() {
		try {
			init();
			int email_count = inbox.getMessageCount();
			Message[] msg = inbox.getMessages(email_count-9, email_count);
			return msg;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
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
			LOG = e.getMessage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOG = e.getMessage();
		}

		return msg;
	}

	public String GetEmailContent(Part p) {
		String content = "";

		try {
			/*if (p.isMimeType("text/*")) {
				content = (String) p.getContent();
			} else if (p.isMimeType("multipart/*")) {
				Multipart mp = (Multipart) p.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++) {
					content = GetEmailContent(mp.getBodyPart(i));
					if (content != "")
						break;
				}
			}*/
			
			if (p.isMimeType("text/*")) {
	            String s = (String)p.getContent();
//	            textIsHtml = p.isMimeType("text/html");
	            return s;
	        }

	        if (p.isMimeType("multipart/alternative")) {
	            // prefer html text over plain text
	            Multipart mp = (Multipart)p.getContent();
	            String text = null;
	            for (int i = 0; i < mp.getCount(); i++) {
	                Part bp = mp.getBodyPart(i);
	                if (bp.isMimeType("text/plain")) {
	                    if (text == null)
	                        text = GetEmailContent(bp);
	                    continue;
	                } else if (bp.isMimeType("text/html")) {
	                    String s = GetEmailContent(bp);
	                    if (s != null)
	                        return s;
	                } else {
	                    return GetEmailContent(bp);
	                }
	            }
	            return text;
	        } else if (p.isMimeType("multipart/*")) {
	            Multipart mp = (Multipart)p.getContent();
	            for (int i = 0; i < mp.getCount(); i++) {
	                String s = GetEmailContent(mp.getBodyPart(i));
	                if (s != null)
	                    return s;
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
