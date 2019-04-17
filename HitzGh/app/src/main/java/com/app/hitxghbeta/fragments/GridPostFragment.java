package com.app.hitxghbeta.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.GridPostAdapter;
import com.app.hitxghbeta.others.ItemOffsetDecoration;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetRecentPost;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class GridPostFragment extends Fragment {

    private int page = 1;

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private ProgressBar gridProgressbar;
    private DatabaseHandler dh;
    private List<Post> postList = new ArrayList<>();

    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter = new ItemAdapter();
    private ApiInterface apiInterface;
    private GetRecentPost getRecentPost;
    ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();

    private boolean random;
    private boolean inFrame;
    private boolean isActive;
    private String category;

    public GridPostFragment() {
        // Required empty public constructor
    }

    public static GridPostFragment newInstance(boolean inFrame, boolean random, int category) {
        GridPostFragment fragment = new GridPostFragment();
        Bundle args = new Bundle();
        args.putBoolean("isFrame",inFrame);
        args.putBoolean("random",random);
        args.putInt("category",category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inFrame = getArguments().getBoolean("isFrame");
            random = getArguments().getBoolean("random");
            category = getArguments().getString("category");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid_post, container, false);
        gridProgressbar = view.findViewById(R.id.gridProgressBar);
        dh = new DatabaseHandler(getContext());
        recyclerView = view.findViewById(R.id.gridPostRecyclerView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //define span size for this position
                //for example, if you have 2 column per row, you can implement something like that:
                if(position == 0||position==5) {
                    return 2; //item will take 2 column (full row size)
                } else {
                    return 1; //item will take 1 column (half)
                }
            }
        });
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.setLayoutManager(gridLayoutManager);
        //Request
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getRecentPost = new GetRecentPost(apiInterface,getActivity());
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setCategoryEnabled(false);
        getRecentPost.setCategory(category);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(isActive) {
                    if (page == 1) {
                        proceed(postList);
                    }
                    else {
                        addData(postList);
                    }
                }
            }

            @Override
            public void onError(String msg) {
                Toasty.error(getActivity(),getResources().getString(R.string.loading_error_msg),Toast.LENGTH_SHORT).show();
            }
        });
        //FastAdapter
        footerAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
        recyclerView.setAdapter(fastAdapter);
        sendRequest(page);
        return view;
    }

    private void sendRequest(int p){
        getRecentPost.setPage(p);
        getRecentPost.execute();
    }

    private void addData(List<Post> postList){
        for(Post p:postList){
            itemAdapter.add(new GridPostAdapter(getActivity(),p));
        }
    }

    private void proceed(List<Post> postList){
        recyclerView.setVisibility(View.VISIBLE);
        gridProgressbar.setVisibility(View.GONE);
        for(Post p:postList){
            itemAdapter.add(new GridPostAdapter(getActivity(),p));
        }
        fastAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                Toast.makeText(getActivity(),"Clicked: "+position,Toast.LENGTH_LONG).show();
                return false;
            }
        });
        if(!inFrame) {
            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore(int currentPage) {
                    footerAdapter.clear();
                    footerAdapter.add(new ProgressItem().withEnabled(false));
                    sendRequest(++page);
                }
            });
        }
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
