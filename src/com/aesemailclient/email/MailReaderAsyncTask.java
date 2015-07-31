package com.aesemailclient.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.aesemailclient.CacheToFile;
import com.aesemailclient.InboxFragment;
import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.db.UserDataSource;
import com.aesemailclient.db.UserEntity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

public class MailReaderAsyncTask extends AsyncTask<Integer, String, Boolean> {
	
	private Date from;
	private Date to;
	private String message;
	private InboxDataSource datasource;
	private UserDataSource userDatasource;
	Activity activity;
	SwipeRefreshLayout swipeLayout;
	InboxFragment fragment;
	int type;
	
	public MailReaderAsyncTask(Activity _activity, SwipeRefreshLayout swipeLayout, Fragment fragment) {
		// TODO Auto-generated constructor stub
		this.activity = _activity;
		this.swipeLayout = swipeLayout;
		this.fragment = (InboxFragment)fragment;
		datasource = new InboxDataSource(this.activity);
		userDatasource = new UserDataSource(this.activity);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected Boolean doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		/*from = new Date();
		to = new Date();
		Date tempDate = new Date();
		Calendar cal = Calendar.getInstance();
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(InboxFragment.DATE_FORMAT);
		type = params[1];
		
		try {
			from = sdf.parse(params[0]);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cal.setTime(from);
		cal.add(Calendar.DATE, -1);
		to = cal.getTime();
		tempDate = from;*/
		
		/*if(type.equals("after")){
			String date_last = CacheToFile.Read(activity, CacheToFile.DATE_LAST);
			if (date_last != "") {
				try {
					to = sdf.parse(date_last);
					calFrom.setTime(tempDate);
					calTo.setTime(to);
					if(calFrom.get(Calendar.YEAR) == calTo.get(Calendar.YEAR) && 
							calFrom.get(Calendar.MONTH) == calTo.get(Calendar.MONTH) && 
							calFrom.get(Calendar.DATE) == calTo.get(Calendar.DATE))
					{
						cal.setTime(tempDate);
						cal.add(Calendar.DATE, 1);
						tempDate = cal.getTime();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
		
		long UUID = 0;
		type = params[0];
		datasource.open();
		if (type == MailReader.NEW) {
			InboxEntity item = datasource.getWhere(null, null, InboxDataSource.COLUMN_UUID + " DESC");
			UUID = item.getUUID();
		}else if (type == MailReader.OLD) {
			InboxEntity item = datasource.getWhere(null, null, InboxDataSource.COLUMN_UUID + " ASC");
			UUID = item.getUUID();
		}
		
		userDatasource.open();
		UserEntity user = userDatasource.getUser();
		userDatasource.close();
		MailReader mailReader = new MailReader(user);
		Message[] msg = mailReader.getMail(UUID, type);
		this.message = MailReader.LOG;
		if(msg == null)
		{
			return false;
		}
		
		try{
			fragment.dataList = new ArrayList<InboxEntity>();
			SimpleDateFormat sdf = new SimpleDateFormat(InboxDataSource.DATE_FORMAT);
			for (int i = 0; i < msg.length; i++) {
				Address[] addr = msg[i].getFrom();
				Address[] addrTo = msg[i].getAllRecipients();
				Long uuid = mailReader.inbox.getUID(msg[i]);
				Date date = new Date(msg[i].getSentDate().toString());
				
				InboxEntity item = new InboxEntity(0, msg[i].getSubject(), addr[0].toString(), 
						addrTo[0].toString(), sdf.format(date), "", uuid, false);
				
				String where = "subject = ? AND from_add = ? AND date = ?";
				String[] whereArgs = new String[]{item.getSubject(), item.getFrom(), item.getDate()};
				InboxEntity temp = datasource.getWhere(where, whereArgs);
				if(temp == null)
				{
					datasource.save(item);
				}
			}
			fragment.dataList.clear();
			fragment.dataList = datasource.getAll();
			datasource.close();
			return true;
		}catch(MessagingException me){
			me.printStackTrace();
			this.message = me.getMessage();
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			this.message = e.getMessage();
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result)
		{
			fragment.adapter.clear();
			fragment.adapter.addAll(fragment.dataList);
			fragment.adapter.notifyDataSetChanged();
		}else{
			Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show();
		}
		if(type == MailReader.OLD)
		{
			fragment.mInboxList.stopLoadMore();
		}else{
			swipeLayout.setRefreshing(false);
		}
		fragment.loading = false;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		fragment.loading = true;
	}
}
