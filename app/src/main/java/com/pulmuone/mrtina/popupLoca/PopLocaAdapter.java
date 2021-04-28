package com.pulmuone.mrtina.popupLoca;

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
import static com.pulmuone.mrtina.R.id.txt_po_date;
import static com.pulmuone.mrtina.R.id.txt_po_no;
import static com.pulmuone.mrtina.R.id.txt_supl;

public class PopLocaAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopLocaItem> arr;

	public PopLocaAdapter(Activity act, ArrayList<PopLocaItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_loca, parent, false);
		}

		TextView LOCATOR_CODE = (TextView)convertView.findViewById(txt_code);
		TextView LOCATOR_NAME = (TextView)convertView.findViewById(txt_name);

		LOCATOR_CODE.setText(arr.get(position).LOCATOR_CODE);
		LOCATOR_CODE.setGravity(Gravity.CENTER);
		LOCATOR_NAME.setText(arr.get(position).LOCATOR_NAME);
		LOCATOR_NAME.setGravity(Gravity.CENTER);

		return convertView;
	}

}


