package com.app.hitxghbeta.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.CarouselAdapter;
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

public class CarouselFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private int category;
    private String embed="embed";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private boolean isActive;
    private List<Post> allposts = new ArrayList<>();
    private List<Post> templist = new ArrayList<>();
    private FastAdapter fastAdapter;
    private DatabaseHandler dh;
    ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
    private ItemAdapter itemAdapter = new ItemAdapter();
    public CarouselFragment() {
        // Required empty public constructor
    }

    public static CarouselFragment newInstance(int param1) {
        CarouselFragment fragment = new CarouselFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carousel, container, false);
        dh = new DatabaseHandler(getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = view.findViewById(R.id.carouselRecyclerView);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = view.findViewById(R.id.carouselProgressBar);
        progressBar.setIndeterminate(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        footerAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
        recyclerView.setAdapter(fastAdapter);
        sendRequest();
        return view;
    }

    private void sendRequest(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetRecentPost getRecentPost = new GetRecentPost(apiInterface,getActivity());
        getRecentPost.setPage(1);
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setCategoryEnabled(true);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(isActive) {
                    templist.addAll(postList);
                    proceed();
                }
            }

            @Override
            public void onError(String msg) {
                Toasty.error(getActivity(),getResources().getString(R.string.loading_error_msg),Toast.LENGTH_SHORT).show();
            }
        });
        getRecentPost.execute();
    }

    private void addData(){
        allposts.addAll(templist);
    }

    private void proceed(){
        allposts.addAll(templist);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        for (int i=0;i<templist.size();i++){
            itemAdapter.add(new CarouselAdapter(getContext(),templist.get(i)));
        }

        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                Toast.makeText(getActivity(),position+" position clicked",Toast.LENGTH_LONG).show();
                return false;
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                Toast.makeText(getActivity(),"Page: "+currentPage,Toast.LENGTH_LONG).show();
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
