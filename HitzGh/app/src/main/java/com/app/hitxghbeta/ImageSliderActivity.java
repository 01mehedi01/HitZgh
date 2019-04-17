package com.app.hitxghbeta;

import android.Manifest;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class ImageSliderActivity extends AppCompatActivity {
    CarouselView carouselView;
    private ViewPager mPager;
    String filename;
    String color;
    private DownloadManager downloadManager;
    boolean fullSizeImage,downloadfullsize;
    int length;
    int scrollto;
    String img;
    List<String> images = new ArrayList<>();
    PhotoViewAttacher mAttacher;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private String TAG = ImageSliderActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        Toolbar toolbar = findViewById(R.id.slider_toolbar);
        setSupportActionBar(toolbar);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        length = getIntent().getIntExtra("length", 0);
        scrollto = getIntent().getIntExtra("scrollto",0);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        fullSizeImage = prefs.getBoolean("full_size_image",false);
        downloadfullsize = prefs.getBoolean("download_full_size",false);
        Log.e("Settings","FSI:"+fullSizeImage+" DFG: "+downloadfullsize);
        for (int i = 0; i < length; i++) {
            images.add(getIntent().getStringExtra("image" + i));
        }
        changeTitle(images.get(0));
        mPager = (ViewPager) findViewById(R.id.pager);
        init();

    }

    private void changeTitle(String img){
        if(img.contains("?")) {
            filename = img.substring(img.lastIndexOf("/") + 1, img.indexOf("?"));
        }else{
            filename = img.substring(img.lastIndexOf("/")+1,img.length());
        }
        getSupportActionBar().setTitle(filename);
    }

    private static final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_SET_WALLPAPER = 113;
    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {
                saveImage();
            }
        } else {
           saveImage();
        }
    }

    private void init() {
        for(int i=0;i<images.size();i++)
            mPager.setAdapter(new ImgVPSlider(this,images,fullSizeImage));
            CirclePageIndicator indicator = findViewById(R.id.indicator);
            indicator.setViewPager(mPager);
            final float density = getResources().getDisplayMetrics().density;
            //Set circle indicator radius
            indicator.setRadius(5 * density);
            NUM_PAGES = images.size();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPager.setCurrentItem(scrollto, true);
                }
            }, 50);
            // Pager listener over indicator
            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                changeTitle(images.get(position));
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_download) {
            Toast.makeText(getApplicationContext(),R.string.image_download_msg,Toast.LENGTH_LONG).show();
            askPermission();
            return true;
        }
        else if(id ==R.id.exitmain){
            finish();
            moveTaskToBack(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage(){
        String URL,oriImg;
        Uri uri,oriUri;
        int currentItem = mPager.getCurrentItem();
        URL = images.get(currentItem);
        if(URL.contains("?"))
            oriImg = URL.substring(0,URL.indexOf("?"));
        else
            oriImg = URL;
        uri = Uri.parse(URL);
        oriUri = Uri.parse(oriImg);
        if(downloadfullsize)
            DownloadData(oriUri);
        else
            DownloadData(uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_download).setVisible(true);
        return true;
    }

    private long DownloadData (Uri uri) {
        Log.e("URL",uri.toString());
        Log.e("FileName",filename);
        long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(filename);

        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir("/"+getString(R.string.app_name),filename);

        //Environment.DIRECTORY_DOWNLOADS
        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
