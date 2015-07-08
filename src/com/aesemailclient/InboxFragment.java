package com.aesemailclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.aesemailclient.db.InboxDataSource;
import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.email.MailReaderAsyncTask;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class InboxFragment extends Fragment {
	public static final String DATE_FORMAT = "dd-MM-yyyy";
	
	private View view;
	private SwipeRefreshLayout swipeLayout;
	private ListView mInboxList;
	private Fragment fragment;
	
	public ProgressBar progressBar;
	public InboxAdapter adapter;
	public List<InboxEntity> dataList;
	public Boolean loading = false;

	public InboxFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_inbox, container, false);
		fragment = this;
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				String date = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().getTime());
				new MailReaderAsyncTask(getActivity(), swipeLayout, fragment).execute(date,"after");
			}
		});
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
	    
	    mInboxList = (ListView)findViewById(R.id.inbox_list);
	    progressBar = new ProgressBar(getActivity());
	    progressBar.setVisibility(View.GONE);
		mInboxList.addFooterView(progressBar);
	    dataList = new ArrayList<InboxEntity>();
	    adapter = new InboxAdapter(getActivity(), R.layout.inbox_drawer, dataList);
	    mInboxList.setAdapter(adapter);
		
	    
	    
	    mInboxList.setOnScrollListener(new AbsListView.OnScrollListener() {
			
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
				
//				if((totalItemCount - visibleItemCount) == firstVisibleItem && totalItemCount > 1)
//				{
//					progressBar.setVisibility(View.VISIBLE);
//					Log.i(getTag(), "Last item");
//					if(!loading)
//					{
//						Date date = new Date();
//						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//						String time_text = CacheToFile.Read(getActivity(), CacheToFile.DATE_TEMP);
//						if (time_text != "") {
//							try {
//								Calendar cal = Calendar.getInstance();
//								cal.setTime(sdf.parse(time_text));
//								cal.add(Calendar.DATE, -1);
//								date = cal.getTime();
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//						
//						new MailReaderAsyncTask(getActivity(), swipeLayout, fragment).execute(sdf.format(date), "before");
//					}
//				}
			}
		});
	    
	    new InboxAsyncTask().execute();
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
				datasource.close();
				if (dataList == null) {
					return false;
				}
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
