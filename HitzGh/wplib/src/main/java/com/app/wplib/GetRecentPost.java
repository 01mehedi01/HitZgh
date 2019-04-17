package com.app.wplib;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anubhav on 24/11/17 for personal use only.
 * Not permitted to use without permission
 */

public class GetRecentPost {

    private static String CONTEXT_EMBED = "embed";

    public interface Listner{
        void onSuccessful(List<Post> postList,int totalPosts,int totalPages);

        void onError(String msg);
    }

    public GetRecentPost(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    private DatabaseHandler databaseHandler;
    private ApiInterface apiInterface;
    private int page;
    private int totalPages;
    private int totalPosts;
    private Integer perPage;
    private boolean mediaEnabled;
    private boolean categoryEnabled;
    private String category;
    private String search;
    private Listner listner;
    private Context context;
    private String excluded;
    private boolean pluginInstalled;

    public void setPluginInstalled(boolean pluginInstalled) {
        this.pluginInstalled = pluginInstalled;
    }

    public void setMediaEnabled(boolean mediaEnabled) {
        this.mediaEnabled = mediaEnabled;
    }

    public void setCategoryEnabled(boolean categoryEnabled) {
        this.categoryEnabled = categoryEnabled;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setOnCompleteListner(Listner listner) {
        this.listner = listner;
    }

    public void setExcluded(String excluded) {
        this.excluded = excluded;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        if(!pluginInstalled)
            CONTEXT_EMBED = null;
        final Call<List<Post>> posts = apiInterface.getPostList(page,category,search,perPage,CONTEXT_EMBED,excluded);
        Log.e("Making Request",pluginInstalled+" To: "+posts.request().url());
        posts.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(pluginInstalled&&response.body()!=null) {
                    totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                    totalPosts = Integer.parseInt(response.headers().get("x-wp-total"));
                    Log.e("Total Posts",""+totalPosts);
                    for (Post p:response.body()){
                        if(p.getBetterFeaturedImage()!=null){
                            if(p.getBetterFeaturedImage().getMediumLarge()!=null){
                                p.getBetterFeaturedImage().setPostThumbnail(p.getBetterFeaturedImage().getMediumLarge());
                            }
                        }
                    }
                    //new CompleteTask().execute(response);
                    //checktoproceed(response.body());
                   // Toast.makeText(context, "Total page "+totalPages+"post "+totalPosts, Toast.LENGTH_SHORT).show();
                    listner.onSuccessful(response.body(),totalPosts,totalPages);

                }else if(response.isSuccessful()){
                    totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                    totalPosts = Integer.parseInt(response.headers().get("x-wp-total"));
                    if(response.body()==null){
                        return;
                    }
                    if(pluginInstalled){
                        //checktoproceed(response.body());
                    }else {
                        Log.e("WPLIB","Plugin Not Installed");
                    }
                    //checktoproceed();
                }else{
                    listner.onError("Unknown Error");
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listner.onError(t.getMessage());
            }
        });
    }

    private void allSuccessful(List<Post> list){
        Log.e("GetRecentPosts","Invoked");
        listner.onSuccessful(list,totalPosts,totalPages);
    }

    /*private class CompleteTask extends AsyncTask<Response<List<Post>>, Integer, Void> {

        protected Void doInBackground(Response<List<Post>>... responses) {
            Response<List<Post>> response = responses[0];
            if(response.isSuccessful()&&response.body()!=null){
                totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                totalPosts = Integer.parseInt(response.headers().get("x-wp-total"));
                Log.e("Total Posts",""+totalPosts);
                for (Post p:response.body()){
                    if(p.getBetterFeaturedImage()!=null){
                        if(p.getBetterFeaturedImage().getMediumLarge()!=null){
                            p.getBetterFeaturedImage().setPostThumbnail(p.getBetterFeaturedImage().getMediumLarge());
                        }
                    }
                }
                if(postList==null){
                    return null;
                }
            }
            return null;
        }
        //onPostExecute os getting executed before requests
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("Task","TaskCompleted");
            checktoproceed();
        }
    }*/


}
