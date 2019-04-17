package com.app.hitxghbeta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.CarouselPostListActivity;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.PostActivity;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.VerticalPostAdapter;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetPages;
import com.app.wplib.GetRecentPost;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.FastAdapter;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.itemanimators.SlideRightAlphaAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.app.hitxghbeta.MainApplication.POST_IDS;

public class VerticalListFragment extends Fragment {

    private static final String category_param = "category";
    private static final String item_type = "item_type";
    private static final String ARG_SEARCH = "arg_search";
    private static final String ARG_START_INDEX = "arg_start_index";
    private int startIndex;
    private int itemType;
    private String category;
    private String search;
    private RecyclerView recyclerView;
    private Context context;
    private boolean isActive;
    private int page = 1;
    private int totalPage;
    private List<Post> allposts = new ArrayList<>();

    private FastItemAdapter fastItemAdapter;
    private DatabaseHandler dh;
    private ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
    private FastAdapter fastAdapter;
    private ProgressBar progressBar;
    private ItemAdapter itemAdapter = new ItemAdapter();
    private ApiInterface apiInterface;
    private GetRecentPost getRecentPost;
    private View verticalInvisibleLayout;
    private Button reloadBtn;
    private GetPages getPages;
    private static final String TAG = "VerticalListFragment";
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    private AdView mAdView,topads;
    private SwipeRefreshLayout swipeRefreshLayout;


    public VerticalListFragment() {
        // Required empty public constructor
    }

    public static VerticalListFragment newInstance(String category,String search,int type) {
        VerticalListFragment fragment = new VerticalListFragment();
        Bundle args = new Bundle();
        args.putString(category_param, category);
        args.putString(ARG_SEARCH,search);
        args.putInt(item_type,type);
        fragment.setArguments(args);
        return fragment;
    }

    public static VerticalListFragment newInstance(String category,String search,int type,int startIndex) {
        VerticalListFragment fragment = new VerticalListFragment();
        Bundle args = new Bundle();
        args.putString(category_param, category);
        args.putString(ARG_SEARCH,search);
        args.putInt(item_type,type);
        args.putInt(ARG_START_INDEX,startIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(category_param);
            itemType = getArguments().getInt(item_type);
            search = getArguments().getString(ARG_SEARCH);
            startIndex = getArguments().getInt(ARG_START_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_vertical_list_type, container, false);
        fastItemAdapter = new FastItemAdapter();
        recyclerView = view.findViewById(R.id.verticalListTypeRecyclerView);
        progressBar = view.findViewById(R.id.verticalListTypeItemProgressBar);
        verticalInvisibleLayout = view.findViewById(R.id.verticalInvisibleLayout);
        mAdView = view.findViewById(R.id.adView);
        reloadBtn = view.findViewById(R.id.reloadBtn);
        topads = view.findViewById(R.id.postviewhomepage_adView);

        LoadPage();

        return view;
    }

    public void LoadPage(){


        AdRequest adRequestt = new AdRequest.Builder().build();
        topads.loadAd(adRequestt);
        topads.setVisibility(View.VISIBLE);

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verticalInvisibleLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                sendRequest();
            }
        });
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        //Request
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getRecentPost = new GetRecentPost(apiInterface,getContext());
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setSearch(search);
        getRecentPost.setCategory(category);
        getRecentPost.setCategoryEnabled(true);
        getRecentPost.setMediaEnabled(true);
        Log.e("Category","Id "+category);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(isActive && postList.size()>0) {
                    totalPage = totalPages+1;

                    if(page==1) {
                        if(startIndex!=0&&postList.size()>startIndex){
                            ((CarouselPostListActivity)getActivity()).setupSlider(postList.subList(0,startIndex));
                             proceed(postList.subList(startIndex, postList.size()));
                             allposts.addAll(postList.subList(startIndex, postList.size()));
                        }else {
                            proceed(postList);
                            allposts.addAll(postList);
                        }
                    }else {
                        addData(postList);
                        allposts.addAll(postList);
                    }
                    page++;
                }else {
                    if(isActive) {
                        progressBar.setVisibility(View.GONE);
                        if (page == 1)
                            verticalInvisibleLayout.setVisibility(View.VISIBLE);
                        Toasty.error(getActivity(), getResources().getString(R.string.loading_error_msg), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if(isActive) {
                    progressBar.setVisibility(View.GONE);
                    if(page==1)
                        verticalInvisibleLayout.setVisibility(View.VISIBLE);
                    Toasty.error(getActivity(), getResources().getString(R.string.loading_error_msg), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Recycler view & Adapter
        footerAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.setItemAnimator(new SlideRightAlphaAnimator());
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<VerticalPostAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<VerticalPostAdapter> adapter, VerticalPostAdapter item, int position) {
                if(allposts.get(position).getExcerpt().isJsonMemberProtected()){
                    Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                }else {
                    getPostIdList(allposts);
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(PostActivity.POST_ID_STRING,allposts.get(position).getId());
                    startActivity(intent);
                }
                return false;
            }
        });
        fastAdapter.withEventHook(new VerticalPostAdapter.CategoryCardClickEvent());
        sendRequest();
        loadAds();

    }

    private void getPostIdList(List<Post> postsList){
        POST_IDS.clear();
        for (Post p:postsList){
            POST_IDS.add(p.getId());
        }
    }

    private void sendRequest(){
        getRecentPost.setPage(page);
        getRecentPost.setCategory(category);
        getRecentPost.execute();
    }

    private void addData(List<Post> postList){
        footerAdapter.clear();
        for (Post p:postList){
            itemAdapter.add(new VerticalPostAdapter(getActivity(),p,itemType));
        }
    }

    private void proceed(List<Post> postList){
        for (Post p:postList){
            itemAdapter.add(new VerticalPostAdapter(getActivity(),p,itemType));
            //itemType=1;
        }
        progressBar.setVisibility(View.GONE);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {

            @Override
            public void onLoadMore(int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                if(page<totalPage) {
                    Toasty.info(getActivity(),"Loading more...",Toast.LENGTH_SHORT).show();
                    sendRequest();
                }else{
                    footerAdapter.clear();
                }
            }
        };
        if(page+1!=totalPage) {
            recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        }

    }

    private void loadAds(){
        if(Config.showBannerAds) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive=false;
    }
}
