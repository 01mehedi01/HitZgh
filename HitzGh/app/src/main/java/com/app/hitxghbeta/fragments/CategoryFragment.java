package com.app.hitxghbeta.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.CarouselPostListActivity;
import com.app.hitxghbeta.PostListActivity;
import com.app.hitxghbeta.R;
import com.app.hitxghbeta.adapters.CategoryGridAdapter;
import com.app.hitxghbeta.others.ItemOffsetDecoration;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetCategories;
import com.app.wplib.models.category.Category;
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

public class CategoryFragment extends Fragment {

    private static final String ARG_INFRAME = "arg_inFrame";
    private static final String ARG_POST = "arg_post";
    private static final String ARG_PARENT = "arg_parent";
    private static final String ARG_SEARCH = "arg_search_cf";
    private static final String ARG_SLUG = "arg_slug";
    Integer parent,post;
    private String slug;
    private boolean isActive;
    private RelativeLayout rootView;
    private RecyclerView recyclerView;
    private int page=1;
    private int listSize=0;
    private int total;
    private String search;
    private List<Category> allCategories = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
    private ItemAdapter itemAdapter = new ItemAdapter();
    private FastAdapter fastAdapter;
    ApiInterface apiInterface;
    GetCategories getCategories;
    private ProgressBar progressBar;
    private ItemOffsetDecoration itemDecoration;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(boolean inFrame, Integer parent, int post,String search,String slug) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_INFRAME,inFrame);
        args.putInt(ARG_PARENT,parent);
        args.putInt(ARG_POST,post);
        args.putString(ARG_SEARCH,search);
        args.putString(ARG_SLUG,slug);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parent = getArguments().getInt(ARG_PARENT);
            post = getArguments().getInt(ARG_POST);
            search = getArguments().getString(ARG_SEARCH);
            slug = getArguments().getString(ARG_SLUG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        rootView = view.findViewById(R.id.rootCatFrame);
        recyclerView = view.findViewById(R.id.categoryListRecyclerView);
        progressBar = view.findViewById(R.id.categoryGridProgressBar);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getCategories = new GetCategories(apiInterface,getActivity());
        getCategories.setHideEmpty(0);
        getCategories.setSearch(search);
        getCategories.setSlug(slug);
        getCategories.setParent(parent);
        if(post!=0){
            getCategories.setPost(post);
        }
        //FastAdapter Settings
        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
        recyclerView.setAdapter(fastAdapter);
        sendRequest(page);
        return view;
    }

    private void sendRequest(int a){
        getCategories.setPage(a);
        getCategories.setListner(new GetCategories.Listner() {
            @Override
            public void onSuccessful(List<Category> list, int totalPages) {
                if(isActive) {
                    progressBar.setVisibility(View.GONE);
                    categories.clear();
                    categories.addAll(list);
                    allCategories.addAll(list);
                    total = totalPages;
                    if (page == 1) {
                        page++;
                        proceed();
                    } else {
                        page++;
                        addData();
                    }
                }
            }

            @Override
            public void onError(String msg) {
                if(isActive) {
                    Toasty.error(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        getCategories.execute();
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

    private void addData(){
        for(int i=0;i<categories.size();i++){
            itemAdapter.add(new CategoryGridAdapter(getContext(),
                    categories.get(i)));
        }
        listSize += categories.size();
    }

    private void proceed(){
        if(slug!=null&&allCategories.size()==0){
            Toasty.error(getActivity(),"Error is url",Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        if(slug!=null&&allCategories.size()>0){
            Intent intent = new Intent(getActivity(), CarouselPostListActivity.class);
            intent.putExtra(PostListActivity.ARG_CATEGORY,allCategories.get(0).getId()+"");
            intent.putExtra(PostListActivity.ARG_TYPE,1);
            intent.putExtra(PostListActivity.ARG_TITLE,allCategories.get(0).getName());
            intent.putExtra(PostListActivity.ARG_SHOW_IMG,true);
            try {
                intent.putExtra(PostListActivity.ARG_SHOW_IMG_URL, allCategories.get(0).getCmb2().getWordroidFields().getCategoryImage());
            }catch (NullPointerException e){
                Log.e("Site Error","Wordpress plugin not installed");
            }
            startActivity(intent);
            getActivity().finish();
        }else {
            final List<CategoryGridAdapter> strings = new ArrayList<>();
            for (int i = 0; i < categories.size(); i++) {
                itemAdapter.add(new CategoryGridAdapter(getContext(),
                        categories.get(i)));
            }
            listSize += categories.size();
            fastAdapter.withSelectable(true);
            fastAdapter.withOnClickListener(new OnClickListener() {
                @Override
                public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
                    Intent intent = new Intent(getActivity(), CarouselPostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_CATEGORY, allCategories.get(position).getId() + "");
                    intent.putExtra(PostListActivity.ARG_TYPE, 1);
                    intent.putExtra(PostListActivity.ARG_TITLE, allCategories.get(position).getName());
                    try {
                        intent.putExtra(PostListActivity.ARG_SHOW_IMG_URL, allCategories.get(position).getCmb2().getWordroidFields().getCategoryImage());
                    } catch (NullPointerException e) {
                        Log.e("Site Error", "Wordpress plugin not installed");
                    }
                    startActivity(intent);
                    return true;
                }
            });
            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore(int currentPage) {
                    footerAdapter.clear();
                    footerAdapter.add(new ProgressItem().withEnabled(false));
                    if (page <= total) {
                        sendRequest(page);
                    } else {
                        footerAdapter.clear();
                    }
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
    }
}
