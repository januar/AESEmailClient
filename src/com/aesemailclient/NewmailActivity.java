package com.aesemailclient;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aesemailclient.EncryptDialog.EncryptDialogListener;
import com.aesemailclient.crypto.CryptoUtils;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.db.SentDataSource;
import com.aesemailclient.db.SentEntity;
import com.aesemailclient.db.UserDataSource;
import com.aesemailclient.db.UserEntity;
import com.aesemailclient.email.MailSender;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.style.BulletSpan;

public class NewmailActivity extends AppCompatActivity implements
		EncryptDialogListener {

	private SentDataSource datasource;
	private UserDataSource userDatasource;
	private UserEntity user;

	private EditText txt_from;
	private EditText txt_to;
	private EditText txt_subject;
	private EditText txt_content;
	private int public_key;

	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newmail);
		// Show the Up button in the action bar.
		setupActionBar();
		datasource = new SentDataSource(this);
		datasource.open();
		userDatasource = new UserDataSource(this);
		userDatasource.open();
		user = userDatasource.getUser();
		userDatasource.close();

		txt_from = (EditText) findViewById(R.id.txt_from);
		txt_from.setText(user.getEmail());
		txt_from.setKeyListener(null);

		txt_to = (EditText) findViewById(R.id.txt_to);
		txt_subject = (EditText) findViewById(R.id.txt_subject);
		txt_content = (EditText) findViewById(R.id.txt_content);

		progress = new ProgressDialog(this);

		try {
			Intent intent = this.getIntent();
			Bundle bundle = intent.getExtras();
			InboxEntity entity = (InboxEntity) bundle
					.getSerializable(InboxFragment.INBOX_ENTITY);
			
			String action = bundle.getString("action");
			if (entity != null) {
				if (action.equals("reply")) {
					txt_to.setText(entity.getFrom());
					txt_subject.setText("Re: " + entity.getSubject());
					if (entity.getSubject().startsWith("Re:")) {
						txt_subject.setText(entity.getSubject());
					}
				} else {
					txt_subject.setText("Fwd: " + entity.getSubject());
					if (entity.getSubject().startsWith("Fwd:")) {
						txt_subject.setText(entity.getSubject());
					}
					txt_content.setText(entity.getContent());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
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
			if (txt_to.getText().toString().trim() == "") {
				Toast.makeText(this, "Please insert email address of reciver!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else if (txt_subject.getText().toString().trim() == "") {
				Toast.makeText(this, "Please insert email subject!",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			new SendAsyncTask(this).execute();
			return true;

		case R.id.action_encrypt:
			//new EncryptDialog().show(getFragmentManager(), "TAG");
			if (this.public_key <= 0) {
				new RabinDialog().show(getFragmentManager(), "TAG");
			}else{
				new EncryptDialog(this.public_key).show(getFragmentManager(), "TAG");
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void showEncryptDialog(int public_key)
	{
		this.public_key = public_key;
		new EncryptDialog(this.public_key).show(getFragmentManager(), "TAG");
	}

	public void addEncrytedText(String ciphertext) {
		String text = txt_content.getText().toString();

		text += ciphertext;
		txt_content.setText(text);
	}

	private class SendAsyncTask extends AsyncTask<String, String, Boolean> {
		private Activity activity;

		public SendAsyncTask(Activity activity) {
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

			MailSender sender = new MailSender(user);
			SimpleDateFormat sdf = new SimpleDateFormat(
					InboxFragment.DATE_FORMAT);
			String date = sdf.format(new Date());
			if (sender.send(from, to, subject, content)) {
				try {
					SentEntity sent = new SentEntity(subject, from, to,
							content, date);
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
				Toast.makeText(activity, MailSender.LOG, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
	
	private class RabinDialog extends DialogFragment{
		//private View view;
		private EditText txt_publickey;
		
		public RabinDialog(){}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			//view = inflater.inflate(R.layout.encrypt_layout, null);
			txt_publickey = new EditText(getActivity());
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			txt_publickey.setLayoutParams(lp);
			builder.setView(txt_publickey);
			builder.setTitle("Rabin Cryptosystem");
			builder.setMessage("Insert receiver public key:");
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
						int public_key = 0;
						try {
							public_key = Integer.parseInt(txt_publickey.getText().toString());
							showEncryptDialog(public_key);
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
			}
		}
	}
}
