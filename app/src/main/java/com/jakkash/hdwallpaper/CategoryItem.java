package com.jakkash.hdwallpaper;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.adapter.CategoryItemGridAdapter;
import com.example.item.ItemCategory;
import com.example.util.AlertDialogManager;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CategoryItem extends SherlockActivity {
	
	GridView grid_cat_item;
	List<ItemCategory> arrayOfCategoryImage;
	CategoryItemGridAdapter objAdapter;
	AlertDialogManager alert = new AlertDialogManager();
	
	ArrayList<String> allListImage,allListImageCatName;
	String[] allArrayImage,allArrayImageCatName;
	 
	private int columnWidth;
	JsonUtils util;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_item_grid);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle(Constant.CATEGORY_TITLE);
		
		
		// Look up the AdView as a resource and load a request.
	    AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequestb= new AdRequest.Builder().build();
        adView.loadAd(adRequestb);

		grid_cat_item=(GridView)findViewById(R.id.category_grid);
		arrayOfCategoryImage=new ArrayList<ItemCategory>();
		
		allListImage=new ArrayList<String>();
		allListImageCatName=new ArrayList<String>();
	
		
		allArrayImage=new String[allListImage.size()];
		allArrayImageCatName=new String[allListImageCatName.size()];
	
		
		util=new JsonUtils(getApplicationContext());
		InitilizeGridLayout();
		
		grid_cat_item.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intslider=new Intent(getApplicationContext(),SlideImageActivity.class);
				intslider.putExtra("POSITION_ID", position);
				intslider.putExtra("IMAGE_ARRAY", allArrayImage);
 				intslider.putExtra("IMAGE_CATNAME", allArrayImageCatName);
	
				startActivity(intslider);
			}
		});
		
		if (JsonUtils.isNetworkAvailable(CategoryItem.this)) {
			new MyTask().execute(Constant.CATEGORY_ITEM_URL+Constant.CATEGORY_ID);
		} else {
			showToast("No Network Connection!!!");
			 alert.showAlertDialog(CategoryItem.this, "Internet Connection Error",
	                    "Please connect to working Internet connection", false);
		}
		
	}
	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				Constant.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((util.getScreenWidth() - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);

		grid_cat_item.setNumColumns(Constant.NUM_OF_COLUMNS);
		grid_cat_item.setColumnWidth(columnWidth);
		grid_cat_item.setStretchMode(GridView.NO_STRETCH);
		grid_cat_item.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		grid_cat_item.setHorizontalSpacing((int) padding);
		grid_cat_item.setVerticalSpacing((int) padding);
	}
	
	private	class MyTask extends AsyncTask<String, Void, String> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			//pDialog = new ProgressDialog(CategoryItem.this);
			//pDialog.setMessage("Loading...");
		//	pDialog.setCancelable(false);
		//	pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return JsonUtils.getJSONString(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (null != pDialog && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (null == result || result.length() == 0) {
				showToast("No data found from web!!!");
				CategoryItem.this.finish();
			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ITEM_ARRAY);
					JSONObject objJson = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						  objJson = jsonArray.getJSONObject(i);

						ItemCategory objItem = new ItemCategory();

						
						objItem.setCategoryName(objJson.getString(Constant.CATEGORY_ITEM_CATNAME));
						objItem.setImageurl(objJson.getString(Constant.CATEGORY_ITEM_IMAGEURL));
						
						
					
						arrayOfCategoryImage.add(objItem);
					 

					}
					 
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				 
				for(int j=0;j<arrayOfCategoryImage.size();j++)
				{
					 
					 
					ItemCategory objCategoryBean=arrayOfCategoryImage.get(j);
					
					allListImage.add(objCategoryBean.getImageurl());
					allArrayImage=allListImage.toArray(allArrayImage);
					
					allListImageCatName.add(objCategoryBean.getCategoryName());
					allArrayImageCatName=allListImageCatName.toArray(allArrayImageCatName);

				}
			
				 

  			setAdapterToListview();
  		}

		}
	}

	 
 
	public void setAdapterToListview() {
		objAdapter = new CategoryItemGridAdapter(CategoryItem.this, R.layout.latest_grid_item,
				arrayOfCategoryImage,columnWidth);
		grid_cat_item.setAdapter(objAdapter);
		
		
	}

	public void showToast(String msg) {
		Toast.makeText(CategoryItem.this, msg, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{       
		switch (menuItem.getItemId()) 
        {
        case android.R.id.home: 
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
	}

}
