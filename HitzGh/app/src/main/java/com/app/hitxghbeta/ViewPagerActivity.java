package com.app.hitxghbeta;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.app.hitxghbeta.adapters.ViewPagerAdapter;
import com.app.hitxghbeta.others.CustomTypefaceSpan;
import com.mikepenz.materialize.MaterializeBuilder;

import org.cryse.widget.persistentsearch.PersistentSearchView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPagerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PersistentSearchView searchView;
    private View tintView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NavigationView navView;
    private AppBarLayout appBarLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        new MaterializeBuilder()
                .withActivity(this)
                .build();
        setupSearch();
        collapsingToolbarLayout = findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setContentScrim(getResources().getDrawable(R.drawable.gradient_background));
        appBarLayout = findViewById(R.id.appbar);
        Toolbar toolbar = findViewById(R.id.viewpager_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        DrawerLayout drawer = findViewById(R.id.drawer_layout_viewpager);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view_viewpager);
        navView.setNavigationItemSelectedListener(this);
        View hView =  navView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.nav_header_title);
        CircleImageView user_img = hView.findViewById(R.id.nav_header_imageView);
       // Button button = hView.findViewById(R.id.signInBtnClicked);
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
            appBarLayout.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }else {
            navView.setItemIconTintList(null);
        }
        Menu m = navView.getMenu();
        if(Config.defaultHomeScreen==3) {
            m.findItem(R.id.nav_posts).setVisible(false);
            m.findItem(R.id.nav_home).setVisible(false);
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

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupSearch(){
        searchView = findViewById(R.id.searchviewVP);
        tintView = findViewById(R.id.view_search_tintVP);
        setSearchView(searchView,tintView);
        searchView.setSearchListener(new PersistentSearchView.SearchListener() {
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
                openSearchEdit(tintView);
            }

            @Override
            public void onSearchEditClosed() {
                closeSearchEdit(tintView);
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),getApplicationContext());
        for(int i=0;i<getResources().getStringArray(R.array.tab_name).length;i++)
            adapter.addFragment(getResources().getStringArray(R.array.tab_name)[i],
                    getResources().getIntArray(R.array.tab_id)[i]);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(getResources().getStringArray(R.array.tab_name).length);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Dont put here
            }

            @Override
            public void onPageSelected(int position) {
                //Do the work here
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Dont put here
            }
        });
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            case R.id.action_search:
                searchView.openSearch();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_viewpager);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(searchView.isEditing()){
                searchView.cancelEditing();
                closeSearchEdit(tintView);
            }else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
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
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_home){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_posts){
            Intent intent = new Intent(this,CarouselPostListActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_pages) {
            Intent intent = new Intent(this,PostListActivity.class);
            intent.putExtra(PostListActivity.ARG_SHOW_PAGES_INSTEAD,true);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(this,CategoryActivity.class));
        }else if (id == R.id.nav_contacus){
            startActivity(new Intent(this,ContactUSActivity.class));
        }
        else if (id == R.id.nav_settings){
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
            openFbUrl("Google");
        }else if(id == R.id.nav_tw){
            //open username in twitter app
            openTwitterUrl("https://www.twitter.com/hitzgh1");
        }else if(id == R.id.nav_insta){
            openInstagram("google");
        }else if (id == R.id.nav_web_url){
            Toast.makeText(ViewPagerActivity.this, "Web", Toast.LENGTH_SHORT).show();
            openWebUrl("https://m.facebook.com/hitzgh");
        }else if(id == R.id.nav_yt){
            Toast.makeText(ViewPagerActivity.this, "Youtube", Toast.LENGTH_SHORT).show();
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_viewpager);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
