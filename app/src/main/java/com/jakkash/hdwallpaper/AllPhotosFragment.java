package com.jakkash.hdwallpaper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.AllPhotosListAdapter;
import com.example.adapter.CategoryItemGridAdapter;
import com.example.adapter.OverflowAdapter;
import com.example.item.ItemAllPhotos;
import com.example.item.ItemCategory;
import com.example.item.ItemOption;
import com.example.util.AlertDialogManager;
import com.example.util.Constant;
import com.example.util.JsonUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class AllPhotosFragment extends Fragment implements View.OnClickListener {

    ListView lsv_allphotos;
    List<ItemAllPhotos> arrayOfAllphotos;
    AllPhotosListAdapter objAdapter;
    AlertDialogManager alert = new AlertDialogManager();
    private ItemAllPhotos objAllBean;

    /*agregado*/
    List<ItemCategory> arrayOfCategoryImage;
    ArrayList<String> allListImage, allListImageCatName;
    String[] allArrayImage, allArrayImageCatName;

    /****/
    private ImageView ic_menu_rate;
    private ImageView ic_menu_about;
    private ImageView ic_menu_more;
    private int Catid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_allphotos, container, false);
        lsv_allphotos = (ListView) rootView.findViewById(R.id.lsv_allphotos);

        ic_menu_rate = (ImageView) rootView.findViewById(R.id.ic_menu_rate);
        ic_menu_about = (ImageView) rootView.findViewById(R.id.ic_menu_about);
        ic_menu_more = (ImageView) rootView.findViewById(R.id.ic_menu_more);
        ic_menu_rate.setOnClickListener(this);
        ic_menu_about.setOnClickListener(this);
        ic_menu_more.setOnClickListener(this);

        arrayOfAllphotos = new ArrayList<ItemAllPhotos>();
        arrayOfCategoryImage = new ArrayList<ItemCategory>();
        allListImage = new ArrayList<String>();
        allListImageCatName = new ArrayList<String>();


        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Constant.CATEGORY_URL);
        } else {
            showToast("No Network Connection!!!");
            alert.showAlertDialog(getActivity(), "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        }


        lsv_allphotos.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                objAllBean = arrayOfAllphotos.get(position);
                Catid = objAllBean.getCategoryId();
                Log.e("cat_id", Catid+"");
                Constant.CATEGORY_ID = objAllBean.getCategoryId();
                Constant.CATEGORY_TITLE = objAllBean.getCategoryName();

				/*Intent intcat=new Intent(getActivity(),CategoryItem.class);
                startActivity(intcat);*/

                /**agregado**/
                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    new MyTask_().execute(Constant.CATEGORY_ITEM_URL + Catid);
                } else {
                    showToast("No Network Connection!!!");
                    alert.showAlertDialog(getActivity(), "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_menu_rate:
                final String appName = "com.jakkash.apksaver";//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
                break;
            case R.id.ic_menu_about:
                Intent about = new Intent(getActivity(), AboutActivity.class);
                startActivity(about);
                break;
            case R.id.ic_menu_more:
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/search?q=jakkash+application")));
                break;
            default:
                break;
        }
    }



    private class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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


    private class MyTask_ extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("result_", result);
            super.onPostExecute(result);
            arrayOfCategoryImage.clear();
            allListImage.clear();
            allListImageCatName.clear();


            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast("No data found from web!!!");
                //CategoryItem.this.finish();
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

                for (int j = 0; j < arrayOfCategoryImage.size(); j++) {
                    ItemCategory objCategoryBean = arrayOfCategoryImage.get(j);
                    allListImage.add(objCategoryBean.getImageurl());
                    allListImageCatName.add(objCategoryBean.getCategoryName());
                }

                allArrayImage = new String[allListImage.size()];
                Log.e("all image size", allListImage.size()+"");

                allArrayImageCatName = new String[allListImageCatName.size()];
                Log.e("allListImageCatName", allListImageCatName.size()+"");


                allArrayImage = allListImage.toArray(allArrayImage);
                allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);

                setAdapterToListview_();

                Intent intslider = new Intent(getActivity(), SlideImageActivity.class);
                Log.e("params", "image_array:"+allArrayImage.length);
                intslider.putExtra("POSITION_ID", 0);
                intslider.putExtra("IMAGE_ARRAY", allArrayImage);
                intslider.putExtra("IMAGE_CATNAME", allArrayImageCatName);
                pDialog.dismiss();
                startActivity(intslider);
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

    public void setAdapterToListview_() {
        /*objAdapter = new CategoryItemGridAdapter(CategoryItem.this, R.layout.latest_grid_item,
                arrayOfCategoryImage,columnWidth);
        grid_cat_item.setAdapter(objAdapter);*/
    }


}
