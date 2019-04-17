package com.app.wplib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.category.Category;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 24/11/17 for personal use only.
 * Not permitted to use without permission
 */

public class GetCategories {

    private List<Category> categoryList = new ArrayList<>();

    public interface Listner{
        void onSuccessful(List<Category> list,int totalPages);

        void onError(String msg);
    }

    private DatabaseHandler databaseHandler;
    private Listner listner;
    private ApiInterface apiInterface;
    private static String context_embed = "embed";
    private boolean contextEmbedEnabled;
    private Context context;
    private int totalPages;
    private Integer page,post,hideEmpty,parent;
    private String search,orderBy,order,slug;

    public GetCategories(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    public void setContextEmbedEnabled(boolean contextEmbedEnabled) {
        this.contextEmbedEnabled = contextEmbedEnabled;
    }

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public void setHideEmpty(Integer hideEmpty) {
        this.hideEmpty = hideEmpty;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        if(isNetworkAvailable()) {
            Call<List<Category>> categories = apiInterface.getCategoriesList(page, search, orderBy, parent, order, 1, post,slug);
            Log.e("Making Request to"," url:"+categories.request().url());
            categories.enqueue(new Callback<List<Category>>() {
                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                    if (response.isSuccessful()) {
                        Log.e("Successful","Posts returned "+response.body().size());
                        totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                        categoryList = response.body();
                        for (Category c : categoryList) {
                            databaseHandler.addCategory(c);
                        }
                        listner.onSuccessful(categoryList, totalPages);
                    }
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {
                    listner.onError("Error connecting to server");
                }
            });
        }else {
            listner.onSuccessful(databaseHandler.getAllCategories(10),totalPages);
            listner.onError("No network available");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
