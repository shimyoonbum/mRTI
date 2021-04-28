package com.pulmuone.mrtina.popupPickTicket;

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

public class PopPickTicketAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopPickTicketItem> arr;

	public PopPickTicketAdapter(Activity act, ArrayList<PopPickTicketItem> arr_item) {
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

		TextView SO_TRO_NO = (TextView)convertView.findViewById(txt_code);
		TextView SHIP_DATE = (TextView)convertView.findViewById(txt_name);

		SO_TRO_NO.setText(arr.get(position).SO_TRO_NO);
		SHIP_DATE.setText(arr.get(position).SHIP_DATE);

		return convertView;
	}

}


