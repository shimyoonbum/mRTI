package com.pulmuone.mrtina.popupOrg;

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

import static com.pulmuone.mrtina.R.id.txt_lot_date;
import static com.pulmuone.mrtina.R.id.txt_lot_no;

public class PopOrgAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopOrgItem> arr;

	public PopOrgAdapter(Activity act, ArrayList<PopOrgItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_org, parent, false);
		}

		TextView ORG_CODE = (TextView)convertView.findViewById(R.id.txt_org_code);
		TextView ORG_NAME = (TextView)convertView.findViewById(R.id.txt_org_name);

		ORG_CODE.setText(arr.get(position).ORG_CODE);
		ORG_CODE.setGravity(Gravity.CENTER);
		ORG_NAME.setText(arr.get(position).ORG_NAME);
		ORG_NAME.setGravity(Gravity.CENTER);

		return convertView;
	}
}


