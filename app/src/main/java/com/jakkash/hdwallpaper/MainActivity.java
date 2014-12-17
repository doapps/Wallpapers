package com.jakkash.hdwallpaper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.dialogs.Dialog_Rate;
import com.example.util.UtilFonts;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.jakkash.hdwallpaper.R.string;
import com.google.android.gms.ads.AdRequest;
import com.me.doapps.v2.Master;

import java.io.File;

public class MainActivity extends Master implements ActionBar.TabListener {

    private String[] tabs = {"ULTIMOS", "CATEGORÍAS", "FAVORITOS"};
    private TabsPagerAdapter mAdapter;
    private ViewPager viewPager;
    ActionBar.Tab tab;
    private InterstitialAd interstitial;
    AdRequest adRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);




        //tab added in action bar
        for (String tab_name : tabs) {
            tab = getSupportActionBar().newTab();

            tab.setText(tab_name);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequestb = new AdRequest.Builder().build();
        adView.loadAd(adRequestb);


        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(string.admob_interstitial));
        adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);


        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                interstitial.show();
            }
        }, 60000);*/

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent about = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(about);
                return true;
            case R.id.menu_overflow:
                return true;
            case R.id.menu_moreapp:

                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/search?q=jakkash+application")));

                return true;

            case R.id.menu_rateapp:

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //you may open Interstitial Ads here
            interstitial.loadAd(adRequest);

            /*Dialog Rate*/
            if (existDir("/hd_wallpaper")) {
                Log.e("RATE_APP", "Ya está calificada");
                finish();
            } else {
                Dialog_Rate dialog_rate = new Dialog_Rate(MainActivity.this);
                dialog_rate.show();

                dialog_rate.setInterface_rate(new Dialog_Rate.Interface_Rate() {
                    @Override
                    public void getRate(boolean status, int option) {
                        if (status) {
                            if (option == 1) {
                                finish();
                            }
                            if (option == 2) {
                                createDir("/hd_wallpaper");
                                final String appName = "com.jakkash.apksaver";
                                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://play.google.com/store/apps/details?id=" + appName));
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public InterstitialAd getInterstitial() {
        return interstitial;
    }



    public boolean existDir(String path){
        boolean tmp = false;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if(file.exists()){
            tmp = true;
        }
        return tmp;
    }

    public boolean createDir(String path){
        boolean tmp_ = true;
        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                tmp_ = false;
            }
        }
        return tmp_;
    }

}
