package com.aesemailclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.db.SentDataSource;
import com.aesemailclient.db.SentEntity;

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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SentFragment extends Fragment {
	
	private View view;
	private SwipeRefreshLayout swipeLayout;
	
	public ListView mSentList;
	public ProgressBar progressBar;
	public SentAdapter adapter;
	public List<SentEntity> dataList;

	public SentFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_sent, container, false);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_sent_container);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
			}
		});
		
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
	    
	    mSentList = (ListView) findViewById(R.id.sent_list);
	    dataList = new ArrayList<SentEntity>();
	    adapter = new SentAdapter(getActivity(), R.layout.sent_drawer, dataList);
	    mSentList.setAdapter(adapter);
	    
	    mSentList.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
	    		Bundle bundle = new Bundle();
	    		bundle.putString("activity", "sent");
	    		
	    		SentEntity sent = adapter.getItem(position);
	    		InboxEntity inbox = new InboxEntity();
	    		inbox.setId(sent.getId());
	    		inbox.setSubject(sent.getSubject());
	    		inbox.setFrom(sent.getFrom());
	    		inbox.setTo(sent.getTo());
	    		inbox.setDate(sent.getDate());
	    		inbox.setContent(sent.getContent());
	    		bundle.putSerializable(InboxFragment.INBOX_ENTITY, inbox);
	    		
	    		Intent intent = new Intent(getActivity(), ReadActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	    
	    progressBar = (ProgressBar) findViewById(R.id.sent_progressbar);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		new SentAsyncTask().execute();
	}
	
	protected View findViewById(int id) {
		return view.findViewById(id);
	}
	
	private class SentAsyncTask extends AsyncTask<String, String, Boolean>{
		private SentDataSource datasource;
		private List<SentEntity> dataList;
		
		public SentAsyncTask()
		{
			datasource = new SentDataSource(getActivity());
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
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {
				adapter.addAll(dataList);
				adapter.notifyDataSetChanged();
			}
			progressBar.setVisibility(View.INVISIBLE);
		}
		
	}
}
