package com.jakkash.hdwallpaper;

import java.util.ArrayList;
import java.util.List;







import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.AllPhotosListAdapter;
import com.example.item.ItemAllPhotos;
import com.example.util.AlertDialogManager;
import com.example.util.Constant;
import com.example.util.JsonUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class AllPhotosFragment extends Fragment {
	
	ListView lsv_allphotos;
	List<ItemAllPhotos> arrayOfAllphotos;
	AllPhotosListAdapter objAdapter;
	AlertDialogManager alert = new AlertDialogManager();
	private ItemAllPhotos objAllBean;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_allphotos, container, false);
		lsv_allphotos=(ListView)rootView.findViewById(R.id.lsv_allphotos);
		arrayOfAllphotos=new ArrayList<ItemAllPhotos>();
		
		lsv_allphotos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				objAllBean=arrayOfAllphotos.get(position);
				int Catid=objAllBean.getCategoryId();
				Constant.CATEGORY_ID=objAllBean.getCategoryId();
				Log.e("cat_id",""+Catid);
				Constant.CATEGORY_TITLE=objAllBean.getCategoryName();
				
				Intent intcat=new Intent(getActivity(),CategoryItem.class);
				startActivity(intcat);
				
				
			}
		});
		
		
		if (JsonUtils.isNetworkAvailable(getActivity())) {
			new MyTask().execute(Constant.CATEGORY_URL);
		} else {
			showToast("No Network Connection!!!");
			 alert.showAlertDialog(getActivity(), "Internet Connection Error",
	                    "Please connect to working Internet connection", false);
		}
		
		return rootView;
	}
	private	class MyTask extends AsyncTask<String, Void, String> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		//	pDialog = new ProgressDialog(getActivity());
		//	pDialog.setMessage("Loading...");
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
				 
			} else {

				try {
					JSONObject mainJson = new JSONObject(result);
					JSONArray jsonArray = mainJson.getJSONArray(Constant.CATEGORY_ARRAY_NAME);
					JSONObject objJson = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						  objJson = jsonArray.getJSONObject(i);

						ItemAllPhotos objItem = new ItemAllPhotos();
						objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
						objItem.setCategoryId(objJson.getInt(Constant.CATEGORY_CID));
						objItem.setCategoryImage(objJson.getString(Constant.CATEGORY_IMAGE_URL));
						arrayOfAllphotos.add(objItem);
					 

					}
					 
				} catch (JSONException e) {
					e.printStackTrace();
				}
				 

  			setAdapterToListview();
  		}

		}
	}

	 
 
	public void setAdapterToListview() {
		objAdapter = new AllPhotosListAdapter(getActivity(), R.layout.allphotos_lsv_item,
				arrayOfAllphotos);
		lsv_allphotos.setAdapter(objAdapter);
	}

	public void showToast(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
}
