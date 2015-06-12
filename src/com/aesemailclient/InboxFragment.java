package com.aesemailclient;

import java.util.ArrayList;
import java.util.List;

import com.aesemailclient.email.MailReaderAsyncTask;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class InboxFragment extends Fragment {
	private View view;
	private SwipeRefreshLayout swipeLayout;
	private ListView mInboxList;
	
	InboxAdapter adapter;
	List<InboxItem> dataList;

	public InboxFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_inbox, container, false);
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
			        @Override public void run() {
			            swipeLayout.setRefreshing(false);
			        }
			    }, 5000);
			}
		});
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
	    
	    mInboxList = (ListView)findViewById(R.id.inbox_list);
	    dataList = new ArrayList<InboxItem>();
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    dataList.add(new InboxItem("Test 1", "detail 1", R.drawable.ic_action_about));
	    adapter = new InboxAdapter(getActivity(), R.layout.inbox_drawer, dataList);
	    mInboxList.setAdapter(adapter);
	    
	    new MailReaderAsyncTask(getActivity()).execute();
	    
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	protected View findViewById(int id) {
		return view.findViewById(id);
	}

}
