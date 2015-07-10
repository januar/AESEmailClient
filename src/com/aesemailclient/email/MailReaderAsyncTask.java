package com.aesemailclient.email;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.aesemailclient.CacheToFile;
import com.aesemailclient.InboxFragment;
import com.aesemailclient.InboxItem;
import com.aesemailclient.R;
import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MailReaderAsyncTask extends AsyncTask<String, String, Boolean> {
	
	private Date from;
	private Date to;
	private String message;
	private InboxDataSource datasource;
	Activity activity;
	SwipeRefreshLayout swipeLayout;
	InboxFragment fragment;
	String type;
	
	public MailReaderAsyncTask(Activity _activity, SwipeRefreshLayout swipeLayout, Fragment fragment) {
		// TODO Auto-generated constructor stub
		this.activity = _activity;
		this.swipeLayout = swipeLayout;
		this.fragment = (InboxFragment)fragment;
		datasource = new InboxDataSource(this.activity);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		from = new Date();
		to = new Date();
		Date tempDate = new Date();
		Calendar cal = Calendar.getInstance();
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
		tempDate = from;
		
		if(type == "after"){
			String date_last = CacheToFile.Read(activity, CacheToFile.DATE_LAST);
			if (date_last != "") {
				try {
					to = sdf.parse(date_last);
					if(tempDate.getYear() == to.getYear() && tempDate.getMonth() == to.getMonth() && tempDate.getDay() == to.getDay())
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
		}
		
		MailReader mailReader = new MailReader(activity);
		Message[] msg = mailReader.getMail(tempDate, to);
		if(msg == null)
		{
			int maxloop = 0;
			if (type == "before") {
//				cal.setTime(from);
				while(msg == null && maxloop < 3)
				{
					cal.add(Calendar.DATE, -1);
					to = cal.getTime();
					msg = mailReader.getMail(from, to);
					maxloop++;
				}
			} 
			
			if(msg == null){
				this.message = "No data found";
				return false;
			}
		}
		try{
			fragment.dataList = new ArrayList<InboxEntity>();
			datasource.open();
			for (int i = 0; i < msg.length; i++) {
				Address[] addr = msg[i].getFrom();
				Address[] addrTo = msg[i].getAllRecipients();
				
				InboxEntity item = new InboxEntity(0, msg[i].getSubject(), addr[0].toString(), 
						addrTo[0].toString(), msg[i].getSentDate().toString(), mailReader.GetEmailContent(msg[i]), false);
				
				String where = "subject = ? AND from_add = ? AND date = ?";
				String[] whereArgs = new String[]{item.getSubject(), item.getFrom(), item.getDate()};
				InboxEntity temp = datasource.getWhere(where, whereArgs);
				if(temp == null)
				{
					datasource.save(item);
				}
			}
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
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		SimpleDateFormat sdf = new SimpleDateFormat(InboxFragment.DATE_FORMAT);
		if(result)
		{
			fragment.adapter.clear();
			fragment.adapter.addAll(fragment.dataList);
			fragment.adapter.notifyDataSetChanged();
		}else{
			Toast.makeText(this.activity, message, Toast.LENGTH_SHORT).show();
		}
		if(type == "before")
		{
			fragment.mInboxList.stopLoadMore();
			CacheToFile.Write(this.activity, CacheToFile.DATE_OLD, sdf.format(to));
		}else{
			swipeLayout.setRefreshing(false);
			CacheToFile.Write(this.activity, CacheToFile.DATE_LAST, sdf.format(from));
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
