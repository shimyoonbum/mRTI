package com.pulmuone.mrtina.popupTranItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_item_code;
import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_org_code;

public class PopTranItemAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopTranItemItem> arr;

	public PopTranItemAdapter(Activity act, ArrayList<PopTranItemItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_item, parent, false);
		}

		TextView ITEM_NO = (TextView)convertView.findViewById(txt_item_code);
		TextView ITEM_NAME = (TextView)convertView.findViewById(txt_item_name);
		TextView ORG_CODE = (TextView)convertView.findViewById(txt_org_code);

		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		ITEM_NAME.setText(arr.get(position).ITEM_DESC);
		ORG_CODE.setText(arr.get(position).ORGANIZATION_CODE);

		return convertView;
	}

}


