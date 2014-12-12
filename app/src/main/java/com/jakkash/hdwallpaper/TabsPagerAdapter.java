package com.jakkash.hdwallpaper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.me.doapps.v2.Slide_Image_Fragment_v2;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Latest fragment activity
			return new Slide_Image_Fragment_v2();
		case 1:
			// AllPhotos fragment activity
			return new AllPhotosFragment();
		case 2:
			// Favorite fragment activity
			return new FavoriteFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
