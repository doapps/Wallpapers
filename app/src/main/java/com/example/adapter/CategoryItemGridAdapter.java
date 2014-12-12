package com.example.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.imageloader.ImageLoader;
import com.example.item.ItemCategory;
import com.example.util.Constant;
import com.jakkash.hdwallpaper.R;

public class CategoryItemGridAdapter extends ArrayAdapter<ItemCategory> {
	
	private Activity activity;
	private List<ItemCategory> itemsCategory;
	private ItemCategory objCategoryBean;
	private int row;
	public ImageLoader imageLoader; 
	private int imageWidth;
	 public CategoryItemGridAdapter(Activity act, int resource, List<ItemCategory> arrayList, int columnWidth) {
			super(act, resource, arrayList);
			this.activity = act;
			this.row = resource;
			this.itemsCategory = arrayList;
			imageLoader=new ImageLoader(activity);
			this.imageWidth=columnWidth;
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

			if ((itemsCategory == null) || ((position + 1) > itemsCategory.size()))
				return view;

			objCategoryBean = itemsCategory.get(position);

			 
			holder.imgv_latetst=(ImageView)view.findViewById(R.id.item);
			 holder.imgv_latetst.setScaleType(ImageView.ScaleType.CENTER_CROP);
			 holder.imgv_latetst.setLayoutParams(new GridView.LayoutParams(imageWidth,
						imageWidth));
			 imageLoader.DisplayImage(Constant.SERVER_IMAGE_UPFOLDER_CATEGORY+objCategoryBean.getCategoryName()+"/"+objCategoryBean.getImageurl().toString(), holder.imgv_latetst);
			
			return view;
			
		}

		public class ViewHolder {
		 
			public ImageView imgv_latetst;
			
		}

}
