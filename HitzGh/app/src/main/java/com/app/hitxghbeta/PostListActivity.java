package com.app.hitxghbeta;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hitxghbeta.fragments.PageFragment;
import com.app.hitxghbeta.fragments.VerticalListFragment;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetCategories;
import com.app.wplib.models.category.Category;
import com.mikepenz.materialize.MaterializeBuilder;
import com.nex3z.flowlayout.FlowLayout;

import org.cryse.widget.persistentsearch.PersistentSearchView;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PostListActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = "PostListActivity";
    //Arguments
    public static String ARG_CATEGORY = "arg_category";
    public static String ARG_SEARCH = "arg_search";
    public static String ARG_TITLE = "arg_title";
    public static String ARG_TYPE= "arg_type";
    public static String ARG_SHOW_IMG = "arg_showImg";
    public static String ARG_SHOW_IMG_URL = "arg_showImgUrl";
    public static String ARG_ACTIVITY_ID = "activity_id";
    public static String ARG_SHOW_PAGES_INSTEAD = "arg_show_pages";

    //Views
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView toolbarTitle;
    private FlowLayout flowLayout;
    private FrameLayout frameLayout;
    private PersistentSearchView searchView;
    private View searchViewTint;

    //Data
    private boolean isActive;
    private boolean showPages;
    private String searchQuery;
    private boolean loadChild;
    private String showImgUrl;
    private boolean showImg;
    private String category;
    private int type;
    private int activity_id;
    private String title;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        new MaterializeBuilder()
                .withActivity(this)
                .build();

        Toast.makeText(this, "Post list list list ", Toast.LENGTH_SHORT).show();

        if(getIntent()!=null){
            category = getIntent().getStringExtra(ARG_CATEGORY);
            type = getIntent().getIntExtra(ARG_TYPE,1);
            title = getIntent().getStringExtra(ARG_TITLE);
            showImg = getIntent().getBooleanExtra(ARG_SHOW_IMG,false);
            showImgUrl = getIntent().getStringExtra(ARG_SHOW_IMG_URL);
            searchQuery = getIntent().getStringExtra(ARG_SEARCH);
            showPages = getIntent().getBooleanExtra(ARG_SHOW_PAGES_INSTEAD,false);
        }

        //Binding Views
        swipeRefreshLayout = findViewById(R.id.swiperefresspostlist);
        collapsingToolbarLayout = findViewById(R.id.postListCollapsingLayout);
        toolbar = findViewById(R.id.postListToolbar);
        appBarLayout = findViewById(R.id.postListAppBar);
        toolbarTitle = findViewById(R.id.postListToolbarTitle);
        flowLayout = findViewById(R.id.postListFlowLayout);
        frameLayout = findViewById(R.id.postListFrameLayout);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }
        if(category!=null) {
            loadChildCategories();
        }
        setupSearch();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostListActivity.super.onBackPressed();
            }
        });
        toolbarTitle.setText(title);
        if(showPages){
            replaceFragment(R.id.postListFrameLayout, new PageFragment().newInstance(searchQuery), "PAGE_LIST", null);
        }else {
            replaceFragment(R.id.postListFrameLayout, new VerticalListFragment().newInstance(category, searchQuery, type), "POST_LIST", null);
        }
    }

    private void setupSearch(){
        searchView = findViewById(R.id.postList_searchview);
        searchViewTint = findViewById(R.id.postList_view_search_tint);
        setSearchView(searchView,searchViewTint);
        searchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {
                if(showPages){
                    replaceFragment(R.id.postListFrameLayout, new PageFragment().newInstance(query), "PAGE_LIST", null);
                }else {
                    if(query!=null) {
                        toolbarTitle.setText(query);
                        replaceFragment(R.id.postListFrameLayout, new VerticalListFragment().newInstance(category, query, type), "POST_LIST", null);
                        searchView.closeSearch();
                    }
                }
            }

            @Override
            public void onSearchEditOpened() {
                openSearchEdit(searchViewTint);
            }

            @Override
            public void onSearchEditClosed() {
                closeSearchEdit(searchViewTint);
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
                    categories.addAll(list);
                    addViews(totalPages);
                }
            }

            @Override
            public void onError(String msg) {
                if(isActive) {
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.error_loading_categories), Toast.LENGTH_SHORT).show();
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
        Log.e(TAG, "addViews: "+categories.size());
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
                    if(categories.get(position).getId()==Integer.parseInt(PostListActivity.this.category)) {
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
                    }
                }
            });
            flowLayout.addView(v);
        }
        if(categories.size()>0)
            flowLayout.setVisibility(View.VISIBLE);
    }

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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (collapsingToolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
            swipeRefreshLayout.setEnabled(false);
        } else {
            swipeRefreshLayout.setEnabled(true);
        }
    }
}
