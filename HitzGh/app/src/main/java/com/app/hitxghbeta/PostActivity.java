package com.app.hitxghbeta;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.app.hitxghbeta.fragments.HorizontalPostsFragment;
import com.app.hitxghbeta.webview.CustomWebViewClient;
import com.app.hitxghbeta.webview.VideoEnabledWebChromeClient;
import com.app.hitxghbeta.webview.VideoEnabledWebView;
import com.app.hitxghbeta.webview.WebAppInterface;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetPage;
import com.app.wplib.GetPost;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.page.Page;
import com.app.wplib.models.post.Post;
import com.mikepenz.materialize.MaterializeBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class PostActivity extends BaseActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "PostActivity";
    public static String POST_ID_STRING = "post_id";
    public static String POST_SLUG = "post_slug";
    public static String POST_PASSWORD_SRING = "password";
    public static String POST_URL_STRING = "post_url";
    public static String LOAD_PAGE = "load_page";
    public static String IS_OFFLINE_POST = "isOfflinePost";

    private boolean isActive;
    private boolean loadPage;
    private boolean isOfflinePost;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private String slug;
    private String url;
    private TextView titleView,dateView,relatedPostTitleView;
    private LinearLayout shareCommentView;
    private ImageView imageView;
    private VideoEnabledWebView postWebview;
    private VideoEnabledWebChromeClient webChromeClient;
    private ProgressBar progressBar;
    private Post thisPost;
    private Page thisPage;
    private int postId;
    private String allCategories;
    private String password;
    private RewardedVideoAd mRewardedVideoAd;
    private ExecutorService executorService;
    private HashMap<String, Future<?>> queue = new HashMap<String, Future<?>>();
    private DatabaseHandler databaseHandler;
    private AdView mAdView;
    private TextToSpeech tts;
    private String videoId;
    private com.facebook.ads.AdView fbAds;
    private List<String> images = new ArrayList<>();
    private SwipeRefreshLayout swipe;
    private Toolbar toolbar;
    private AdView topAds;
    private AdView bottomads,relatedtopads,relatedbottomads;
    private LinearLayout Carrlayout;
    private boolean SwipeValue = false;
    private static SharedPreferences pref;
    private  SharedPreferences.Editor editor;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        toolbar = findViewById(R.id.postToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PostActivity.super.onBackPressed();

                Intent intent = new Intent(getApplicationContext(), CarouselPostListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        new MaterializeBuilder()
                .withActivity(this)
                .build();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }
        initilizeAds();
        databaseHandler = new DatabaseHandler(this);


        swipe = findViewById(R.id.swipwrefress);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                initilizeAds();
//
//

//                postWebview.setVisibility(View.GONE);
//                dateView.setText("");
//                titleView.setText("");
//                imageView.setImageResource(0);
//                        topAds.setVisibility(View.GONE);
//                        bottomads.setVisibility(View.GONE);
//                        relatedtopads.setVisibility(View.GONE);
//                        relatedbottomads.setVisibility(View.GONE);
//                SwipeValue = true;
//
//
//                swipe.setRefreshing(true);
//                loadweb();




//
//                loadweb();
                swipe.setRefreshing(false);
                startActivity(new Intent(getApplicationContext(),PostActivity.class));
            }
        });
        loadweb();

    }



    private void loadweb(){


        //initilizeAds();

//      if(SwipeValue){
//          postWebview.setVisibility(View.VISIBLE);
//          topAds.setVisibility(View.VISIBLE);
//          bottomads.setVisibility(View.VISIBLE);
//          relatedtopads.setVisibility(View.VISIBLE);
//          relatedbottomads.setVisibility(View.VISIBLE);
//          SwipeValue = false;
//      }

        Carrlayout = findViewById(R.id.carrylayout);
        tts = new TextToSpeech(this,this);
        mAdView = findViewById(R.id.postAdView);
        progressBar = findViewById(R.id.pd_progressBar);
        shareCommentView = findViewById(R.id.shareCommentsBar);
        titleView = findViewById(R.id.postTitle);
        dateView = findViewById(R.id.postDate);
        imageView = findViewById(R.id.postImg);
        postWebview = findViewById(R.id.postWebview);
        postWebview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        postWebview.setLongClickable(false);
        postWebview.setHapticFeedbackEnabled(false);
        relatedPostTitleView= findViewById(R.id.relatedPostsTitle);
        //WebView Settings


        WebSettings webSettings = postWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSaveFormData(true);
        //Get Extras
        if(getIntent()!=null){
            //Toast.makeText(this, "IN in in in  in in ", Toast.LENGTH_SHORT).show();
            //swipe.setRefreshing(false);
            postId = getIntent().getIntExtra(POST_ID_STRING,0);
            password = getIntent().getStringExtra(POST_PASSWORD_SRING);
            url = getIntent().getStringExtra(POST_URL_STRING);
           // Toast.makeText(this, "URLCool "+url, Toast.LENGTH_SHORT).show();
            slug = getIntent().getStringExtra(POST_SLUG);
            loadPage = getIntent().getBooleanExtra(LOAD_PAGE,false);
            isOfflinePost = getIntent().getBooleanExtra(IS_OFFLINE_POST,false);
            //Toast.makeText(this, "Full", Toast.LENGTH_SHORT).show();


//            editor.clear();
//            editor.commit();

            ////                 save value /////////////////////////////

///                                         update by mehedi    ///////////////////////////////////////////////////
            if(postId!=0) {
               // Toast.makeText(this, "In shard prefaeance", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.commit();
                editor.putInt("postId", postId);
                editor.putString("password", password);
                editor.putString("url", url);
                editor.putString("slug", slug);
                editor.putBoolean("loadPage", loadPage);
                editor.putBoolean("isOfflinePost", isOfflinePost);
                editor.commit();
            }

            if(pref!=null) {
                loadPage = pref.getBoolean("loadPage", true);
                isOfflinePost = pref.getBoolean("isOfflinePost", true);
                postId = pref.getInt("postId", 0);
                password = pref.getString("password", null);
                url = pref.getString("url", null);
                slug = pref.getString("slug", null);
               // Toast.makeText(PostActivity.this, "Create "+pref.getInt("postId", 0), Toast.LENGTH_SHORT).show();
            }

//            Toast.makeText(this, "Save Successfull"+postId, Toast.LENGTH_SHORT).show();


///                                         update by mehedi    ///////////////////////////////////////////////////
        }
        if (isOfflinePost){
            setupOfflinePost();

        }else {
            swipe.setRefreshing(false);
            fetchPost();
        }
//        if(Config.showBannerAds) {
//            loadAds()
//        }
        //loadInterstitialAd(true);
        if (Config.showFBBannerAds){
            loadFBBannerAd();
        }else {
            loadAds();
        }
        //titleView.setText("Aftar Swipe \n\npostId"+postId+"\npassword"+password+"\nurl"+url+"\nslug"+slug+"\nloadPage"+loadPage+"\nisOfflinePost"+isOfflinePost);
      //  titleView.setText("postId"+postId+"\npassword"+password+"\nurl"+url+"\nslug"+slug+"\nloadPage"+loadPage+"\nisOfflinePost"+isOfflinePost);
    }








    private void loadFBBannerAd(){
        // Instantiate an AdView view
        fbAds = new com.facebook.ads.AdView(this, getResources().getString(R.string.fb_ads_banner_placement_id), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(fbAds);

        // Request an ad
        fbAds.loadAd();
    }

    private void setupYouTubePlayer(final String videoId){
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeFragment);
        imageView.setVisibility(View.GONE);
        youTubePlayerFragment.initialize(getResources().getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                PostActivity.this.youTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoId);
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
    }

    private void setupOfflinePost(){
        thisPost = databaseHandler.getPost(postId);
        if(thisPost!=null){
            titleView.setText(thisPost.getTitle().getRendered());
            dateView.setText(thisPost.getDate());
            path = thisPost.getBetterFeaturedImage().getPostThumbnail();
            Glide.with(getApplicationContext())
                    .load(thisPost.getBetterFeaturedImage().getPostThumbnail())
                    .placeholder(R.color.md_green_100)
                    .into(imageView);
            proceed();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        postWebview.onResume();
        isActive = true;
    }

    private void fetchPost(){

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        if(loadPage){
            GetPage getPage = new GetPage(apiInterface,getApplicationContext());
            getPage.setId(postId);
            getPage.setOnComepleteListner(new GetPage.onComepleteListner() {
                @Override
                public void onSuccess(Page page) {
                    thisPage = page;
                    url = thisPage.getLink();
                    allCategories = null;
                    proceed(page);
                }

                @Override
                public void onError(String msg) {
                    Toasty.error(getApplicationContext(),getResources().getString(R.string.error_loading_post),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            getPage.execute();
        }else{
            GetPost getPost = new GetPost(apiInterface,getApplicationContext());
            getPost.setPostId(postId);
            getPost.setSlug(slug);
            getPost.setPassword(password);
            getPost.setListner(new GetPost.Listner() {
                @Override
                public void onSuccess(Post post) {
                    thisPost = post;
                    post=null;
                    url = thisPost.getLink();
                    allCategories = thisPost.getCategories().toString();
                    Log.e("Categories","Cats: "+allCategories);
                    allCategories = allCategories.substring(1,allCategories.length()-1);
                    proceed();
                }

                @Override
                public void onError(String msg) {
                    Toasty.error(getApplicationContext(),getResources().getString(R.string.error_loading_post),Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            getPost.execute();
        }
    }

    private void proceed(Page page){
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, "proceed: Post Returned");
        titleView.setText(page.getTitle().getRendered());
        String metaString = "Posted by "+page.getAuthorName()+" on "+page.getDate().substring(0,page.getDate().indexOf("T"));
        dateView.setText(metaString);
        if(page.getBetterFeaturedImage()!=null)
            path = page.getBetterFeaturedImage().getPostThumbnail();
        else
            path = null;
        Glide.with(getApplicationContext())
                .load(path)
                .placeholder(R.color.md_green_100)
                .into(imageView);
        String content = "<link rel=\"stylesheet\" href=\"defaultstyles.css\" /><link rel=\"stylesheet\" href=\"styles.css\" />"+thisPage.getContent().getRendered()+"<script>function siteUrlClicked(url){Android.siteUrlClicked(url);}</script>";
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("a[href^=\""+Config.siteUrl+"\"]");
        for(Element e:elements){
            e.attr("onclick","siteUrlClicked('"+e.attr("href")+"')");
            e.attr("href","#");
            Log.e("Tag","Tag: "+e.html());
        }
        writewebview(doc.html());
    }

    private void proceed(){
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, "proceed: Post Returned");
        titleView.setText(thisPost.getTitle().getRendered());
        String metaString = "Posted by "+thisPost.getAuthorName()+" on "+thisPost.getDate().substring(0,thisPost.getDate().indexOf("T"));
        dateView.setText(metaString);
        if(thisPost.getBetterFeaturedImage()!=null)
            path = thisPost.getBetterFeaturedImage().getPostThumbnail();
        else
            path = null;
        Glide.with(getApplicationContext())
                .load(path)
                .placeholder(R.color.md_green_100)
                .error(R.drawable.no_image_placeholder)
                .into(imageView);
        String additionalCss = "";
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            additionalCss = "*{color:#FFFFFF!important}";
        }
        String content = "<link rel=\"stylesheet\" href=\"defaultstyles.css\" /><link rel=\"stylesheet\" href=\"styles.css\" /><style>"+additionalCss+"</style>"+thisPost.getContent().getRendered()+"<script src=\"script.js\"></script>";
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("a[href^=\""+Config.siteUrl+"\"]");
        Elements imgs = doc.select("img");
        int x=0;
        for (Element e:imgs){
            images.add(e.attr("src"));
            e.attr("onclick","imageClicked('https:"+e.attr("src")+"',"+x+")");
            x++;
        }
        for(Element e:elements){
            e.attr("onclick","siteUrlClicked('"+e.attr("href")+"')");
            e.attr("href","#");
            Log.e("Tag","Tag: "+e.html());
        }
        Elements images = doc.select("img[srcset]");
        for (Element img: images){
            img.removeAttr("srcset");
            Log.e("Tag","Tag; "+img.toString());
        }
        Elements iframes = doc.select("iframe");
        if(iframes.size()>0) {
            Log.e("Iframe","Size: "+iframes.size());
            String tag = iframes.first().toString();
            String url0 = tag.substring(tag.indexOf("src=\"") + 5, tag.length());
            videoId = url0.substring(0, url0.indexOf("\" "));
            getYouTubeId(videoId);
        }

        writewebview(doc.html());
        doc = null;
        /*if(linkMode){
            new MyTask().execute(url);
        }else {
            writewebview(doc.html());
        }*/
    }

    @Override
    protected void onDestroy() {
        if (fbAds != null) {
            fbAds.destroy();
        }
        super.onDestroy();
    }

    private void getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            //initializeYoutubePlayer(matcher.group());
            setupYouTubePlayer(matcher.group());
        }
    }

    private void writewebview(String html){
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        final ViewGroup videoLayout = findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all

        //Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show();
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);

        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, postWebview) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                    videoLayout.setVisibility(View.VISIBLE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    videoLayout.setVisibility(View.GONE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }
        });
        postWebview.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        postWebview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        postWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        postWebview.getSettings().setDomStorageEnabled(true);
        postWebview.setWebChromeClient(webChromeClient);
        postWebview.setWebViewClient(new CustomWebViewClient(getApplicationContext(),images));
        postWebview.addJavascriptInterface(new WebAppInterface(getApplicationContext(),postWebview,images), "Android");
        postWebview.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        //progressBar.setVisibility(View.VISIBLE);
        if(isActive&&!loadPage) {
            relatedPostTitleView.setVisibility(View.VISIBLE);
            shareCommentView.setVisibility(View.VISIBLE);
            replaceFragment(R.id.relatedPosts, new HorizontalPostsFragment().newInstance(false, false, allCategories, null,thisPost.getId()+""), null, null);
        }
    }

    @JavascriptInterface
    public void resize(final float height) {
        Log.e(TAG, "resize: "+height );
        PostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postWebview.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.exitmain).setVisible(false);
        if(!loadPage) {

            menu.findItem(R.id.action_share).setVisible(true);
            menu.findItem(R.id.action_save).setVisible(true);
            menu.findItem(R.id.action_speak).setVisible(true);
        }
        if(isOfflinePost){
            menu.findItem(R.id.action_save).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void openComments(View view){
        Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
        if(thisPost!=null) {
//            intent.putExtra(CommentActivity.ARG_POST, thisPost.getId());
//            if(thisPost.getCommentStatus()==null||thisPost.getCommentStatus().equals("open")){
//                intent.putExtra(CommentActivity.ARG_ALLOW_COMMENTS,true);
//            }else {
//                intent.putExtra(CommentActivity.ARG_ALLOW_COMMENTS,false);
//            }

            intent.putExtra("url",thisPost.getLink());


            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
//        intent.putExtra("fbCommenturll", "https://www.androidhive.info/2016/06/android-firebase-integrate-analytics/");
//        startActivity(intent);
    }

    public void initShare(View view){
        if(thisPost!=null){
            path = thisPost.getBetterFeaturedImage().getMediumLarge();
            choice=1;
            askPermission();
           // Toast.makeText(this, "path   "+path, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.action_comments){
            openComments(null);
        }else if(id == R.id.action_share){
            initShare(null);
        }else if(id == R.id.exitmain){
            finish();
            moveTaskToBack(true);
        }else if(id==R.id.action_save){
            databaseHandler.addOfflinePost(thisPost);
            Toasty.success(getApplicationContext(),getResources().getString(R.string.post_saved_successfully),Toast.LENGTH_SHORT).show();
        }else if(id==R.id.action_speak){
            if(!tts.isSpeaking()) {
                if (thisPost != null) {
                    speakOut(thisPost.getContent().getRendered());
                }
                else {
                    speakOut(getString(R.string.no_text_to_speak_msg));
                }
            }else {
                tts.stop();
            }
        }else if(id == R.id.action_delete){
            databaseHandler.deletePost(thisPost.getId());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void initilizeAds(){


        MobileAds.initialize(this, getString(R.string.admob_app_id));

        relatedbottomads = findViewById(R.id.postviewrelatedpostbottom_adView);
        relatedtopads = findViewById(R.id.postviewrelatedposttop_adView);
        topAds = findViewById(R.id.postTopT_adView);
        bottomads = findViewById(R.id.postviewbottom_adView);


        AdRequest adRequest = new AdRequest.Builder().build();

       // topAds.loadAd(adRequest);

       // AdRequest adRequest = new AdRequest.Builder().build();

        topAds.loadAd(adRequest);


        bottomads.loadAd(adRequest);


        relatedtopads.loadAd(adRequest);



        AdRequest adRequestt = new AdRequest.Builder().build();
        relatedbottomads.loadAd(adRequestt);

    }
    private void loadAds(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                   download
                    downloadimage(path, choice);

                } else {

                }
                break;

            default:
                break;
        }

    }


    String path;
    int choice = 0;
    private static final int REQUEST_WRITE_STORAGE = 112;

    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {
                // GetVendorList();
                downloadimage(path, choice);
               // Toast.makeText(this, "Inner "+path+"  choice "+choice, Toast.LENGTH_SHORT).show();
                //imgDownload(path);
            }
        } else {
            //imgDownload(path);
            //GetVendorList();
          //  Toast.makeText(this, "outer "+path+"  choice "+choice, Toast.LENGTH_SHORT).show();
            downloadimage(path, choice);
        }
    }


    public void downloadimage(String url, int flag) {
        Toasty.info(getApplicationContext(),getResources().getString(R.string.wait_while_image_is_downloading),Toast.LENGTH_LONG).show();
        int notificationid = url.hashCode();
        String saveTopath = getPhotoPath(PostActivity.this);
        File storePath = new File(saveTopath, notificationid + ".jpeg");
        if (storePath.exists()) {

            onDownloadImageSuccess(storePath.getPath(), flag);
        } else {
            executorService = Executors.newFixedThreadPool(5);
            DownloadLoader dm = new DownloadLoader(url, storePath.getPath(),
                    flag, notificationid);
            Future<?> future = executorService.submit(dm);
            queue.put(url, future);
        }
    }

    public static String getPhotoPath(Context context) {


        File folder = new File(Environment.getExternalStorageDirectory(),
                context.getResources().getString(R.string.app_name));

        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder.getPath();
    }



    private class DownloadLoader implements Runnable {
        private String urlPath;
        private String storePath;
        private FileOutputStream fileOutput = null;
        private HttpURLConnection urlConnection = null;
        private InputStream inputStream = null;
        private boolean isComplete;
        private File file;
        int flag = 0;


        public DownloadLoader(String path, String storePath, int flag,
                              int notificationid) {

            this.flag = flag;
            this.urlPath = path;
            this.storePath = storePath;
            //Toast.makeText(PostActivity.this, "Download", Toast.LENGTH_SHORT).show();
        }

        public void run() {
            try {
                file = new File(storePath);
                URL url = new URL(urlPath);
                urlConnection = (HttpURLConnection) url.openConnection();
                file.createNewFile();
                urlConnection.connect();
                fileOutput = new FileOutputStream(file);
                inputStream = urlConnection.getInputStream();
                long totalSize = urlConnection.getContentLength();
                Toast.makeText(PostActivity.this, "run", Toast.LENGTH_SHORT).show();
                int downloadedSize = 0;
                byte[] buffer = new byte[50 * 1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    final int prog = (int) ((downloadedSize * 100) / totalSize);
                }
                if (totalSize == file.length()) {
                    isComplete = true;
                    onDownloadImageSuccess(file.getPath(), flag);
                } else {
                    isComplete = false;
                    onDownloadImageFailed();
                }

            } catch (Exception e) {
                e.printStackTrace();
                isComplete = false;
                onDownloadImageFailed();
            } finally {
                try {
                    if (fileOutput != null)
                        fileOutput.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }

    public void onDownloadImageSuccess(final String path, final int flag) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Uri imageUri = FileProvider.getUriForFile(PostActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(path));
                //Toast.makeText(PostActivity.this, "imageUri"+imageUri, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(imageUri);
                sendBroadcast(intent);

                if (flag == 1) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                        imageUri = FileProvider.getUriForFile(PostActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                new File(path));
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    share.putExtra(Intent.EXTRA_TEXT,thisPost.getTitle().getRendered()+"\n"+thisPost.getLink());
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, getResources().getString(R.string.share_via_text)));
                }

            }
        });

    }

    public void onDownloadImageFailed() {
        Toasty.error(getApplicationContext(),getResources().getString(R.string.image_downloading_failed),Toast.LENGTH_SHORT).show();
    }

    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Document doc;
            String returnHtml="";
            Log.e(TAG, "doInBackground: Doing in background with "+urls[0]);
            try {
                Document document = Jsoup.connect(urls[0]).userAgent("Mozilla").get();
                Log.e(TAG, "doInBackground: "+document.html());
                returnHtml = document.html();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return returnHtml;
        }

        @Override
        protected void onPostExecute(String result) {
            //if you had a ui element, you could display the title
            writewebview(result);
        }
    }

    private void speakOut(String content) {
        if(youTubePlayer==null||!youTubePlayer.isPlaying()) {
            String text = Jsoup.parse(content).text();
            if (text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(text);
                } else {
                    ttsUnder20(text);
                }
                Toasty.info(getApplicationContext(), getString(R.string.read_out_loud_text), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toasty.info(getApplicationContext(), getString(R.string.pause_youtube), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        postWebview.onPause();
        isActive=false;
        if (mAdView != null) {
            mAdView.pause();
        }
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(postWebview, (Object[]) null);
            if(tts.isSpeaking())
                tts.shutdown();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch(NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            Log.e("TTS", "Initilize success!");
            Locale locale = new Locale(Config.TTSLanguage);
            int result = tts.setLanguage(locale);
            tts.setPitch(0.7f);
            tts.setSpeechRate(1f);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                Toasty.error(getApplicationContext(),"Language not supported",Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


}
