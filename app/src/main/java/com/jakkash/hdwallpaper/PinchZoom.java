package com.jakkash.hdwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockActivity;
import com.example.adapter.PinchZoomPageAdapter;

public class PinchZoom extends SherlockActivity {
	
	String[] mZoomImages,mZoomCatName;
	ViewPager vp_zoom;
	PinchZoomPageAdapter adapter;
	int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pinchzoom);
		getSupportActionBar().hide();
		vp_zoom=(ViewPager)findViewById(R.id.zoompager);
		
		Intent i=getIntent();
		mZoomImages=i.getStringArrayExtra("ZOOM_IMAGE_URL");
		mZoomCatName=i.getStringArrayExtra("ZOOM_IMAGE_CATEGORY");
		position=i.getIntExtra("POSITION_ID", 0);
		
//		for(int i1=0;i1<mZoomImages.length;i1++)
//		{
//			Log.e("aa", ""+mZoomImages[i1]);
//		}
		
		adapter=new PinchZoomPageAdapter(PinchZoom.this,mZoomImages,mZoomCatName);
		vp_zoom.setAdapter(adapter);
		vp_zoom.setCurrentItem(position);
		
	}

}
