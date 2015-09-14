package com.aesemailclient;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;

import com.aesemailclient.DecryptDialog.DecryptDialogListener;
import com.aesemailclient.crypto.CryptoUtils;
import com.aesemailclient.crypto.InvalidKeyException;
import com.aesemailclient.crypto.Rabin;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ReadActivity extends AppCompatActivity implements DecryptDialogListener {
	
	private InboxDataSource datasource;
	private UserDataSource userDatasource;
	private InboxEntity entity;
	private int index;
	private int start;
	private int end;
	private String result;
	private String status;
	private int private_p;
	private int private_q;
	
	TextView email_subject;
	TextView email_from;
	TextView email_to;
	TextView email_date;
	ImageView email_avatar;
	WebView email_content;
	ProgressBar email_progress;
	
	public static final String REGEX_PATTERN = "<encrypt>([a-zA-Z0-9\\W]*?)</encrypt>";
	public static final String REGEX_TEXT = "<t>([a-zA-Z0-9\\W]*?)</t>";
	public static final String REGEX_KEY = "<q>([a-zA-Z0-9\\W]*?)</q>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datasource = new InboxDataSource(this);
		userDatasource = new UserDataSource(this);
		result = "";
		index = 0;
		
		setContentView(R.layout.activity_read);
		setupActionBar();
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		
		entity = (InboxEntity) bundle.getSerializable(InboxFragment.INBOX_ENTITY);
		status = bundle.getString("activity");
		
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
		
		if (status.equals("inbox")) {
			datasource.open();
			InboxEntity item = new InboxEntity();
			item = datasource.getById(entity.getId());
			if (!item.isDownload()) {
				new GetEmailAsyncTask(this).execute(entity.getUUID());
			}
			checkContent(item.getContent());
		}else{
			email_content.loadData(entity.getContent(), "text/html", null);
		}
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
		case R.id.action_reply:
			Intent intent = new Intent(this, NewmailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(InboxFragment.INBOX_ENTITY, (Serializable) entity);
			if (status.equals("inbox")) {
				bundle.putString("action", "reply");
			}else{
				bundle.putString("action", "fw");
			}
			
			intent.putExtras(bundle);
			startActivity(intent);
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void checkContent(String content){
		Pattern pattern = Pattern.compile(REGEX_PATTERN);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			start = matcher.start();
			end = matcher.end();
			Pattern patternText = Pattern.compile(REGEX_TEXT);
			Matcher matcherText = patternText.matcher(matcher.group(1));
			
			Pattern patternKey = Pattern.compile(REGEX_KEY);
			Matcher matcherKey = patternKey.matcher(matcher.group(1));
			
			Boolean isFindText = matcherText.find();
			Boolean isFindKey = matcherKey.find();
			if (isFindText && isFindKey) {
				try {
					if (private_p == 0 && private_q == 0) {
						new RabinDialog(matcherKey, matcherText, content).show(getFragmentManager(), "");
					}else{
						encrypt(matcherKey, matcherText, content);
					}
				} catch (Exception e) {
					// TODO: handle exception
					loadContent(matcher.group(1), content);
				}
			}else if(!isFindText && !isFindKey){
				Bundle bundle = new Bundle();
				bundle.putString("ciphertext", matcher.group(1));
				bundle.putString("content", content);
				DecryptDialog dialog = new DecryptDialog();
				dialog.setArguments(bundle);
				dialog.show(getFragmentManager().beginTransaction(), "TAG");
			}else{
				loadContent(matcher.group(1), content);
			}
		}else{
			email_content.loadData(content, "text/html", null);
		}
	}
	
	private void encrypt(Matcher matcherKey, Matcher matcherText, String content) throws InvalidKeyException {
		Rabin rabin = new Rabin();
		byte[] cipherByte = Base64.decode(matcherKey.group(1), Base64.DEFAULT);
		String aesKey = rabin.Decrypt(private_p, private_q, cipherByte);
		
		String plaintext = CryptoUtils.decrypt(aesKey, matcherText.group(1));
		if (plaintext != "") {
			loadContent(plaintext, content);
		}else{
			Toast.makeText(this, CryptoUtils.LOG,
					Toast.LENGTH_SHORT).show();
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
				checkContent(message.getContent());
			}else{
				Toast.makeText(activity, Log, Toast.LENGTH_SHORT).show();
			}
			email_progress.setVisibility(View.INVISIBLE);
			email_content.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void loadContent(String plaintext, String content) {
		// TODO Auto-generated method stub
		result = content.substring(index, index + (start - index)) + plaintext + content.substring(end);
		checkContent(result);
	}

	@Override
	public void loadContent(String content) {
		// TODO Auto-generated method stub
		email_content.loadData(content, "text/html", null);
	}
	
	private class RabinDialog extends DialogFragment{
		EditText txt_p;
		EditText txt_q;
		Matcher matcherKey; 
		Matcher matcherText; 
		String content;
		
		public RabinDialog(Matcher matcherKey, Matcher matcherText, String content){
			this.matcherKey = matcherKey;
			this.matcherText = matcherText;
			this.content = content;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			LinearLayout layout1 = new LinearLayout(getActivity());
			layout1.setOrientation(LinearLayout.VERTICAL);
			layout1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout1.setPadding(10, 0, 10, 0);
			
			LinearLayout layout2 = new LinearLayout(getActivity());
			layout2.setOrientation(LinearLayout.HORIZONTAL);
			layout2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			TextView lbl_p = new TextView(getActivity());
			lbl_p.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lbl_p.setText("p :");
			layout2.addView(lbl_p);
			
			txt_p = new EditText(getActivity());
			txt_p.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout2.addView(txt_p);
			
			LinearLayout layout3 = new LinearLayout(getActivity());
			layout3.setOrientation(LinearLayout.HORIZONTAL);
			layout3.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			TextView lbl_q = new TextView(getActivity());
			lbl_q.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lbl_q.setText("q :");
			layout3.addView(lbl_q);
			
			txt_q = new EditText(getActivity());
			txt_q.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout3.addView(txt_q);
			
			layout1.addView(layout2);
			layout1.addView(layout3);
			
			builder.setView(layout1);
			builder.setTitle("Rabin Cryptosystem");
			builder.setMessage("Insert your private key:");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			});

			return builder.create();
		}
		
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			AlertDialog d = (AlertDialog) getDialog();
			if (d != null) {
				Button positive = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
				positive.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						private_p = 0;
						private_q = 0;
						try {
							private_p = Integer.parseInt(txt_p.getText().toString());
							private_q = Integer.parseInt(txt_q.getText().toString());
							encrypt(matcherKey, matcherText, content);
							dismiss();
						} catch (NumberFormatException e) {
							// TODO: handle exception
							Toast.makeText(getActivity(), "Please insert valid number", Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});
				
				Button negative = (Button) d.getButton(Dialog.BUTTON_NEGATIVE);
				negative.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						loadContent(content);
						dismiss();
					}
				});
			}
		}
	}
}
