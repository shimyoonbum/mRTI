package com.pulmuone.mrtina.prodTran;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.pulmuone.mrtina.R;

public class ProdTranAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private ArrayList<ProdTranItem> arr;


    public ProdTranAdapter(Activity activity, ArrayList<ProdTranItem> items) {
        this.mActivity = activity;
        this.arr = items;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_prod_tran, parent, false);
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        } else {
            convertView.setBackgroundColor(mActivity.getResources().getColor(R.color.lightgray));
        }

        TextView batch_no = (TextView)convertView.findViewById(R.id.txt_batch_no);
        TextView pallet_no = (TextView)convertView.findViewById(R.id.txt_pall_no);
        TextView item_no = (TextView)convertView.findViewById(R.id.txt_item_no);
        TextView item_name = (TextView)convertView.findViewById(R.id.txt_item_name);
        TextView recipe_no = (TextView)convertView.findViewById(R.id.txt_recipe_no);
        TextView recipe_ver = (TextView)convertView.findViewById(R.id.txt_recipe_ver);
        TextView planned_qty = (TextView)convertView.findViewById(R.id.txt_planned_qty);
        TextView total_qty = (TextView)convertView.findViewById(R.id.txt_total_qty);
        TextView comp_qty = (TextView)convertView.findViewById(R.id.txt_pallet_qty);
        TextView text_uom = (TextView)convertView.findViewById(R.id.txt_uom);
        TextView text_uom2 = (TextView)convertView.findViewById(R.id.txt_uom_2);
        TextView text_uom3 = (TextView)convertView.findViewById(R.id.txt_uom_3);


        batch_no.setText(arr.get(position).BATCH_NO);
        pallet_no.setText(arr.get(position).PALLET_NUMBER);
        item_no.setText(arr.get(position).ITEM_CODE);
        item_name.setText(arr.get(position).ITEM_DESC);
        recipe_no.setText(arr.get(position).RECIPE_NO);
        recipe_ver.setText(arr.get(position).RECIPE_VERSION);
        planned_qty.setText(arr.get(position).PL_PLAN_QTY);
        total_qty.setText(arr.get(position).PL_ACTUAL_QTY);
        comp_qty.setText(arr.get(position).COMP_QTY);

        if(arr.get(position).PLAN_QTY.equals("")){
            text_uom.setText("");
        }else{
            text_uom.setText(arr.get(position).PL_SKU_UOM);
        }

        if(arr.get(position).PL_ACTUAL_QTY.equals("")){
            text_uom2.setText("");
        }else{
            text_uom2.setText(arr.get(position).PL_SKU_UOM);
        }

        if(arr.get(position).COMP_QTY.equals("")){
            text_uom3.setText("");
        }else{
            text_uom3.setText(arr.get(position).PL_SKU_UOM);
        }

        return convertView;
    }
}
