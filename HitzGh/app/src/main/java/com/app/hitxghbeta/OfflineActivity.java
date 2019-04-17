package com.app.hitxghbeta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.hitxghbeta.adapters.VerticalPostAdapter;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.itemanimators.SlideRightAlphaAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfflineActivity extends BaseActivity {

    RecyclerView recyclerView;
    Toolbar toolbar;
    DatabaseHandler dh;
    List<Post> postList = new ArrayList<>();

    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter = new ItemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        toolbar = findViewById(R.id.offlineToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }
    }

    private void proceed(){
        postList.clear();
        dh = new DatabaseHandler(this);
        postList = dh.getOfflinePosts();
        Log.e("Size",postList.size()+"");
        recyclerView = findViewById(R.id.offlineRecycler);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.setItemAnimator(new SlideRightAlphaAnimator());
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<VerticalPostAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<VerticalPostAdapter> adapter, VerticalPostAdapter item, int position) {

                Intent intent = new Intent(OfflineActivity.this, PostActivity.class);
                intent.putExtra(PostActivity.POST_ID_STRING,postList.get(position).getId());
                intent.putExtra(PostActivity.IS_OFFLINE_POST,true);
                startActivity(intent);
                return false;
            }
        });
        fastAdapter.withEventHook(new VerticalPostAdapter.CategoryCardClickEvent());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OfflineActivity.super.onBackPressed();
            }
        });
        itemAdapter.clear();
        for (Post p:postList){
            itemAdapter.add(new VerticalPostAdapter(getApplicationContext(),p,1));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.exit){
            finish();
            moveTaskToBack(true);
        }
        return   super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        proceed();
    }
}
