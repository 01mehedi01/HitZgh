package com.app.hitxghbeta;

import android.animation.Animator;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.app.hitxghbeta.others.SampleSuggestionsBuilder;
import com.app.hitxghbeta.others.SimpleAnimationListener;

import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Anubhav on 11-08-2017.
 */

public class BaseActivity extends AppCompatActivity {

    public static final String SHOW_DIALOG = "showDialog";
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_MSG = "dialogMsg";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private PersistentSearchView searchView;
    private View searchTintView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;
    private SharedPreferences sharedPref;
    public FirebaseAuth auth;

    private int appVersion;
    private boolean isActive;
    private boolean isForceUpdate;
    private boolean showDialog;
    private String dialogTitle;
    private String dialogMessage;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private InterstitialAd interstitial;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {


            showInterstitial();
            loadInterstitialAd(false);
            loadAdsInterstitialPeriodically();
        }
    };

    private void loadAdsInterstitialPeriodically() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000 * Config.interstitialAdInterval);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        /*AppCompatDelegate
                .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);*/
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseApp.initializeApp(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() == null&&Config.enableSignupSignin){
            Intent i = new Intent(this, AuthUiActivity.class);
            startActivity(i);
            finish();
        }

        sharedPref = getSharedPreferences(Config.DEF_SHAREF_PREF,Context.MODE_PRIVATE);
        if(Config.showFullScreenAds) {
            loadInterstitialAd(false);
            loadAdsInterstitialPeriodically();
        }

        if(getIntent()!=null){
            showDialog = getIntent().getBooleanExtra(SHOW_DIALOG,false);
            dialogTitle = getIntent().getStringExtra(DIALOG_TITLE);
            dialogMessage = getIntent().getStringExtra(DIALOG_MSG);
        }
        if(showDialog){
            showDialog(dialogTitle,dialogMessage);
        }

        appVersion = sharedPref.getInt(Config.APP_VERSION,0);
        isForceUpdate = sharedPref.getBoolean(Config.IS_FORCE_UPDATE,false);
        String updateTitle,updateBody;
        updateTitle = sharedPref.getString("update_title","");
        updateBody = sharedPref.getString("update_body","");
        if(appVersion>Config.currentAppVersion&&Config.showDialog){
            showUpdateDialog(updateTitle,updateBody);
        }
    }

    public void showFBInterstitialAd(){
        fbInterstitialAd = new com.facebook.ads.InterstitialAd(this,getResources().getString(R.string.fb_ads_interstitial_placement_id));
        fbInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(getApplicationContext(), "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                fbInterstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        fbInterstitialAd.loadAd();

    }

    public void openLoginScreen(View v){
        //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
        if (auth.getCurrentUser()==null) {
            Intent intent = new Intent(getApplicationContext(), AuthUiActivity.class);
            startActivity(intent);
            finish();
        }else{
            auth.signOut();
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
        }
        super.onDestroy();
    }

    private void showDialog(String title,String message){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    private void showUpdateDialog(String title,String message){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(getString(R.string.update_now_btn_string), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(isActive&&isForceUpdate)
                            finish();
                    }
                })
                .setNegativeButton(getString(R.string.update_later_string), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        if(isActive&&isForceUpdate)
                            finish();
                    }
                });
        builder.show();
        Config.showDialog = false;
    }

    public void loadInterstitialAd(final boolean b){
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ads_new_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //showInterstitial();
                if (b){
                    mInterstitialAd.show();
                }//else if(mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(BaseActivity.this, "failadd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded() && isActive) {
            mInterstitialAd.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //menu.findItem(R.id.exitmain).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.exitmain){
           return true;
        }
        else if(id == R.id.action_search){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSearchView(PersistentSearchView mSearchView, final View mSearchTintView){
        this.searchView = mSearchView;
        this.searchTintView = mSearchTintView;
        mSearchTintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.closeSearch();
            }
        });

        mSearchView.setSuggestionBuilder(new SampleSuggestionsBuilder(this));
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {

            }

            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen

            }

            @Override
            public void onSearchEditClosed() {

            }

            @Override
            public boolean onSearchEditBackPressed() {
                return false;
            }

            @Override
            public void onSearchExit() {

            }
        });
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if(delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }
    }

    public void closeSearchEdit(final View view){
            view
                .animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new SimpleAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public void openSearchEdit(View view){
        view.setVisibility(View.VISIBLE);
        view
                .animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new SimpleAnimationListener())
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchView.populateEditText(matches);  // Set result to PersistentSearchView
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void showSnackBar(View v,String s){
        Snackbar.make(v,s, Snackbar.LENGTH_LONG).show();
    }

    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commitAllowingStateLoss();
    }

    public void openWebView(String url){
        Intent intent = new Intent(getApplicationContext(),WebViewActivity.class);
        intent.putExtra("URL",url);
        startActivity(intent);
    }

    protected void openWebUrl(String url){

        // CustomTabsIntent.Builder used to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder().setShowTitle(true);

        // Change the background color of the Toolbar.
        builder.setToolbarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));

        // Hide status bar once the user scrolls down the content
        builder.enableUrlBarHiding();

        // For adding menu item
        builder.addMenuItem("Share", getItem(url));

        // For adding Action button
        builder.setActionButton(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_menu_share), "Share", getItem(url), true);

        // CustomTabsIntent used to launch the URL
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        //Open the Custom Tab
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private PendingIntent getItem(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Droidmentor is a site, which contains android tutorials");
        Log.d("PendingIntent", "setMenuItem: " + url);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        return PendingIntent.getActivity(this, 0, shareIntent, 0);
    }

    protected void openFbUrl(String username){
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(username);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    protected void openCarouselCategoryPage(String title, int id){
        Intent intent = new Intent(getApplicationContext(), CarouselPostListActivity.class);
        intent.putExtra(PostListActivity.ARG_CATEGORY, id + "");
        intent.putExtra(PostListActivity.ARG_TYPE, 1);
        intent.putExtra(PostListActivity.ARG_TITLE, title);
        intent.putExtra(PostListActivity.ARG_SHOW_IMG, true);
        startActivity(intent);
    }

    protected void openSimpleCategoryPage(String title,int id){
        Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
        intent.putExtra(PostListActivity.ARG_TITLE,title);
        intent.putExtra(PostListActivity.ARG_CATEGORY,id+"");
        startActivity(intent);
    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(String username) {
        String FACEBOOK_URL = "https://www.facebook.com/"+username;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + username;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    protected void openTwitterUrl(String username){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name="+username));
            startActivity(intent);
            Toast.makeText(this, "TwitterBase"+username, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "TwitteException"+username, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/"+username)));
        }
    }

    protected void openYoutubeUrl(String youtubeUrl){
        Intent intent=null;
        try {
            intent =new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(youtubeUrl));
            startActivity(intent);
          //  Toast.makeText(this, "Base Activity", Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(youtubeUrl));
            startActivity(intent);
        }
    }

    protected void openInstagram(String username){
        Uri uri = Uri.parse("http://instagram.com/_u/"+username);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/"+username)));
        }
    }

    protected void openWebUrlInChrome(String url){
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
