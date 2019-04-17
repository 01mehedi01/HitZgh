package com.app.hitxghbeta.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.PostActivity;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.HorizontalPostsAdapter;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetRecentPost;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HorizontalPostsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_SEARCH = "search_string";
    private static final String ARG_CAT_LIST = "category_list";
    private static final String ARG_IS_ROOT = "root_in_frame";
    private static final String ARG_POST_TO_EXCLUDE = "post_to_exclude";

    private boolean isRoot;
    private String embed="embed";
    private RecyclerView recyclerView;
    private boolean isActive;
    private boolean isCarousel=false;
    private Button reloadBtn;
    private int page = 1;
    private int mediaQueue=0;
    private int categoryQueue = 0;
    private int totalPages;
    private List<Integer> query_categories = new ArrayList<>();
    private String query_category_string = null;
    private String postsToExclude;
    private String query_string;
    private List<Post> allposts = new ArrayList<>();
    private List<Post> templist = new ArrayList<>();
    private FastAdapter fastAdapter;
    private DatabaseHandler dh;
    private ItemAdapter<ProgressItem> footerAdapter;
    ItemAdapter itemAdapter = new ItemAdapter();
    private ProgressBar hpProgressBar;

    public HorizontalPostsFragment() {
        // Required empty public constructor
    }

    public static HorizontalPostsFragment newInstance(boolean isCarousel,boolean isRoot,String categories,String search,String postId) {
        HorizontalPostsFragment fragment = new HorizontalPostsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1,isCarousel);
        args.putString(ARG_CAT_LIST,categories);
        args.putBoolean(ARG_IS_ROOT,isRoot);
        args.putString(ARG_SEARCH,search);
        args.putString(ARG_POST_TO_EXCLUDE,postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isCarousel = getArguments().getBoolean(ARG_PARAM1);
            query_category_string = getArguments().getString(ARG_CAT_LIST);
            query_string = getArguments().getString(ARG_SEARCH);
            isRoot = getArguments().getBoolean(ARG_IS_ROOT);
            postsToExclude = getArguments().getString(ARG_POST_TO_EXCLUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_horizontal_posts, container, false);
        hpProgressBar = (ProgressBar)view.findViewById(R.id.hpProgressBar);
        reloadBtn = view.findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest(page);
            }
        });
        dh = new DatabaseHandler(getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = view.findViewById(R.id.horizontalPostFragmentRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        fastAdapter = new FastItemAdapter().with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);
        sendRequest(page);
        return view;
    }

    private void sendRequest(int page){
        Log.e("Query Cat","String: "+query_category_string);
        reloadBtn.setVisibility(View.GONE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetRecentPost getRecentPost = new GetRecentPost(apiInterface,getActivity());
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setCategoryEnabled(false);
        getRecentPost.setExcluded(postsToExclude);
        getRecentPost.setCategory(query_category_string);
        getRecentPost.setPage(page);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(isActive) {
                    templist.clear();
                    allposts.addAll(postList);
                    proceed(postList);
                }
            }

            @Override
            public void onError(String msg) {
                reloadBtn.setVisibility(View.VISIBLE);
                hpProgressBar.setVisibility(View.GONE);
                if(isActive) {
                    Toasty.error(getActivity(), getResources().getString(R.string.loading_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });
        getRecentPost.execute();
    }

    private void addData(){
        allposts.addAll(templist);
    }

    private void proceed(List<Post> postList){
        recyclerView.setVisibility(View.VISIBLE);
        hpProgressBar.setVisibility(View.GONE);
        for (int i=0;i<postList.size();i++){
            itemAdapter.add(new HorizontalPostsAdapter(getContext(),postList.get(i)));
        }
        footerAdapter = new ItemAdapter<>();
        fastAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(PostActivity.POST_ID_STRING,allposts.get(position).getId());
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=true;
    }
}
