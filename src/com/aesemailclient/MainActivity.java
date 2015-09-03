package com.aesemailclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aesemailclient.db.UserDataSource;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

	private Bundle args;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	CustomDrawerAdapter adapter;
	List<DrawerItem> dataList;
	
	Fragment mCurrent;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dataList = new ArrayList<DrawerItem>();
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Add Drawer Item to dataList
		dataList.add(new DrawerItem("Inbox", R.drawable.ic_action_email));
		dataList.add(new DrawerItem("Sent Item", R.drawable.ic_action_send_now));
		dataList.add(new DrawerItem("About", R.drawable.ic_action_about));
		dataList.add(new DrawerItem("Key", R.drawable.ic_action_settings));
		dataList.add(new DrawerItem("Sign out", R.drawable.ic_action_help));

		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
				dataList);

		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		/*getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);*/
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		/*mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};*/
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		

		if (savedInstanceState == null) {
			args = new Bundle();
			SelectItem(0);
		}else{
			args = savedInstanceState;
		}
		
		/*if (CacheToFile.Read(this, CacheToFile.DATE_OLD) == "") {
			SimpleDateFormat sdf = new SimpleDateFormat(InboxFragment.DATE_FORMAT);
			CacheToFile.Write(this, CacheToFile.DATE_OLD, sdf.format(new Date()));
		}*/
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void SelectItem(int possition) {
		Fragment fragment = null;
		Boolean isFinished = false;
		if (mCurrent != null) {
			mCurrent.onSaveInstanceState(args);
		}
		
		switch (possition) {
		case 0: // inbox
			fragment = new InboxFragment();
			break;
		case 1: // sent
			fragment = new SentFragment();
			break;
		case 3: // key
			fragment = new KeyFragment();
			break;
		case 4: // sign out
			UserDataSource userDatasource= new UserDataSource(this);
			userDatasource.open();
			userDatasource.delete();
			userDatasource.close();
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
			isFinished = true;
			break;
		default:
			fragment = new FragmentOne();
			args.putString(FragmentOne.ITEM_NAME, dataList.get(possition)
					.getItemName());
			args.putInt(FragmentOne.IMAGE_RESOURCE_ID, dataList.get(possition)
					.getImgResID());
			break;
		}

		if (!isFinished) {
			fragment.setArguments(args);
			FragmentManager frgManager = getFragmentManager();
			frgManager.beginTransaction().replace(R.id.content_frame, fragment)
					.commit();
			mCurrent = fragment;
			mDrawerList.setItemChecked(possition, true);
			setTitle(dataList.get(possition).getItemName());
			mDrawerLayout.closeDrawer(mDrawerList);
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
//		getActionBar().setTitle(mTitle);
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		switch (item.getItemId()) {
		case R.id.action_new_email:
			Intent intent = new Intent(this, NewmailActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mDrawerLayout.isDrawerOpen(Gravity.START|Gravity.LEFT)){
	        mDrawerLayout.closeDrawers();
	        return;
	    }
		super.onBackPressed();
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			view.setSelected(true);
			SelectItem(position);
		}
	}

}
