package com.jakkash.hdwallpaper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.example.adapter.CategoryItemGridAdapter;
import com.example.adapter.OverflowAdapter;
import com.example.favorite.DatabaseHandler;
import com.example.favorite.Pojo;
import com.example.imageloader.ImageLoader;
import com.example.item.ItemAllPhotos;
import com.example.item.ItemCategory;
import com.example.item.ItemOption;
import com.example.util.AlertDialogManager;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.me.doapps.v2.Master;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathan on 17/12/2014.
 */
public class LastFragment extends Fragment implements SensorEventListener,View.OnClickListener {
    private int position;
    String[] mAllImages, mAllImageCatName;

    public DatabaseHandler db;
    ImageView vp_imageview;
    ViewPager viewpager;
    public ImageLoader imageLoader;
    private int TOTAL_IMAGE = 0;
    private String fileName;
    private SensorManager sensorManager;
    private boolean checkImage = false;
    private long lastUpdate;
    Handler handler;
    Runnable Update;
    boolean Play_Flag = false;
    private Menu menu;
    private DatabaseHandler.DatabaseManager dbManager;
    String Image_catName, Image_Url;
    Bitmap bgr;

    /**/
    private LinearLayout ic_home;
    private ImageView ic_menu_back;
    private ImageView ic_menu_play;
    private ImageView ic_menu_next;
    private ImageView ic_menu_fav;
    private ImageView ic_overflow;
    private ArrayList<ItemOption> itemOptions;
    private InterstitialAd interstitial;

    private int currentState, previousState;

    /*agregado*/
    List<ItemCategory> arrayOfCategoryImage;
    ArrayList<String> allListImage, allListImageCatName;
    String[] allArrayImage, allArrayImageCatName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = new DatabaseHandler(getActivity());
        dbManager = DatabaseHandler.DatabaseManager.INSTANCE;
        dbManager.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fullimageslider, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**agregado**/

        viewpager = (ViewPager) getActivity().findViewById(R.id.image_slider);
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTask_().execute(Constant.CATEGORY_ITEM_URL + "32");
        } else {
            showToast("No Network Connection!!!");
        }

        /****/
        ic_home = (LinearLayout) getActivity().findViewById(R.id.ic_home);
        ic_menu_back = (ImageView) getActivity().findViewById(R.id.ic_menu_back);
        ic_menu_next = (ImageView) getActivity().findViewById(R.id.ic_menu_next);
        ic_menu_fav = (ImageView) getActivity().findViewById(R.id.ic_menu_fav);
        ic_overflow = (ImageView) getActivity().findViewById(R.id.ic_overflow);

        ic_home.setOnClickListener(this);
        ic_menu_back.setOnClickListener(this);
        ic_menu_next.setOnClickListener(this);
        ic_menu_fav.setOnClickListener(this);
        ic_overflow.setOnClickListener(this);

        itemOptions = new ArrayList<ItemOption>();
        itemOptions.add(new ItemOption("Compartir", R.drawable.share, 1));
        itemOptions.add(new ItemOption("Fondo pantalla", R.drawable.set, 2));
        itemOptions.add(new ItemOption("Guardar", R.drawable.save, 3));
        itemOptions.add(new ItemOption("Puntuar", R.drawable.rate, 4));
        itemOptions.add(new ItemOption("Acerca de", R.drawable.about, 5));
        itemOptions.add(new ItemOption("Más Apps", R.drawable.more, 6));

        /**
         * Admob
         */
        interstitial = new InterstitialAd(getActivity());
        interstitial.setAdUnitId(getString(R.string.admob_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);
        /****/


        AdView adView = (AdView) getActivity().findViewById(R.id.adView);
        AdRequest adRequestb = new AdRequest.Builder().build();
        adView.loadAd(adRequestb);





        imageLoader = new ImageLoader(getActivity());
        handler = new Handler();


        arrayOfCategoryImage = new ArrayList<ItemCategory>();
        allListImage = new ArrayList<String>();
        allListImageCatName = new ArrayList<String>();


        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                position = viewpager.getCurrentItem();
                Image_Url = mAllImages[position];

                List<Pojo> pojolist = db.getFavRow(Image_Url);
                if (pojolist.size() == 0) {
                    ic_menu_fav.setImageResource(R.drawable.fav);
                    //menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav));

                } else {
                    if (pojolist.get(0).getImageurl().equals(Image_Url)) {
                        ic_menu_fav.setImageResource(R.drawable.fav_hover);
                        //menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav_hover));
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /**
                 * Solo 30 Imagenes y luego no vuelve a cargar
                 */
                    /*if (position == 30) {
                        if (interstitial.isLoaded()) {
                            interstitial.show();
                        }
                    }*/


                /**
                 * Cada 30 - 60 - 90 - 120 Imagenes
                 */
                if (position == 30 || position == 60 || position == 120 || position == 240) {
                    if (interstitial.isLoaded()) {
                        interstitial.show();
                    }
                } else {
                    if (!interstitial.isLoaded()) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        interstitial.loadAd(adRequest);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /*if (state == ViewPager.SCROLL_STATE_IDLE) {
                    if(position == 0){
                        viewpager.setCurrentItem(TOTAL_IMAGE-2, false);
                    }
                    else{
                        if(position == TOTAL_IMAGE-1){
                            viewpager.setCurrentItem(1, false);
                        }
                    }
                }*/
                int currentPage = viewpager.getCurrentItem();
                Log.e("TAG", "currentPage::" + currentPage);
                Log.e("TAG", "currentState::" + currentState);
                Log.e("TAG", "previousState::" + previousState);
                if (currentPage == TOTAL_IMAGE-1 || currentPage == 0) {
                    previousState = currentState;
                    currentState = state;
                    if (previousState == 1 && currentState == 0) {
                        viewpager.setCurrentItem(currentPage == 0 ? TOTAL_IMAGE-1 : 0);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_home:
                startActivity(new Intent(getActivity(), MainActivity.class));
                break;
            case R.id.ic_menu_back:
                position = viewpager.getCurrentItem();
                position--;
                if (position < 0) {
                    position = TOTAL_IMAGE-1;
                }
                viewpager.setCurrentItem(position);
                break;
            case R.id.ic_menu_next:
                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = 0;
                }
                viewpager.setCurrentItem(position);
                break;
            case R.id.ic_menu_fav:
                position = viewpager.getCurrentItem();
                Image_Url = mAllImages[position];
                List<Pojo> pojolist = db.getFavRow(Image_Url);
                if (pojolist.size() == 0) {
                    AddtoFav(position);//if size is zero i.e means that record not in database show add to favorite
                } else {
                    if (pojolist.get(0).getImageurl().equals(Image_Url)) {
                        RemoveFav(position);
                    }
                }
                break;
            case R.id.ic_overflow:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setAdapter(new OverflowAdapter(itemOptions, getActivity()), onClickListener);
                builder.create();
                builder.show();
            default:
                break;
        }
    }


    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            ListView listView = ((AlertDialog) dialog).getListView();
            int id = ((ItemOption) listView.getAdapter().getItem(which)).getId();
            dialog.dismiss();
            switch (id) {
                case 1:
                    Share();
                    break;
                case 2:
                    SetAsWallpaper();
                    break;
                case 3:
                    save();
                    break;
                case 4:
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
                case 5:
                    Intent about = new Intent(getActivity(), AboutActivity.class);
                    startActivity(about);
                    break;
                case 6:
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/search?q=jakkash+application")));
                    break;
                default:
                    break;
            }
        }
    };
    //add to favorite

    public void AddtoFav(int position) {

        Image_catName = mAllImageCatName[position];
        Image_Url = mAllImages[position];

        db.AddtoFavorite(new Pojo(Image_catName, Image_Url));
        Toast toast = Toast.makeText(getActivity(), "Added to Favorite", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
        //menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav_hover));
        ic_menu_fav.setImageResource(R.drawable.fav_hover);
    }

    //remove from edsxfavorite
    public void RemoveFav(int position) {
        Image_Url = mAllImages[position];
        db.RemoveFav(new Pojo(Image_Url));
        Toast toast = Toast.makeText(getActivity(), "Removed from Favorite", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 150);
        toast.show();
        //menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav));
        ic_menu_fav.setImageResource(R.drawable.fav);

    }

    //for share current image of view  pager

    public void Share() {
        viewpager.buildDrawingCache();
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path,
                "Android/data/com.example.actionbartest/cache/share_cache.jpg");//default  path of android
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.e("draw_save", e.toString());
        }

        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            Log.e("draw_save1", e.toString());
        }

        if (this.viewpager.getDrawingCache() == null) {
            Log.e("lal", "tis null");
        }

        this.viewpager.getDrawingCache()
                .compress(Bitmap.CompressFormat.JPEG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("draw_save1", e.toString());
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    //set as Wall paper

    public void SetAsWallpaper() {
        viewpager.buildDrawingCache();
        Bitmap mBitmap = viewpager.getDrawingCache();

        WallpaperManager myWallpaperManager = WallpaperManager
                .getInstance(getActivity());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels << 1;
        int width1 = width / 2;
        mBitmap = Bitmap.createScaledBitmap(mBitmap, width1, height, true);
        try {
            myWallpaperManager.setBitmap(mBitmap);
            myWallpaperManager.suggestDesiredDimensions(width1, height);
            Toast toast = Toast.makeText(getActivity(), "Wallpaper set", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 150);
            toast.show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Error setting wallpaper",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //for Save File in SdCard

    public void save() { // called on save menu
        Log.e("saving1", "a");
        if (this.fileName == null) {
            Log.e("saving2", "a");
            this.saveDialog();
        } else {
            Log.e("saving3", "a");
            this.saveToFile(this.fileName);
        }
    }

    public void saveDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Save file...");
        alert.setMessage("File name to save ");
        final EditText input = new EditText(getActivity());
        input.setText("");
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String fname = input.getText().toString();
                if (fname.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Enter File Name", Toast.LENGTH_SHORT).show();
                } else {
                    String path = Environment.getExternalStorageDirectory().toString();
                    File f = new File(path, "Wallpaper/" + fname + ".jpg");
                    Toast.makeText(getActivity(), "Save Path:Sdcard/Wallpaper", Toast.LENGTH_SHORT).show();

                    if (f.exists()) {
                        fileExistsConfirmationDialog(fname);
                    } else {
                        saveToFile(fname);
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    public void fileExistsConfirmationDialog(final String fname) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Error");
        alert.setMessage("The file \"" + fname
                + "\" already exists, do you wish to overwrite it?");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveToFile(fname);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    public void saveToFile(String fname) {
        this.viewpager.buildDrawingCache();

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "Wallpaper/" + fname + ".jpg");
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.e("draw_save", e.toString());
        }

        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            Log.e("draw_save1", e.toString());
        }

        if (this.viewpager.getDrawingCache() == null) {
            Log.e("lal", "tis null");
        }

        this.viewpager.getDrawingCache()
                .compress(Bitmap.CompressFormat.JPEG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("draw_save1", e.toString());
        }
    }

    //auto play slide show

    public void AutoPlay() {
        Update = new Runnable() {

            @Override
            public void run() {
                AutoPlay();
                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = TOTAL_IMAGE;
                    handler.removeCallbacks(Update);//when last image play mode goes to Stop
                    Toast.makeText(getActivity(), "Last Image Auto Play Stoped", Toast.LENGTH_SHORT).show();
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.play));
                    Play_Flag = false;
                    //Show All Menu when Auto Play Stop
                    ShowMenu();
                }
                viewpager.setCurrentItem(position);

            }
        };

        handler.postDelayed(Update, 1500);
    }

    public void ShowMenu() {
        menu.getItem(0).setVisible(true);
        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(true);
        menu.getItem(4).setVisible(true);
    }

    public void HideMenu() {
        menu.getItem(0).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
    }

    public void FirstFav() {
        int first = viewpager.getCurrentItem();
        String Image_id = mAllImages[first];

        List<Pojo> pojolist = db.getFavRow(Image_id);
        if (pojolist.size() == 0) {
            menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav));

        } else {
            if (pojolist.get(0).getImageurl().equals(Image_id)) {
                menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav_hover));
                ic_menu_fav.setImageResource(R.drawable.fav_hover);
            }

        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        public ImagePagerAdapter() {
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mAllImages.length;

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.test, container, false);
            assert imageLayout != null;

            WebView webview = (WebView) imageLayout.findViewById(R.id.webView1);
            final ProgressBar bar = (ProgressBar) imageLayout.findViewById(R.id.progressBar1);

            WebSettings websettings = webview.getSettings();
            websettings.setJavaScriptEnabled(true);
            websettings.setDomStorageEnabled(true);
            websettings.setCacheMode(1);
            websettings.setAppCacheMaxSize(0x800000L);

            webview.loadDataWithBaseURL(null, "<body style=margin:0;padding:0><img src=\"" + Constant.SERVER_IMAGE_UPFOLDER_CATEGORY + mAllImageCatName[position] + "/" + mAllImages[position] + "\"" + "alt=\"pageNo\" width=\"100%\" align=\"left\" height=\"100%\"></body>", "text/html", "UTF-8", null);
            webview.capturePicture();

            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                    super.onProgressChanged(view, newProgress);

                    if (newProgress == 100) {
                        bar.setVisibility(View.GONE);
                    } else {
                        bar.setVisibility(View.VISIBLE);
                    }

                }


            });

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    /****/
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


                position = 0;
                mAllImages = allArrayImage;
                mAllImageCatName = allArrayImageCatName;

                TOTAL_IMAGE = mAllImages.length - 1;

                ImagePagerAdapter adapter = new ImagePagerAdapter();
                viewpager.setAdapter(adapter);
                viewpager.setCurrentItem(position);
            }

        }
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

/*Sensor*/
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            if (checkImage) {
                position = viewpager.getCurrentItem();
                viewpager.setCurrentItem(position);
            } else {

                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = TOTAL_IMAGE;
                }
                viewpager.setCurrentItem(position);
            }
            checkImage = !checkImage;
        }
    }


}
