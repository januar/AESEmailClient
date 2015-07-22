package com.aesemailclient;

import java.util.List;
import com.aesemailclient.db.SentEntity;
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

public class SentAdapter extends ArrayAdapter<SentEntity> {
	
	Context context;
    List<SentEntity> drawerItemList;
    int layoutResID;

	public SentAdapter(Context context, int layoutResourceID,
			List<SentEntity> listItems) {
		super(context, layoutResourceID, listItems);
		// TODO Auto-generated constructor stub
		this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SentItemHolder drawerHolder;
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			drawerHolder = new SentItemHolder();

			view = inflater.inflate(layoutResID, parent, false);
			drawerHolder.TxtSubject = (TextView) view
					.findViewById(R.id.txt_sent_subject);
			drawerHolder.TxtTo = (TextView) view.findViewById(R.id.txt_sent_to);
			drawerHolder.TxtDate = (TextView)view.findViewById(R.id.txt_sent_date);
			drawerHolder.icon = (ImageView) view.findViewById(R.id.sent_email_ava_list);

			view.setTag(drawerHolder);
		} else {
			drawerHolder = (SentItemHolder) view.getTag();
		}

		SentEntity dItem = (SentEntity) this.drawerItemList.get(position);
		
		drawerHolder.TxtSubject.setText(dItem.getSubject());
		drawerHolder.TxtTo.setText(dItem.getTo());
		drawerHolder.TxtDate.setText(dItem.getDate());
		
		ColorGenerator generator = ColorGenerator.MATERIAL;
		int color = generator.getColor(dItem.getTo());
		TextDrawable.IBuilder builder = TextDrawable.builder().round();
		String icon = (dItem.getTo().matches("^[a-zA-Z].*$"))?dItem.getTo().substring(0, 1):dItem.getTo().substring(1, 2);
		icon = (icon.matches("^[a-zA-Z].*$")?icon:dItem.getTo().substring(2,3));
		TextDrawable td = builder.build(icon.toUpperCase(), color);
		drawerHolder.icon.setImageDrawable(td);
		return view;
	}

	private static class SentItemHolder {
		TextView TxtSubject;
		TextView TxtTo;
		TextView TxtDate;
		ImageView icon;
	}

}
