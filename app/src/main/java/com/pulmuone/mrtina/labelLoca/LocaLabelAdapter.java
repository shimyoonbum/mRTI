package com.pulmuone.mrtina.labelLoca;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pulmuone.mrtina.R;
import com.pulmuone.mrtina.comm.Comm;

import java.util.ArrayList;

import static com.pulmuone.mrtina.R.id.chk_label_pallet;
import static com.pulmuone.mrtina.R.id.txt_batch_no;
import static com.pulmuone.mrtina.R.id.txt_item_name;
import static com.pulmuone.mrtina.R.id.txt_item_no;
import static com.pulmuone.mrtina.R.id.txt_locator;
import static com.pulmuone.mrtina.R.id.txt_pallet_no;
import static com.pulmuone.mrtina.R.id.txt_subinv;


public class LocaLabelAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private Activity m_activity;
	private ArrayList<LocaLabelItem> arr;

	public LocaLabelAdapter(Activity act, ArrayList<LocaLabelItem> arr_item) {
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
			convertView = mInflater.inflate(R.layout.item_label_locator, parent, false);
		}

		if (position % 2 == 0) {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.white));
		} else {
			convertView.setBackgroundColor(m_activity.getResources().getColor(R.color.lightgray));
		}

		final CheckBox ITEM_CHK 	= (CheckBox)convertView.findViewById(chk_label_pallet);
		TextView PALLET_NUMBER 		= (TextView)convertView.findViewById(txt_pallet_no);
		TextView ITEM_CODE 			= (TextView)convertView.findViewById(txt_item_no);
		TextView ITEM_DESC 			= (TextView)convertView.findViewById(txt_item_name);
		TextView SUBINVENTORY_CODE 	= (TextView)convertView.findViewById(txt_subinv);
		TextView LOC_CODE 	= (TextView)convertView.findViewById(txt_locator);

		PALLET_NUMBER.setText(arr.get(position).PALLET_NUMBER);
		ITEM_CODE.setText(arr.get(position).ITEM_CODE);
		ITEM_DESC.setText(arr.get(position).ITEM_DESC);
		SUBINVENTORY_CODE.setText(arr.get(position).SUBINVENTORY_CODE);
		LOC_CODE.setText(arr.get(position).LOCATOR_CODE);

		if(arr.get(position).CHECK_ITEM.equals("True")){
			ITEM_CHK.setChecked(true);
		}else{
			ITEM_CHK.setChecked(false);
		}

		ITEM_CHK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ITEM_CHK.isChecked()){
					Log.v(Comm.LOG_TAG, "Checked " );
					ITEM_CHK.setChecked(true);
					arr.get(position).CHECK_ITEM = "True";
				} else{
					Log.v(Comm.LOG_TAG, "unChecked " );
					ITEM_CHK.setChecked(false);
					arr.get(position).CHECK_ITEM = "False";
				}
			}
		});

		return convertView;
	}
}


