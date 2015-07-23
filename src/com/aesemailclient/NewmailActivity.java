package com.aesemailclient;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aesemailclient.db.SentDataSource;
import com.aesemailclient.db.SentEntity;
import com.aesemailclient.email.MailSender;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class NewmailActivity extends Activity {
	
	private SentDataSource datasource;
	
	private EditText txt_from;
	private EditText txt_to;
	private EditText txt_subject;
	private EditText txt_content;
	
	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newmail);
		// Show the Up button in the action bar.
		setupActionBar();
		datasource = new SentDataSource(this);
		datasource.open();
		
		txt_from = (EditText)findViewById(R.id.txt_from);
		txt_from.setText("januar.srt@gmail.com");
		txt_from.setKeyListener(null);
		
		txt_to = (EditText) findViewById(R.id.txt_to);
		txt_subject = (EditText) findViewById(R.id.txt_subject);
		txt_content = (EditText) findViewById(R.id.txt_content);
		
		progress = new ProgressDialog(this);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.newmail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_send_now:
			if(txt_to.getText().toString().trim() == "")
			{
				Toast.makeText(this, "Please insert email address of reciver!", Toast.LENGTH_SHORT).show();
				return false;
			}else if(txt_subject.getText().toString().trim() == "")
			{
				Toast.makeText(this, "Please insert email subject!", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			new SendAsyncTask(this).execute();
			return true;
		
		case R.id.action_encrypt:
			new EncryptDialog().show(getFragmentManager(), "TAG");
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private class SendAsyncTask extends AsyncTask<String, String, Boolean>
	{
		private Activity activity;
		
		public SendAsyncTask(Activity activity){
			this.activity = activity;
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			String from = txt_from.getText().toString().trim();
			String to = txt_to.getText().toString().trim();
			String subject = txt_subject.getText().toString();
			String content = txt_content.getText().toString();
			
			MailSender sender = new MailSender();
			SimpleDateFormat sdf = new SimpleDateFormat(InboxFragment.DATE_FORMAT);
			String date = sdf.format(new Date());
			if (sender.send(from, to, subject, content)) {
				try {
					SentEntity sent = new SentEntity(subject, from, to, content, date);
					datasource.save(sent);
					datasource.close();
					return true;
				} catch (Exception e) {
					// TODO: handle exception
					MailSender.LOG = e.getMessage();
					return false;
				}
			} else {
				return false;
			}
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress.setCancelable(false);
			progress.setMessage("sending");
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.show();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progress.dismiss();
			if (result) {
				activity.finish();
			} else {
				Toast.makeText(activity, MailSender.LOG, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
