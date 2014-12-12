package com.example.adapter;

import com.example.imageloader.ImageLoader;
import com.example.util.Constant;
import com.example.util.TouchImageView;
import com.jakkash.hdwallpaper.R;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class PinchZoomPageAdapter extends PagerAdapter{
	
	private Activity _activity;
	String[] mZoomImages,mZoomCatName;
	private LayoutInflater inflater;
	public ImageLoader imageLoader; 

	public PinchZoomPageAdapter(Activity activity, String[] mZoomImages, String[] mZoomCatName) {
		// TODO Auto-generated constructor stub
		
		this._activity=activity;
		this.mZoomImages=mZoomImages;
		this.mZoomCatName=mZoomCatName;
		imageLoader=new ImageLoader(activity);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mZoomImages.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		 return view == ((RelativeLayout) object);
	}
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        Button btnClose;
 
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.pich_zoom_item, container,
                false);
 
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        
        imageLoader.DisplayImage(Constant.SERVER_IMAGE_UPFOLDER_CATEGORY+mZoomCatName[position]+"/"+mZoomImages[position], imgDisplay);
        
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				_activity.finish();
			}
		}); 

        ((ViewPager) container).addView(viewLayout);
 
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
 
    }

}
