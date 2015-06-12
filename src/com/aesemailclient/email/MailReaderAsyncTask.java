package com.aesemailclient.email;

import javax.mail.Message;

import android.app.Activity;
import android.os.AsyncTask;

public class MailReaderAsyncTask extends AsyncTask<String, String, Boolean> {
	
	Activity activity;
	
	public MailReaderAsyncTask(Activity _activity) {
		// TODO Auto-generated constructor stub
		this.activity = _activity;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		MailReader mailReader = new MailReader(activity);
		Message msg = mailReader.getMail();
		
		return true;
	}

}
