package com.app.hitxghbeta;

import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.hitxghbeta.fragments.CategoryFragment;
import com.mikepenz.materialize.MaterializeBuilder;

import org.cryse.widget.persistentsearch.PersistentSearchView;

public class CategoryActivity extends BaseActivity {

    public static final String ARG_PARENT = "arg_parent";
    public static final String ARG_POST = "arg_post";
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_SLUG = "arg_slug";
    int parent;
    int post;
    String title;
    String slug;
    private boolean isActive;

    private TextView titleView;
    private PersistentSearchView searchView;
    private View searchViewTint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_category);
        new MaterializeBuilder()
                .withActivity(this)
                .build();
        setupSearch();
        if(getIntent()!=null){
            parent = getIntent().getIntExtra(ARG_PARENT,0);
            post = getIntent().getIntExtra(ARG_POST,0);
            title = getIntent().getStringExtra(ARG_TITLE);
            slug = getIntent().getStringExtra(ARG_SLUG);
        }
        Log.e("CategoryActivity","Parent: "+title);
        titleView = findViewById(R.id.category_title);
        Toolbar toolbar = findViewById(R.id.category_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }
        if(title!=null){
            titleView.setText(title);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryActivity.super.onBackPressed();
            }
        });
        replaceFragment(R.id.categoryFrame,new CategoryFragment().newInstance(true,parent,post,null,slug),null
                ,null);
    }

    private void setupSearch(){
        searchView = findViewById(R.id.searchviewCategory);
        searchViewTint = findViewById(R.id.view_search_tintCategory);
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
                replaceFragment(R.id.categoryFrame,new CategoryFragment().newInstance(true,parent,post,query,null),null
                        ,null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
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
}