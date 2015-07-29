package com.aesemailclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;

import com.aesemailclient.DecryptDialog.DecryptDialogListener;
import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.db.UserDataSource;
import com.aesemailclient.db.UserEntity;
import com.aesemailclient.email.MailReader;
import com.aesemailclient.textdrawable.TextDrawable;
import com.aesemailclient.textdrawable.util.ColorGenerator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReadActivity extends AppCompatActivity implements DecryptDialogListener {
	
	private InboxDataSource datasource;
	private UserDataSource userDatasource;
	private String secretKey;
	
	TextView email_subject;
	TextView email_from;
	TextView email_to;
	TextView email_date;
	ImageView email_avatar;
	WebView email_content;
	ProgressBar email_progress;
	Matcher matcher;
	
	public static final String REGEX_PATTERN = "(<encrypt>)([a-zA-Z0-9\\W]*)(</encrypt>)";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datasource = new InboxDataSource(this);
		userDatasource = new UserDataSource(this);
		secretKey = "";
		
		setContentView(R.layout.activity_read);
		setupActionBar();
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
			new GetEmailAsyncTask(this).execute(entity.getUUID());
		}
		checkContent(item.getContent());
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		ActionBar bar = getSupportActionBar();
		int change = bar.getDisplayOptions() ^ ActionBar.DISPLAY_HOME_AS_UP;
        bar.setDisplayOptions(change, ActionBar.DISPLAY_HOME_AS_UP);
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
//		datasource.close();
	}
	
	private Boolean checkEncryptText(String content){
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		matcher = pattern.matcher(content);
		return matcher.find();
	}
	
	private void checkContent(String content){
		if (checkEncryptText(content)) {
			Bundle bundle = new Bundle();
			bundle.putString("ciphertext", matcher.group(2));
			bundle.putString("content", content);
			DecryptDialog dialog = new DecryptDialog();
			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), "TAG");
		}else{
			email_content.loadData(content, "text/html", null);
		}
	}
	
	private class GetEmailAsyncTask extends AsyncTask<Long, String, Boolean>
	{
		private Activity activity;
		private InboxEntity message;
		private String Log;
		
		public GetEmailAsyncTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected Boolean doInBackground(Long... params) {
			// TODO Auto-generated method stub
			userDatasource.open();
			UserEntity user = userDatasource.getUser();
			userDatasource.close();
			MailReader mailReader = new MailReader(user);
			Message msg = mailReader.GetEmailByUUID(params[0]);
			if (msg == null) {
				Log = MailReader.LOG;
				return false;
			}
			
			message = datasource.getByUUID(params[0]);
			message.setContent(mailReader.GetEmailContent(msg));
			message.setDownload(true);
			datasource.update(message);
			datasource.close();
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
//				email_content.loadData(message.getContent(), "text/html", null);
				checkContent(message.getContent());
			}else{
				Toast.makeText(activity, Log, Toast.LENGTH_SHORT).show();
			}
			email_progress.setVisibility(View.INVISIBLE);
			email_content.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void loadContent(String ciphertext) {
		// TODO Auto-generated method stub
		email_content.loadData(ciphertext, "text/html", null);
	}
}
