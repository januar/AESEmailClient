package com.aesemailclient.email;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.aesemailclient.InboxFragment;
import com.aesemailclient.InboxItem;
import com.aesemailclient.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

public class MailReaderAsyncTask extends AsyncTask<String, String, Boolean> {
	
	Activity activity;
	SwipeRefreshLayout swipeLayout;
	InboxFragment fragment;
	List<InboxItem> dataList;
	
	public MailReaderAsyncTask(Activity _activity, SwipeRefreshLayout swipeLayout, Fragment fragment) {
		// TODO Auto-generated constructor stub
		this.activity = _activity;
		this.swipeLayout = swipeLayout;
		this.fragment = (InboxFragment)fragment;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		MailReader mailReader = new MailReader(activity);
		Message[] msg = mailReader.getMail();
		if(msg == null)
		{
			return false;
		}
		try{
			dataList = new ArrayList<InboxItem>();
			for (int i = 0; i < msg.length; i++) {
				Address[] addr = msg[i].getFrom();
				
				dataList.add(new InboxItem(msg[i].getSubject(), addr[0].toString(), R.drawable.ic_action_about));
			}
			return true;
		}catch(MessagingException me){
			me.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		swipeLayout.setRefreshing(false);
		if(result)
		{
			fragment.adapter.clear();
			fragment.adapter.addAll(dataList);
			fragment.adapter.notifyDataSetChanged();
		}else{
			Toast.makeText(this.activity, "Failed connected to email server", Toast.LENGTH_SHORT).show();
		}
	}
}
