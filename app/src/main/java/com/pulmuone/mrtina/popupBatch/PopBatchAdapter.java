package com.pulmuone.mrtina.popupBatch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pulmuone.mrtina.R;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.txt_batch_no;
import static com.pulmuone.mrtina.R.id.txt_item_no;
import static com.pulmuone.mrtina.R.id.txt_plan_date;

public class PopBatchAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<PopBatchItem> arr;

	public PopBatchAdapter(Activity act, ArrayList<PopBatchItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_popup_batch, parent, false);
		}

		TextView BATCH_NO 		= (TextView)convertView.findViewById(txt_batch_no);
		TextView ITEM_NO		= (TextView)convertView.findViewById(txt_item_no);
		TextView PLANNED_DATE	= (TextView)convertView.findViewById(txt_plan_date);

		BATCH_NO.setText(arr.get(position).BATCH_NO);
		ITEM_NO.setText(arr.get(position).ITEM_CODE);
		PLANNED_DATE.setText(arr.get(position).PLAN_START_DATE);

		return convertView;
	}
}


