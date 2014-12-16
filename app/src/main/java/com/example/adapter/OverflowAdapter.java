package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.item.ItemOption;
import com.jakkash.hdwallpaper.R;

import java.util.ArrayList;

/**
 * Created by jonathan on 15/12/2014.
 */
public class OverflowAdapter extends BaseAdapter {
    private ArrayList<ItemOption> itemOptions;
    private LayoutInflater inflater;
    private Context context;

    public OverflowAdapter(ArrayList<ItemOption> itemOptions, Context context){
        this.itemOptions = itemOptions;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return itemOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        ItemOption itemOption = itemOptions.get(position);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_overflow, parent, false);
            holder = new Holder();

            holder.img_option = (ImageView)convertView.findViewById(R.id.img_option);
            holder.txt_option = (TextView)convertView.findViewById(R.id.txt_option);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder)convertView.getTag();
        }

        holder.img_option.setImageResource(itemOption.getResource());
        holder.txt_option.setText(itemOption.getName());
        return convertView;
    }

    class Holder {
        ImageView img_option;
        TextView txt_option;
    }
}
