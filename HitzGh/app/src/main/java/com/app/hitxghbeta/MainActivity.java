package com.app.hitxghbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.fragments.ContentBox1;
import com.app.hitxghbeta.others.CustomTypefaceSpan;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetCategories;
import com.app.wplib.GetRecentPost;
import com.app.wplib.GetSettings;
import com.app.wplib.models.Settings;
import com.app.wplib.models.category.Category;
import com.app.wplib.models.post.Post;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mikepenz.materialize.MaterializeBuilder;
import com.nex3z.flowlayout.FlowLayout;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.cryse.widget.persistentsearch.PersistentSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public static final String SHOW_DIALOG = "showDialog";
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String DIALOG_MSG = "dialogMsg";
    public boolean isActive;
    CarouselView carouselView;
    private InterstitialAd interstitial;
    private NavigationView navView;
    private PersistentSearchView mSearchView;
    private DrawerLayout drawer;
    private View mSearchTintView;
    private Toolbar toolbar;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Settings userSettings;
    private ApiInterface apiInterface;
    private AppBarLayout appBarLayout;

    private FlowLayout flowLayout;
    private View invisibleView;
    private boolean sliderEnabled;
    private List<Post> sliderPosts = new ArrayList<>();
    private List<Category> flowCategories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        appBarLayout = findViewById(R.id.mainAppBarLayout);
        mSearchView = findViewById(R.id.searchview);

        if(toolbar.getVisibility() == View.VISIBLE) {



            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            mSearchView.setVisibility(View.GONE);
        }

     //   MainLoadView();
      //  Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();
    }

    private void  MainLoadView(){

        new ProceessInBackground().execute();
        sharedPref = getSharedPreferences(Config.DEF_SHAREF_PREF,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        new MaterializeBuilder()
                .withActivity(this)
                .build();


        setupSearch();

        navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        View hView =  navView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.nav_header_title);
       // Button button = hView.findViewById(R.id.signInBtnClicked);
        CircleImageView user_img = hView.findViewById(R.id.nav_header_imageView);
        TextView nav_user_email = hView.findViewById(R.id.nav_header_email);
        if(auth!=null&&auth.getCurrentUser()!=null) {
            nav_user.setText(auth.getCurrentUser().getDisplayName());
            nav_user_email.setText(auth.getCurrentUser().getEmail());
            Glide.with(getApplicationContext())
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .dontAnimate()
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(user_img);
            //button.setText("Logout");
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            navView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
            toolbar.setBackground(null);
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }else {
            navView.setItemIconTintList(null);
        }

        //navView.setItemIconTintList(null);
        Menu m = navView.getMenu();
        if(Config.defaultHomeScreen==1) {
            m.findItem(R.id.nav_posts).setVisible(false);
            m.findItem(R.id.nav_tab_posts).setVisible(false);
        }
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        invisibleView = findViewById(R.id.invisibleView);
        carouselView = findViewById(R.id.carouselView);
        flowLayout = findViewById(R.id.flowSection);
        sendRequest();
        sliderEnabled = sharedPref.getBoolean(Config.IS_SLIDER_ENABLED,false);
        if(sliderEnabled){
            String cat = sharedPref.getString(Config.SLIDER_CATEGORY,null);
            loadSlider(cat);
        }else {
            carouselView.setVisibility(View.GONE);
            //invisibleView.setVisibility(View.VISIBLE);
        }

        boolean isSettingsSaved = sharedPref.getBoolean(Config.IS_SETTINGS_SAVED,false);
        if(isSettingsSaved) {
            int sectionCount = sharedPref.getInt(Config.NO_OF_SECTIONS, 0);
            for (int i = 0; i < sectionCount; i++) {
                int containerId;
                switch (i) {
                    case 0:
                        containerId = R.id.homeFrame1;
                        break;
                    case 1:
                        containerId = R.id.homeFrame2;
                        break;
                    case 2:
                        containerId = R.id.homeFrame3;
                        break;
                    case 3:
                        containerId = R.id.homeFrame4;
                        break;
                    case 4:
                        containerId = R.id.homeFrame5;
                        break;
                    case 5:
                        containerId = R.id.homeFrame6;
                        break;
                    case 6:
                        containerId = R.id.homeFrame6;
                        break;
                    case 7:
                        containerId = R.id.homeFrame6;
                        break;
                    case 8:
                        containerId = R.id.homeFrame6;
                        break;
                    case 9:
                        containerId = R.id.homeFrame6;
                        break;
                    default:
                        containerId = R.id.homeFrame1;
                        break;
                }
                String title, category, type, image, count;
                int countInt;
                title = sharedPref.getString(Config.SECTION_PREFIX + i + Config.SECTION_TITLE, "Undefined");
                category = sharedPref.getString(Config.SECTION_PREFIX + i + Config.SECTION_CATEGORY, null);
                image = sharedPref.getString(Config.SECTION_PREFIX + i + Config.SECTION_SMALL_IMAGE_URL, Config.defaultMediaUrl);
                type = sharedPref.getString(Config.SECTION_PREFIX + i + Config.SECTION_TYPE, "1");
                count = sharedPref.getString(Config.SECTION_PREFIX+i+Config.SECTION_COUNT,"3");
                countInt = Integer.parseInt(count);
                if(category!=null) {
                    if (category.equals(""))
                        category = null;
                    replaceFragment(containerId, new ContentBox1().newInstance(
                            title,
                            image,
                            category,
                            countInt,
                            Integer.parseInt(type)
                    ), "FRAG_TAG" + i, null);
                }
            }
        }else {
            Toasty.info(getApplicationContext(),getResources().getString(R.string.error_fetching_settings),Toast.LENGTH_SHORT).show();
        }
        //Manual Actions Here
        if(!Config.isPluginInstalled) {
            replaceFragment(R.id.homeFrame1, new ContentBox1().newInstance("Beauty",
                    "http://dev.app.com/wp-content/uploads/2018/01/make-up.png", "4", 5, 1), "KILL", null);
            replaceFragment(R.id.homeFrame2, new ContentBox1().newInstance("Culture",
                    "http://dev.app.com/wp-content/uploads/2018/01/parrot.png", "5", 3, 2), "KILL1", null);
            replaceFragment(R.id.homeFrame4, new ContentBox1().newInstance("Dining",
                    "http://dev.app.com/wp-content/uploads/2018/01/006-diet.png", "6", 5, 4), "KILL3", null);
            replaceFragment(R.id.homeFrame3, new ContentBox1().newInstance("Fashion",
                    "http://dev.app.com/wp-content/uploads/2018/01/005-dress.png", "11", 6, 3), "KILL2", null);
            replaceFragment(R.id.homeFrame5, new ContentBox1().newInstance("Fitness",
                    "http://dev.app.com/wp-content/uploads/2018/01/001-football.png", "7", 6, 5), "KILL4", null);
        }
        if(Config.showLatestPostsInSliderWhenNoCategorySelected)
            loadSlider(null);
    }









    private void loadSlider(String category){
        GetRecentPost getRecentPost = new GetRecentPost(apiInterface,getApplicationContext());
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(postList.size()>0&&isActive) {
                    if(postList.size()>0) {


                        sliderPosts.clear();
                        sliderPosts.addAll(postList);
                        carouselView.setViewListener(viewListener);
                        carouselView.setPageCount(sliderPosts.size());
                        invisibleView.setVisibility(View.GONE);
                        carouselView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if(isActive) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.loading_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });
        getRecentPost.setCategory(category);
        getRecentPost.setPerPage(5);
        getRecentPost.setCategoryEnabled(true);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setPage(1);
        getRecentPost.execute();
    }

    private void sendRequest(){
        if(Config.isPluginInstalled) {
            GetSettings getSettings = new GetSettings(apiInterface, getApplicationContext());
            getSettings.setListner(new GetSettings.onComplete() {
                @Override
                public void onSuccess(Settings settings) {
                    if (settings.getCategories() != null && settings.getCategories().length > 0) {
                        for (int i = 0; i < settings.getCategories().length; i++) {
                            String id,title;
                            id = settings.getCategories()[i].substring(0,settings.getCategories()[i].indexOf(" "));
                            title = settings.getCategories()[i].substring(settings.getCategories()[i].indexOf(" "),settings.getCategories()[i].length());
                            Category category = new Category();
                            category.setId(Integer.parseInt(id));
                            category.setName(title);
                            flowCategories.add(category);
                        }
                        flowCategory();
                    } else {
                        //fetchLatestCategories(apiInterface);
                        if (!sliderEnabled) {
                            //invisibleView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onError(String msg) {
                    //fetchLatestCategories(apiInterface);
                    if (!sliderEnabled) {
                        //invisibleView.setVisibility(View.VISIBLE);
                    }
                }
            });
            getSettings.execute();
        }else{
            if(Config.showLatestCategoryWhenNothingSelected)
                fetchLatestCategories(apiInterface);
        }
    }

    private void fetchLatestCategories(ApiInterface apiInterface){
        GetCategories getCategories = new GetCategories(apiInterface,getApplicationContext());
        getCategories.setPage(1);
        getCategories.setListner(new GetCategories.Listner() {
            @Override
            public void onSuccessful(List<Category> list, int totalPages) {
                flowCategories.addAll(list);
                flowCategory();

            }

            @Override
            public void onError(String msg) {

            }
        });
        getCategories.execute();
    }

    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            final int i=position;
            View view = getLayoutInflater().inflate(R.layout.slider_item,null);
            ImageView sliderImage = view.findViewById(R.id.sliderImage);
            TextView sliderCategory = view.findViewById(R.id.sliderCategory);
            TextView sliderTitle = view.findViewById(R.id.sliderTitle);

            String title,category="",img="";
            title = sliderPosts.get(position).getTitle().getRendered();
            if(Config.isPluginInstalled){
                if(sliderPosts.get(position).getCategoryDetails()!=null)
                    category = sliderPosts.get(position).getCategoryDetails().get(0).getName();
                if(sliderPosts.get(position).getBetterFeaturedImage()!=null)
                    img = sliderPosts.get(position).getBetterFeaturedImage().getPostThumbnail();
            }else {
                category = sliderPosts.get(position).getCategories_string();
                img = sliderPosts.get(position).getFeat_media_url();
            }

            sliderTitle.setText(title);
            sliderCategory.setText(category);
            Glide.with(getApplicationContext())
                    .load(img)
                    .placeholder(R.color.md_green_100)
                    .error(R.drawable.no_image_placeholder)
                    .into(sliderImage);

            sliderCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CarouselPostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_TITLE,sliderPosts.get(i).getCategoryDetails().get(0).getName());
                    intent.putExtra(PostListActivity.ARG_CATEGORY,sliderPosts.get(i).getCategoryDetails().get(0).getId()+"");
                    startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),PostActivity.class);
                    intent.putExtra(PostActivity.POST_ID_STRING,sliderPosts.get(i).getId());
                    intent.putExtra(PostActivity.POST_URL_STRING,sliderPosts.get(i).getLink());
                    intent.putExtra(PostActivity.POST_PASSWORD_SRING,sliderPosts.get(i).getPassword());
                    startActivity(intent);
                    finish();
                }
            });

            return view;
        }
    };

    private void flowCategory(){
        for (int i=0;i<flowCategories.size();i++){
            final int position = i;
            View v = getLayoutInflater().inflate(R.layout.category_btn_rect,null);
            CardView cardView = v.findViewById(R.id.cat_card);
            TextView textView = v.findViewById(R.id.cat_title);

            Colors r = new Colors();
            cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(),r.getRandomColor()));
            if(flowCategories.get(position).getCmb2()!=null){
                String color = flowCategories.get(position).getCmb2().getWordroidFields().getCategoryColor();
                if(color!=null&&!color.isEmpty()&&!color.equals("#ffffff"))
                    cardView.setCardBackgroundColor(Color.parseColor(color));
            }
            textView.setText(flowCategories.get(position).getName());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), CarouselPostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_TITLE,flowCategories.get(position).getName());
                    intent.putExtra(PostListActivity.ARG_CATEGORY,flowCategories.get(position).getId()+"");
                    startActivity(intent);
                }
            });
            flowLayout.addView(v);
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mSearchView.isEditing()){
                mSearchView.cancelEditing();
                closeSearchEdit(mSearchTintView);
            }else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_search).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.exitmain:
                 finish();
                 moveTaskToBack(true);
                break;
            case R.id.action_search:
                mSearchView.openSearch();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_posts){
            Intent intent = new Intent(MainActivity.this,CarouselPostListActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_pages) {
            Intent intent = new Intent(MainActivity.this,PostListActivity.class);
            intent.putExtra(PostListActivity.ARG_SHOW_PAGES_INSTEAD,true);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(MainActivity.this,CategoryActivity.class));
        }else if (id == R.id.nav_tab_posts) {
            startActivity(new Intent(MainActivity.this,ViewPagerActivity.class));
        }else if (id == R.id.nav_contacus){
            startActivity(new Intent(this,ContactUSActivity.class));
        }else if (id == R.id.nav_settings){
            //startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            if (AppCompatDelegate.getDefaultNightMode()
                    ==AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                navView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
            }else {
                AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                navView.setItemIconTintList(null);
            }
            recreate();
        }else if(id == R.id.nav_offline_posts){
            Intent i = new Intent(this,OfflineActivity.class);
            startActivity(i);
        }else if(id == R.id.nav_fb){
            //Open facebook page/username in app
            openFbUrl("app");
        }else if(id == R.id.nav_tw){
            //open username in twitter app
            openTwitterUrl("https://www.twitter.com/hitzgh1");
        }else if(id == R.id.nav_insta){
            openInstagram("app");
        }else if (id == R.id.nav_web_url){
            openWebView("https://m.facebook.com/hitzgh");
        }else if(id == R.id.nav_yt){
            openYoutubeUrl("https://www.youtube.com/hitzghtv");
        }else if(id == R.id.my_category){
            openSimpleCategoryPage("Automotive",462);
        }else if(id == R.id.nav_rate_us){
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        isActive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActive = false;

        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void setupSearch(){
        mSearchView.setLogoTextColor(R.color.md_amber_500);
        CardView cardView = mSearchView.findViewById(org.cryse.widget.persistentsearch.R.id.cardview_search);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.cardBackgroundColor));
        mSearchTintView = findViewById(R.id.view_search_tint);
        mSearchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {
            @Override
            public void onHomeButtonClick() {
                if(!drawer.isDrawerOpen(Gravity.LEFT))
                    drawer.openDrawer(Gravity.LEFT);
            }
        });
        setSearchView(mSearchView,mSearchTintView);
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {
                Intent intent = new Intent(getApplicationContext(),PostListActivity.class);
                intent.putExtra(PostListActivity.ARG_SEARCH,query);
                intent.putExtra(PostListActivity.ARG_TITLE,query);
                startActivity(intent);
            }

            @Override
            public void onSearchEditOpened() {
                openSearchEdit(mSearchTintView);
            }

            @Override
            public void onSearchEditClosed() {
                closeSearchEdit(mSearchTintView);
            }

            @Override
            public boolean onSearchEditBackPressed() {
                return false;
            }

            @Override
            public void onSearchExit() {

            }
        });
    }

    private class ProceessInBackground extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getSettings();
            return null;
        }
    }

    private void getSettings(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetSettings getSettings = new GetSettings(apiInterface,getApplicationContext());
        getSettings.setListner(new GetSettings.onComplete() {
            @Override
            public void onSuccess(Settings settings) {
                if(settings!=null) {
                    userSettings = settings;
                    saveSettings();
                    Log.e("SplashActivity", "Settings: " + userSettings.getAppTitle());
                }
            }

            @Override
            public void onError(String msg) {
                    Toasty.error(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        getSettings.execute();
    }

    private void saveSettings(){
        editor.putString(Config.APP_NAME,userSettings.getAppTitle());
        editor.putInt(Config.APP_VERSION,userSettings.getAppVersion());
        editor.putBoolean(Config.IS_FORCE_UPDATE,userSettings.isForceUpdate());
        editor.putString(Config.TOOLBAR_COLOR,userSettings.getToolbarColor());
        editor.putInt(Config.NO_OF_SECTIONS,userSettings.getSections().size());
        editor.putString(Config.SLIDER_CATEGORY,userSettings.getSliderCategory());
        editor.putBoolean(Config.IS_SLIDER_ENABLED,userSettings.isSliderEnabled());
        for (int i=0;i<userSettings.getSections().size();i++){
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_TITLE,userSettings.getSections().get(i).getTitle());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_SMALL_IMAGE_URL,userSettings.getSections().get(i).getImage());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_CATEGORY,userSettings.getSections().get(i).getCategoryId());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_TYPE,userSettings.getSections().get(i).getType());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_COUNT,userSettings.getSections().get(i).getCount());
        }
        editor.putBoolean(Config.IS_SETTINGS_SAVED,true);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupSearch();

    }
}
