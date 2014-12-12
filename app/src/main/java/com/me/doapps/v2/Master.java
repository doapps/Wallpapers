package com.me.doapps.v2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.item.ItemLatest;
import com.example.util.AlertDialogManager;
import com.example.util.Constant;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gantz on 12/12/14.
 */
public class Master extends SherlockFragmentActivity {

    private I_Load_Images i_load_images;

    private ArrayList<String> allListImage, allListImageCatName;
    private String[] allArrayImage, allArrayImageCatName;
    private List<ItemLatest> arrayOfLatestImage;
    private ItemLatest objAllBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arrayOfLatestImage = new ArrayList<ItemLatest>();
        allListImage = new ArrayList<String>();
        allListImageCatName = new ArrayList<String>();


        allArrayImage = new String[allListImage.size()];
        allArrayImageCatName = new String[allListImageCatName.size()];
    }

    public String[] getAllArrayImage() {
        return allArrayImage;
    }

    public String[] getAllArrayImageCatName() {
        return allArrayImageCatName;
    }

    public void loadImages(){
        if (JsonUtils.isNetworkAvailable(this)) {
            new MyTask().execute(Constant.LATEST_URL);
        } else {
            showToast("No Network Connection!!!");
            new AlertDialogManager().showAlertDialog(this,"Internet Connection Error","Please connect to working Internet connection", false);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
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
                showToast("Server Connection Error");
                new AlertDialogManager().showAlertDialog(Master.this,"Server Connection Error","May Server Under Maintaines Or Low Network", false);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.LATEST_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemLatest objItem = new ItemLatest();

                        objItem.setCategoryName(objJson.getString(Constant.LATEST_IMAGE_CATEGORY_NAME));
                        objItem.setImageurl(objJson.getString(Constant.LATEST_IMAGE_URL));

                        arrayOfLatestImage.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 30; i++) {
                    for (int j = 0; j < arrayOfLatestImage.size(); j++) {
                        objAllBean = arrayOfLatestImage.get(j);
                        allListImage.add(objAllBean.getImageurl());
                        allListImageCatName.add(objAllBean.getCategoryName());
                        allArrayImageCatName = allListImageCatName.toArray(allArrayImageCatName);
                    }
                    if(i == 29){
                        allArrayImage = allListImage.toArray(allArrayImage);
                    }
                }

                i_load_images.onFinishLoad(false);
            }
        }
    }

    public void setI_load_images(I_Load_Images i_load_images) {
        this.i_load_images = i_load_images;
    }

    public interface I_Load_Images{
        void onFinishLoad(boolean status);
    }
}
