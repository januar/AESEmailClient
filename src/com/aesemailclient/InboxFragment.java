package com.aesemailclient;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message;
import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.db.UserDataSource;
import com.aesemailclient.db.UserEntity;
import com.aesemailclient.email.MailReader;
import com.aesemailclient.email.MailReaderAsyncTask;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

public class InboxFragment extends Fragment {
	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	public static final String INBOX_ENTITY = "inbox_entity";
	
	private View view;
	private SwipeRefreshLayout swipeLayout;
	private Fragment fragment;
	
	public XListView mInboxList;
	public ProgressBar progressBar;
	public InboxAdapter adapter;
	public List<InboxEntity> dataList;
	public Boolean loading = false;

	public InboxFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_inbox, container, false);
		fragment = this;
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if (!loading) {
					new MailReaderAsyncTask(getActivity(), swipeLayout, fragment).execute(MailReader.NEW);
				}
			}
		});
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
	    
	    mInboxList = (XListView)findViewById(R.id.inbox_list);
	    mInboxList.setPullRefreshEnable(false);
	    mInboxList.setPullLoadEnable(true);
	    /*progressBar = new ProgressBar(getActivity());
	    progressBar.setVisibility(View.GONE);
		mInboxList.addFooterView(progressBar);*/
	    
	    dataList = new ArrayList<InboxEntity>();
    	adapter = new InboxAdapter(getActivity(), R.layout.inbox_drawer, dataList);
	    mInboxList.setAdapter(adapter);
	    
	    mInboxList.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				
			}
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(!loading)
				{
					new MailReaderAsyncTask(getActivity(), swipeLayout, fragment).execute(MailReader.OLD);
				}
			}
		});
		
	    mInboxList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				InboxEntity entity = adapter.getItem(position-1);
				Intent intent = new Intent(getActivity(), ReadActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(INBOX_ENTITY, (Serializable) entity);
				bundle.putString("activity", "inbox");
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	    
	    /*mInboxList.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				Log.i(getTag(), "scrollState : " + scrollState);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				Log.i(getTag(), "firstVisibleItem = " + firstVisibleItem + 
						" visibleItemCount = " + visibleItemCount +
						" totalItemCount = " + totalItemCount);
				
				if((totalItemCount - visibleItemCount) == firstVisibleItem && totalItemCount > 1)
				{
					progressBar.setVisibility(View.VISIBLE);
					Log.i(getTag(), "Last item");
					if(!loading)
					{
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
						String time_text = CacheToFile.Read(getActivity(), CacheToFile.DATE_TEMP);
						if (time_text != "") {
							try {
								Calendar cal = Calendar.getInstance();
								cal.setTime(sdf.parse(time_text));
								cal.add(Calendar.DATE, -1);
								date = cal.getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						new MailReaderAsyncTask(getActivity(), swipeLayout, fragment).execute(sdf.format(date), "before");
					}
				}
			}
		});*/
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable("inbox_list", (Serializable) dataList);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Bundle data = getArguments();
		if (data.getSerializable("inbox_list") != null) {
			dataList = (List<InboxEntity>) data.getSerializable("inbox_list");
			adapter.addAll(dataList);
			adapter.notifyDataSetChanged();
		}else{
			new InboxAsyncTask().execute();
		}
	}
	
	protected View findViewById(int id) {
		return view.findViewById(id);
	}
	
	private class InboxAsyncTask extends AsyncTask<String, String, Boolean>{
		private ProgressBar progressBar;
		private SwipeRefreshLayout swipeLayout;
		private InboxDataSource datasource;
		private List<InboxEntity> dataList;
		
		public InboxAsyncTask()
		{
			progressBar = (ProgressBar)findViewById(R.id.inbox_progressbar);
			swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
			datasource = new InboxDataSource(getActivity());
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				datasource.open();
				dataList = datasource.getAll();
				if (dataList.size() == 0) {
					UserDataSource userDatasource = new UserDataSource(getActivity());
					userDatasource.open();
					UserEntity user = userDatasource.getUser();
					userDatasource.close();
					MailReader mailReader = new MailReader(user);
					Message[] msg = mailReader.getMail();
					if (msg == null) {
						return false;
					}else{
						SimpleDateFormat sdf = new SimpleDateFormat(InboxDataSource.DATE_FORMAT);
						for (int i = 0; i < msg.length; i++) {
							Address[] addr = msg[i].getFrom();
							Address[] addrTo = msg[i].getAllRecipients();
							Long uuid = mailReader.inbox.getUID(msg[i]);
							Date date = new Date(msg[i].getSentDate().toString());
							
							InboxEntity item = new InboxEntity(0, msg[i].getSubject(), addr[0].toString(), 
									addrTo[0].toString(), sdf.format(date), "", uuid, false);
							
							datasource.save(item);
						}
						dataList = datasource.getAll();
					}
				}
				datasource.close();
				return true;
			} catch (Exception e) {
				// TODO: handle exception
				Log.e(getTag(), e.getMessage());
			}
			return false;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			swipeLayout.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {
				adapter.addAll(dataList);
				adapter.notifyDataSetChanged();
			}
			swipeLayout.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}
		
	}

}
