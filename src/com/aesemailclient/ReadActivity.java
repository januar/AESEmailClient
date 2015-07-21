package com.aesemailclient;

import javax.mail.Message;

import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.email.MailReader;
import com.aesemailclient.textdrawable.TextDrawable;
import com.aesemailclient.textdrawable.util.ColorGenerator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReadActivity extends Activity {
	
	private InboxDataSource datasource;
	
	TextView email_subject;
	TextView email_from;
	TextView email_to;
	TextView email_date;
	ImageView email_avatar;
	WebView email_content;
	ProgressBar email_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datasource = new InboxDataSource(this);
		
		setContentView(R.layout.activity_read);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		InboxEntity entity = (InboxEntity) bundle.getSerializable(InboxFragment.INBOX_ENTITY);
		
		email_subject = (TextView)findViewById(R.id.email_subject);
		email_from = (TextView)findViewById(R.id.email_from);
		email_to = (TextView)findViewById(R.id.email_to);
		email_date = (TextView)findViewById(R.id.email_date);
		email_avatar = (ImageView)findViewById(R.id.email_avatar);
		email_content = (WebView) findViewById(R.id.email_content);
		email_progress = (ProgressBar) findViewById(R.id.email_progress);
		
		email_subject.setText(entity.getSubject());
		email_from.setText(entity.getFrom());
		email_to.setText(entity.getTo());
		email_date.setText(entity.getDate());
		
		ColorGenerator generator = ColorGenerator.MATERIAL;
		int color = generator.getColor(entity.getFrom());
		TextDrawable.IBuilder builder = TextDrawable.builder().round();
		String icon = (entity.getFrom().matches("^[a-zA-Z].*$"))?entity.getFrom().substring(0, 1):entity.getFrom().substring(1, 2);
		icon = (icon.matches("^[a-zA-Z].*$")?icon:entity.getFrom().substring(2,3));
		TextDrawable td = builder.build(icon.toUpperCase(), color);
		email_avatar.setImageDrawable(td);
		
		datasource.open();
		InboxEntity item = new InboxEntity();
		item = datasource.getById(entity.getId());
		if (!item.isDownload()) {
			new GetEmailAsynTask(this).execute(entity.getUUID());
		}
		
		email_content.loadData(entity.getContent(), "text/html", null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.read, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		datasource.close();
	}
	
	private class GetEmailAsynTask extends AsyncTask<Long, String, Boolean>
	{
		private Activity activity;
		private InboxEntity message;
		private String Log;
		
		public GetEmailAsynTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			// TODO Auto-generated method stub
			MailReader mailReader = new MailReader();
			Message msg = mailReader.GetEmailByUUID(params[0]);
			if (msg == null) {
				Log = MailReader.LOG;
				return false;
			}
			
			message = datasource.getByUUID(params[0]);
			message.setContent(mailReader.GetEmailContent(msg));
			message.setDownload(true);
			datasource.update(message);
			return true;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			email_progress.setVisibility(View.VISIBLE);
			email_content.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result)
			{
				email_content.loadData(message.getContent(), "text/html", null);
			}else{
				Toast.makeText(activity, Log, Toast.LENGTH_SHORT).show();
			}
			email_progress.setVisibility(View.INVISIBLE);
			email_content.setVisibility(View.VISIBLE);
			
		}
	}
}
