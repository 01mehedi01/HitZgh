package com.app.hitxghbeta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.fragments.VerticalListFragment;
import com.app.hitxghbeta.others.CustomTypefaceSpan;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetCategories;
import com.app.wplib.models.category.Category;
import com.app.wplib.models.post.Post;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nex3z.flowlayout.FlowLayout;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.cryse.widget.persistentsearch.PersistentSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CarouselPostListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener ,
        AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = "PostListActivity";

    //Views
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CarouselView carouselView;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private FlowLayout flowLayout;
    private FrameLayout frameLayout;
    private PersistentSearchView searchView;
    private View searchViewTint;
    private NavigationView navView;
    private AppBarLayout appBarLayout;
    private AdView topads;

    //Data
    private boolean isActive;
    private String searchQuery;
    private String category;
    private int type;
    private String title;

    private List<Category> categories = new ArrayList<>();
    private List<Post> sliderPosts = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel_post_list);

        if(getIntent()!=null){
            category = getIntent().getStringExtra(PostListActivity.ARG_CATEGORY);
            type = getIntent().getIntExtra(PostListActivity.ARG_TYPE,1);
            title = getIntent().getStringExtra(PostListActivity.ARG_TITLE);
            searchQuery = getIntent().getStringExtra(PostListActivity.ARG_SEARCH);
        }

         swipeRefreshLayout = findViewById(R.id.swiperefressfragment);
        //Binding Views
        collapsingToolbarLayout = findViewById(R.id.carouselPostListCollapsingLayout);
        carouselView = findViewById(R.id.carouselPostListCarouselView);
        toolbar = findViewById(R.id.carouselPostListToolbar);
        appBarLayout = findViewById(R.id.carouselPostListAppBar);
        toolbarTitle = findViewById(R.id.carouselPostListToolbarTitle);
        flowLayout = findViewById(R.id.carouselPostListFlowLayout);
        frameLayout = findViewById(R.id.carouselPostListFrameLayout);

        if(category!=null) {
            //Toast.makeText(this, "category"+category, Toast.LENGTH_SHORT).show();
            loadChildCategories();
        }
        //setupSearch();


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_carousel_posts);
        if(category==null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CarouselPostListActivity.super.onBackPressed();
                }
            });
        }
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               CarouseWebLoad();
               carouselView.setVisibility(View.GONE);
               swipeRefreshLayout.setRefreshing(true);

           }
       });

        setupSearch();
        CarouseWebLoad();

    }


    private void  CarouseWebLoad(){




        MobileAds.initialize(this, getString(R.string.admob_app_id));

        navView = findViewById(R.id.nav_view_carousel_posts);
        navView.setNavigationItemSelectedListener(this);
        View hView =  navView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.nav_header_title);
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
           // button.setText("Logout");
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            navView.setItemIconTintList(getResources().getColorStateList(R.color.primaryTextColor));
            toolbar.setBackground(null);
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }else {
            navView.setItemIconTintList(null);
        }
        Menu m = navView.getMenu();
        if(Config.defaultHomeScreen==2) {
            m.findItem(R.id.nav_home).setVisible(false);
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
        if(title!=null) {
            toolbarTitle.setText(title);
        }
        replaceFragment(R.id.carouselPostListFrameLayout,new VerticalListFragment().newInstance(category,searchQuery,type,6),"POST_LIST",null);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupSearch(){
        searchView = findViewById(R.id.carouselPostList_searchview);
        searchViewTint = findViewById(R.id.carouselPostList_view_search_tint);
        setSearchView(searchView,searchViewTint);
        searchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {
               // Toast.makeText(CarouselPostListActivity.this, "onSearchCleared", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {
                carouselView.setVisibility(View.GONE);
                replaceFragment(R.id.carouselPostListFrameLayout,new VerticalListFragment().newInstance(category,query,type),"POST_LIST",null);
            }

            @Override
            public void onSearchEditOpened() {
                openSearchEdit(searchViewTint);
            }

            @Override
            public void onSearchEditClosed() {
                closeSearchEdit(searchViewTint);
               // finish();
               // Toast.makeText(CarouselPostListActivity.this, "onSearchEditClosed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onSearchEditBackPressed() {
               // Toast.makeText(CarouselPostListActivity.this, "onSearchEditBackPressed", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSearchExit() {

                CarouseWebLoad();
                carouselView.setVisibility(View.GONE);
            }
        });
    }

    private void loadChildCategories(){

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetCategories getCategories = new GetCategories(apiInterface,getApplicationContext());
        if(category==null){
            getCategories.setParent(null);
        }else{
            getCategories.setParent(Integer.parseInt(category));
        }
        getCategories.setListner(new GetCategories.Listner() {
            @Override
            public void onSuccessful(List<Category> list, int totalPages) {
                if(list.size()>0&&isActive){
                   // Toast.makeText(getApplicationContext(),"Size: "+list.size(),Toast.LENGTH_SHORT).show();


                    categories.addAll(list);
                    addViews(totalPages);
                }
            }

            @Override
            public void onError(String msg) {
                if(isActive) {
                    Toasty.error(getApplicationContext(), "Error loading categoires", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getCategories.execute();
    }


    private void addViews(int totalPages){

        if(totalPages>1){
            Category category = new Category();
            category.setName("more...");
            if(this.category!=null)
                category.setId(Integer.parseInt(this.category));
            categories.add(category);
        }
        for (int i=0;i<categories.size();i++) {
            final int position=i;
            View v = getLayoutInflater().inflate(R.layout.category_btn_rect,null);
            CardView cardView = v.findViewById(R.id.cat_card);
            final TextView textView = v.findViewById(R.id.cat_title);
            Colors r = new Colors();
            cardView.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(),r.getRandomColor()));
            if(categories.get(position).getCmb2()!=null){
                String color = categories.get(position).getCmb2().getWordroidFields().getCategoryColor();
                if(color!=null&&!color.isEmpty()&&!color.equals("#ffffff"))
                    cardView.setCardBackgroundColor(Color.parseColor(color));
            }
            textView.setText(categories.get(position).getName());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(categories.get(position).getId()==Integer.parseInt(CarouselPostListActivity.this.category)) {
                        Intent intent = new Intent(getApplicationContext(),CategoryActivity.class);
                        intent.putExtra(CategoryActivity.ARG_PARENT,categories.get(position).getId());
                        intent.putExtra(CategoryActivity.ARG_TITLE,title);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                        intent.putExtra(PostListActivity.ARG_CATEGORY,categories.get(position).getId()+"");
                        intent.putExtra(PostListActivity.ARG_TYPE,1);
                        intent.putExtra(PostListActivity.ARG_TITLE,categories.get(position).getName());
                        startActivity(intent);
                        //Toast.makeText(CarouselPostListActivity.this, "Post List", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            flowLayout.addView(v);
        }
        if(categories.size()>0)
            flowLayout.setVisibility(View.VISIBLE);
    }

    public void setupSlider(List<Post> postList) {
        if (postList.size() > 0) {


            if(sliderPosts.size() !=0) {
                //Toast.makeText(this, "Delete"+sliderPosts.size(), Toast.LENGTH_SHORT).show();
                sliderPosts.removeAll(sliderPosts);
            }


                sliderPosts.addAll(postList);
                carouselView.setVisibility(View.VISIBLE);
                carouselView.setViewListener(viewListener);
                carouselView.setPageCount(sliderPosts.size());
              //  Toast.makeText(this, "aftar"+sliderPosts.size(), Toast.LENGTH_SHORT).show();




        } else {
            carouselView.setVisibility(View.GONE);
        }
    }


    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            final int i=position;
            View view = getLayoutInflater().inflate(R.layout.slider_item,null);

            ImageView sliderImage = view.findViewById(R.id.sliderImage);
            TextView sliderCategory = view.findViewById(R.id.sliderCategory);
            TextView sliderTitle = view.findViewById(R.id.sliderTitle);

            LinearLayout textContainer = view.findViewById(R.id.sliderContainerLinearLayout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0,0,0,0);
            textContainer.setLayoutParams(params);

            String title,category,img;
            title = sliderPosts.get(position).getTitle().getRendered();
            if(Config.isPluginInstalled){
                if(sliderPosts.get(i).getCategoryDetails().size()>0) {
                    category = sliderPosts.get(i).getCategoryDetails().get(0).getName();
                }else {
                    category = "Undefined";
                }
                if(sliderPosts.get(i).getBetterFeaturedImage()!=null) {
                    img = sliderPosts.get(i).getBetterFeaturedImage().getPostThumbnail();
                }else {
                    img = null;
                }
            }else {
                category = sliderPosts.get(i).getCategoryDetails().get(0).getName();
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
                    Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_TITLE,sliderPosts.get(i).getCategoryDetails().get(0).getName());
                    intent.putExtra(PostListActivity.ARG_CATEGORY,sliderPosts.get(i).getCategoryDetails().get(0).getId()+"");
                    startActivity(intent);
                    Toast.makeText(CarouselPostListActivity.this, "Postlis Activity", Toast.LENGTH_SHORT).show();
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
                }
            });

            return view;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        appBarLayout.addOnOffsetChangedListener(this);
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
                break;
            case R.id.exitmain:
                finish();
                moveTaskToBack(true);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_carousel_posts);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else{
            if(searchView.isEditing()){
                searchView.cancelEditing();
                closeSearchEdit(searchViewTint);
            }else {
                if(category!=null)
                    this.doubleBackToExitPressedOnce=true;
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

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_posts){
//            Intent intent = new Intent(this,MainActivity.class);
//            startActivity(intent);
            CarouseWebLoad();
            carouselView.setVisibility(View.GONE);
        }else if(id == R.id.nav_tab_posts){
            Intent intent = new Intent(this,ViewPagerActivity.class);
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
            //open username in twitter app;
            openTwitterUrl("https://www.twitter.com/hitzgh1");
        }else if(id == R.id.nav_insta){
            openInstagram("google");
        }else if (id == R.id.nav_web_url){
            openWebUrl("https://m.facebook.com/hitzgh");
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout_carousel_posts);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setEnabled(true);
        }
    }
}
