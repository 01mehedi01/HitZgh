package com.app.hitxghbeta.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.PostActivity;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.VerticalPostAdapter;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetPages;
import com.app.wplib.models.page.Page;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.itemanimators.SlideRightAlphaAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PageFragment extends Fragment {

    private static final String ARG_PAGE = "arg_page";
    private static final String ARG_SEARCH = "arg_search";

    private Integer page=1;
    private int totalPage;
    private String search;
    private List<Page> pagesList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GetPages getPages;
    private ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter = new ItemAdapter();
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    public PageFragment() {

    }

    public static PageFragment newInstance(String search) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH, search);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            search = getArguments().getString(ARG_SEARCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = view.findViewById(R.id.pagesRecyclerView);
        progressBar = view.findViewById(R.id.pagesProgressBar);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        footerAdapter = ItemAdapter.items();
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
        recyclerView.setAdapter(fastAdapter);
        recyclerView.setItemAnimator(new SlideRightAlphaAnimator());
        fastAdapter.withSelectable(true);
        fastAdapter.withOnClickListener(new OnClickListener<VerticalPostAdapter>() {
            @Override
            public boolean onClick(View v, IAdapter<VerticalPostAdapter> adapter, VerticalPostAdapter item, int position) {
                if(pagesList.get(position).getExcerpt().isJsonMemberProtected()){
                    Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), PostActivity.class);
                    intent.putExtra(PostActivity.POST_ID_STRING,pagesList.get(position).getId());
                    intent.putExtra(PostActivity.LOAD_PAGE,true);
                    startActivity(intent);
                }
                return false;
            }
        });
        fastAdapter.withEventHook(new VerticalPostAdapter.CategoryCardClickEvent());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getPages = new GetPages(apiInterface,getContext());
        getPages.setMediaEnabled(true);
        getPages.setListner(new GetPages.Listner() {
            @Override
            public void onSuccessful(List<Page> pageList,int totalPages) {
                totalPage = totalPages;
                if (pageList.size()>0){
                    pagesList.addAll(pageList);
                    if(page==1){
                        page++;
                        proceed(pageList);
                    }else{
                        page++;
                        addData(pageList);
                    }
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
        sendRequest(page);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.exit_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.exit){
            getActivity().finish();
            getActivity().moveTaskToBack(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendRequest(int page){
        getPages.setPage(page);
        getPages.setSearch(search);
        getPages.execute();
    }

    private void addData(List<Page> list){
        for(Page p:list){
            itemAdapter.add(new VerticalPostAdapter(getContext(),p,1));
        }
    }

    private void proceed(List<Page> list){
        Log.e("Total Pages",totalPage+"");
        for(Page p:list){
            itemAdapter.add(new VerticalPostAdapter(getContext(),p,1));
        }
        progressBar.setVisibility(View.GONE);

        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(footerAdapter) {

            @Override
            public void onLoadMore(int currentPage) {
                footerAdapter.clear();
                footerAdapter.add(new ProgressItem().withEnabled(false));
                if(page<totalPage) {
                    Toasty.info(getActivity(),"Loading more...",Toast.LENGTH_SHORT).show();
                    sendRequest(page);
                }else{
                    Toasty.success(getActivity(),"All loaded",Toast.LENGTH_SHORT).show();
                    footerAdapter.clear();
                }
            }
        };
        if(page+1<=totalPage) {
            recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        }
    }
}