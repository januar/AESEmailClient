package com.aesemailclient;

import java.util.List;

import com.aesemailclient.db.InboxEntity;
import com.aesemailclient.textdrawable.TextDrawable;
import com.aesemailclient.textdrawable.util.ColorGenerator;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InboxAdapter extends ArrayAdapter<InboxEntity> {
	
	Context context;
    List<InboxEntity> drawerItemList;
    int layoutResID;
    
	public InboxAdapter(Context context, int layoutResourceID,
			List<InboxEntity> listItems) {
		super(context, layoutResourceID, listItems);
		// TODO Auto-generated constructor stub
		this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		InboxItemHolder drawerHolder;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			drawerHolder = new InboxItemHolder();

			view = inflater.inflate(layoutResID, parent, false);
			drawerHolder.TxtSubject = (TextView) view
					.findViewById(R.id.txt_subject);
			drawerHolder.TxtDetail = (TextView) view.findViewById(R.id.txt_detail);
			
			drawerHolder.icon = (ImageView) view.findViewById(R.id.email_ava);

			view.setTag(drawerHolder);
		} else {
			drawerHolder = (InboxItemHolder) view.getTag();
		}

		InboxEntity dItem = (InboxEntity) this.drawerItemList.get(position);
		
		drawerHolder.TxtSubject.setText(dItem.getSubject());
		drawerHolder.TxtDetail.setText(dItem.getFrom());
		
		ColorGenerator generator = ColorGenerator.MATERIAL;
		int color = generator.getColor(dItem.getFrom());
		TextDrawable.IBuilder builder = TextDrawable.builder().round();
		TextDrawable td = builder.build(dItem.getFrom().substring(0, 1).toUpperCase(), color);
		drawerHolder.icon.setImageDrawable(td);
		return view;
	}

	private static class InboxItemHolder {
		TextView TxtSubject;
		TextView TxtDetail;
		ImageView icon;
	}
}
