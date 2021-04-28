package com.pulmuone.mrtina.changeSubInv;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_code;
import static com.pulmuone.mrtina.R.id.txt_name;

public class ChangeSubInvAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<ChangeSubInvItem> arr;

	public ChangeSubInvAdapter(Activity act, ArrayList<ChangeSubInvItem> arr_item) {
		this.m_activity = act;
		arr = arr_item;
		mInflater = (LayoutInflater)m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return arr.size();
	}
	@Override
	public Object getItem(int position) {
		return arr.get(position);
	}
	public long getItemId(int position){
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_change_subinv, parent, false);
		}

		TextView SUBINVENTORY_CODE = (TextView)convertView.findViewById(txt_code);
		TextView SUBINVENTORY_NAME = (TextView)convertView.findViewById(txt_name);

		SUBINVENTORY_CODE.setText(arr.get(position).SUBINVENTORY_CODE);
		SUBINVENTORY_CODE.setGravity(Gravity.CENTER);
		SUBINVENTORY_NAME.setText(arr.get(position).SUBINVENTORY_NAME);
		SUBINVENTORY_NAME.setGravity(Gravity.CENTER);

		return convertView;
	}
}



