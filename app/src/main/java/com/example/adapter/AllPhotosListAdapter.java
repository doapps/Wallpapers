package com.example.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imageloader.ImageLoader;
import com.example.item.ItemAllPhotos;
import com.example.util.Constant;
import com.example.util.UtilFonts;
import com.jakkash.hdwallpaper.R;

public class AllPhotosListAdapter extends ArrayAdapter<ItemAllPhotos> {

    private Activity activity;
    private List<ItemAllPhotos> itemsAllphotos;
    private ItemAllPhotos objAllBean;
    private int row;
    ImageLoader imageloader;

    public AllPhotosListAdapter(Activity act, int resource, List<ItemAllPhotos> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.itemsAllphotos = arrayList;
        imageloader = new ImageLoader(activity);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((itemsAllphotos == null) || ((position + 1) > itemsAllphotos.size()))
            return view;

        objAllBean = itemsAllphotos.get(position);
        holder.txt = (TextView) view.findViewById(R.id.txt_allphotos_categty);
        holder.img_cat = (ImageView) view.findViewById(R.id.image_category);
        holder.txt.setText(objAllBean.getCategoryName().toString());
        holder.txt.setTypeface(UtilFonts.setFight(getContext()));
        imageloader.DisplayImage(Constant.SERVER_IMAGE_UPFOLDER_THUMB + objAllBean.getCategoryImage(), holder.img_cat);
        return view;
    }

    public class ViewHolder {

        public TextView txt;
        public ImageView img_cat;
    }

}
