package com.me.doapps.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.adapter.OverflowAdapter;
import com.example.favorite.DatabaseHandler;
import com.example.favorite.DatabaseHandler.DatabaseManager;
import com.example.favorite.Pojo;
import com.example.imageloader.ImageLoader;
import com.example.item.ItemOption;
import com.example.util.Constant;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jakkash.hdwallpaper.AboutActivity;
import com.jakkash.hdwallpaper.MainActivity;
import com.jakkash.hdwallpaper.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Slide_Image_Fragment_v2 extends SherlockFragment implements SensorEventListener, Master.I_Load_Images, View.OnClickListener {

    int position;
    String[] mAllImages, mAllImageCatName;

    public DatabaseHandler db;
    ImageView vp_imageview;
    ViewPager viewpager;
    public ImageLoader imageLoader;
    int TOTAL_IMAGE;
    private String fileName;
    private SensorManager sensorManager;
    private boolean checkImage = false;
    private long lastUpdate;
    Handler handler;
    Runnable Update;
    boolean Play_Flag = false;
    private Menu menu;
    private DatabaseManager dbManager;
    String Image_catName, Image_Url;
    Bitmap bgr;

    /****/
    private LinearLayout ic_home;
    private ImageView ic_menu_back;
    private ImageView ic_menu_play;
    private ImageView ic_menu_next;
    private ImageView ic_menu_fav;
    private ImageView ic_overflow;
    private ArrayList<ItemOption> itemOptions;

    private InterstitialAd interstitial;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((Master) getActivity()).setI_load_images(this);
        ((Master) getActivity()).loadImages();

        /**
         * Admob
         */
        interstitial = new InterstitialAd(getSherlockActivity());
        interstitial.setAdUnitId(getString(R.string.admob_interstitial));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(getActivity());
        dbManager = DatabaseManager.INSTANCE;
        dbManager.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fullimageslider, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sensorManager = (SensorManager) getActivity().getSystemService(Activity.SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        /****/
        ic_home = (LinearLayout) getView().findViewById(R.id.ic_home);
        ic_menu_back = (ImageView) getView().findViewById(R.id.ic_menu_back);
        ic_menu_next = (ImageView) getView().findViewById(R.id.ic_menu_next);
        ic_menu_fav = (ImageView) getView().findViewById(R.id.ic_menu_fav);
        ic_overflow = (ImageView) getView().findViewById(R.id.ic_overflow);

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
        /****/

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.photo_menu, menu);
        this.menu = menu;
        //for when 1st item of view pager is favorite mode
        FirstFav();
        return super.onCreateOptionsMenu(menu);
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                //onBackPressed();
                return true;

            case R.id.menu_back:

                position = viewpager.getCurrentItem();
                position--;
                if (position < 0) {
                    position = 0;
                }
                viewpager.setCurrentItem(position);

                return true;

            case R.id.menu_next:

                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = TOTAL_IMAGE;
                }
                viewpager.setCurrentItem(position);

                return true;

            case R.id.menu_play:

                if (Play_Flag) {
                    handler.removeCallbacks(Update);
                    menuItem.setIcon(getResources().getDrawable(R.drawable.play));
                    Play_Flag = false;
                    ShowMenu();
                } else {
                    if (viewpager.getCurrentItem() == TOTAL_IMAGE) {
                        Toast.makeText(getActivity(), "Currently Last Image!! Not Start Auto Play", Toast.LENGTH_SHORT).show();
                    } else {
                        AutoPlay();
                        menuItem.setIcon(getResources().getDrawable(R.drawable.stop));
                        Play_Flag = true;
                        HideMenu();
                    }


                }
                return true;

            case R.id.menu_fav:

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

                return true;

            case R.id.menu_overflow:
                //just override click
                return true;
            case R.id.menu_share:

                Share();
                return true;

            case R.id.menu_save:

                save();
                return true;

            case R.id.menu_setaswallaper:

                SetAsWallpaper();
                return true;


            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }

    //add to favorite

    public void AddtoFav(int position) {

        Image_catName = mAllImageCatName[position];
        Image_Url = mAllImages[position];

        db.AddtoFavorite(new Pojo(Image_catName, Image_Url));
        Toast.makeText(getActivity(), "Added to Favorite", Toast.LENGTH_SHORT).show();
        //menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav_hover));
        ic_menu_fav.setImageResource(R.drawable.fav_hover);
    }

    //remove from favorite
    public void RemoveFav(int position) {
        Image_Url = mAllImages[position];
        db.RemoveFav(new Pojo(Image_Url));
        Toast.makeText(getActivity(), "Removed from Favorite", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), "Wallpaper set", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Error setting wallpaper", Toast.LENGTH_SHORT).show();
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
            ic_menu_fav.setImageResource(R.drawable.fav);

        } else {
            if (pojolist.get(0).getImageurl().equals(Image_id)) {
                menu.getItem(3).setIcon(getResources().getDrawable(R.drawable.fav_hover));
                ic_menu_fav.setImageResource(R.drawable.fav_hover);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_home:
                break;
            case R.id.ic_menu_back:
                position = viewpager.getCurrentItem();
                position--;
                if (position < 0) {
                    position = 0;
                }
                viewpager.setCurrentItem(position);
                break;
            case R.id.ic_menu_next:
                position = viewpager.getCurrentItem();
                position++;
                if (position == TOTAL_IMAGE) {
                    position = TOTAL_IMAGE;
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

//	      Context context = SlideImageActivity.this;
//  	      vp_imageview = new ImageView(context);

//	      int padding = context.getResources().getDimensionPixelSize(
//	          R.dimen.padding_medium);
//	      imageView.setPadding(padding, padding, padding, padding);
//	      vp_imageview.setScaleType(ImageView.ScaleType.FIT_XY);
//	      imageLoader.DisplayImage(Constant.SERVER_IMAGE_UPFOLDER+mAllImages[position], vp_imageview);
//	      ((ViewPager) container).addView(vp_imageview, 0);

            View imageLayout = inflater.inflate(R.layout.webview, container, false);
            assert imageLayout != null;

            ImageView webview = (ImageView) imageLayout.findViewById(R.id.webView1);
            final ProgressBar bar = (ProgressBar) imageLayout.findViewById(R.id.progressBar1);

            String url = Constant.SERVER_IMAGE_UPFOLDER_CATEGORY + mAllImageCatName[position] + "/" + mAllImages[position];
            Picasso.with(getSherlockActivity()).load(url).fit().into(webview);

            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        if (dbManager == null) {
            dbManager = DatabaseManager.INSTANCE;
            dbManager.init(getActivity());
        } else if (dbManager.isDatabaseClosed()) {
            dbManager.init(getActivity());
        }
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
    @Override
    public void onPause() {
        super.onPause();
        if (!dbManager.isDatabaseClosed())
            dbManager.closeDatabase();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(Update);
        sensorManager.unregisterListener(this);
        if (dbManager != null) dbManager.closeDatabase();
    }
    */

    @Override
    public void onFinishLoad(boolean status) {
        if (!status) {
            /**
             *
             */
            position = 0;
            mAllImages = ((Master) getActivity()).getAllArrayImage();
            mAllImageCatName = ((Master) getActivity()).getAllArrayImageCatName();
            TOTAL_IMAGE = mAllImages.length - 1;

            viewpager = (ViewPager) getView().findViewById(R.id.image_slider);
            imageLoader = new ImageLoader(getActivity());
            handler = new Handler();

            /**
             * ViewPager
             */
            ImagePagerAdapter adapter = new ImagePagerAdapter();
            viewpager.setAdapter(adapter);
            viewpager.setCurrentItem(position);

            viewpager.setOnPageChangeListener(new OnPageChangeListener() {
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
                public void onPageScrollStateChanged(int position) {
                }
            });
        }
    }
}
