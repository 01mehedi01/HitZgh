package com.app.wplib;

import android.content.Context;
import android.util.Log;

import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.media.Media;
import com.app.wplib.models.page.Page;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 28/11/17.
 */

public class GetPages {

    public interface Listner{
        void onSuccessful(List<Page> pageList,int totalPages);
        void onFailed(String message);
    }
    private Listner listner;
    List<Media> mediaList = new ArrayList<>();
    private List<Page> pageList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private ApiInterface apiInterface;
    private Context context;
    private String contextEmbed = null;
    private int page;
    private String search;
    private int totalPages;
    private int totalNoOfPages;
    private int mediaQueue;
    private boolean mediaEnabled;

    public GetPages(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalNoOfPages() {
        return totalNoOfPages;
    }

    public void setMediaEnabled(boolean mediaEnabled) {
        this.mediaEnabled = mediaEnabled;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        Call<List<Page>> call = apiInterface.getpageList(page,search,contextEmbed);
        Log.e("Making Reques","To "+call.request().url());
        call.enqueue(new Callback<List<Page>>() {
            @Override
            public void onResponse(Call<List<Page>> call, Response<List<Page>> response) {
                if(response.isSuccessful()){
                    Log.e("Returned","Pages "+response.body().size());
                    totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                    totalNoOfPages = Integer.parseInt(response.headers().get("x-wp-total"));
                    pageList = response.body();
                    if(pageList==null)
                        return;
                    if(mediaEnabled){
                        mediaQueue = pageList.size();
                        for (int i = 0; i < pageList.size(); i++) {
                            if(pageList.get(i).getFeaturedMedia()!=0){
                                fetchMedia(pageList.get(i).getFeaturedMedia(),i);
                            }else {
                                mediaQueue--;
                                pageList.get(i).setFeaturedMediaUrl(null);
                                checktoproceed();
                            }
                        }
                    }

                }else {
                    listner.onFailed(response.code()+" error");
                }
            }

            @Override
            public void onFailure(Call<List<Page>> call, Throwable t) {
                listner.onFailed(t.getMessage());
            }
        });
    }

    private void fetchMedia(int id, final int index){
        Media media = databaseHandler.getMedia(id);
        if(media==null){
            Call<Media> mediaCall = apiInterface.getMedia(id);
            mediaCall.enqueue(new Callback<Media>() {
                @Override
                public void onResponse(Call<Media> call, Response<Media> response) {
                    if(response.isSuccessful()){
                        mediaQueue--;
                        databaseHandler.addMedia(response.body());
                        pageList.get(index).setFeaturedMediaUrl(response.body().getSourceUrl());
                        Log.e("Source","Media From Server");
                        checktoproceed();
                    }else{
                        mediaQueue--;
                        pageList.get(index).setFeaturedMediaUrl(null);
                        checktoproceed();
                    }
                }

                @Override
                public void onFailure(Call<Media> call, Throwable t) {
                    mediaQueue--;
                    checktoproceed();
                }
            });
        }else{
            Log.e("Source","Media From DB");
            mediaQueue--;
            pageList.get(index).setFeaturedMediaUrl(media.getSourceUrl());
            checktoproceed();
        }

    }

    private void checktoproceed(){
        if(mediaQueue<=0){
            allSuccessful();
        }
    }

    private void allSuccessful(){
        listner.onSuccessful(pageList,totalPages);
    }

}
