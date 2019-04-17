package com.app.wplib;

import android.content.Context;
import android.util.Log;


import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.author.Author;
import com.app.wplib.models.media.Media;
import com.app.wplib.models.post.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 14/12/17.
 */

public class GetPost {
    private Post post;
    private ApiInterface apiInterface;
    private DatabaseHandler databaseHandler;
    private Context context;
    private Integer postId;
    private String password;
    private String slug;
    private boolean isMediaFetched,isAuthorFetched;
    private Listner listner;

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    public interface Listner{
        void onSuccess(Post post);
        void onError(String msg);
    }

    public GetPost(ApiInterface apiInterface, Context context){
        this.apiInterface = apiInterface;
        this.context = context;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        if(postId==0){
            Call<List<Post>> call = apiInterface.getPostBySlug(slug,password);
            Log.e("Request URL","URL: "+call.request().url());
            call.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if(response.isSuccessful()){
                        if(response.body()!=null&&response.body().size()>0) {
                            post = response.body().get(0);
                            checkToProceed();
                        }else {
                            listner.onError("");
                        }
                        //Improving the post loading speed at the price of site without using the wordroid plugin.
                        /*if(post.getFeaturedMedia()!=0)
                            fetchMedia(post.getFeaturedMedia());*/
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    listner.onError("");
                }
            });
        }else {
            Call<Post> call = apiInterface.getPost(postId, slug, password);
            Log.e("Request URL","URL: "+call.request().url());
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if(response.isSuccessful()&&response.body()!=null){
                        post = response.body();
                        checkToProceed();
                        //Improving the post loading speed at the price of site without using the wordroid plugin.
                        /*if(post.getFeaturedMedia()!=0)
                            fetchMedia(post.getFeaturedMedia());
*/
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    listner.onError("");
                }
            });
        }
    }

    private void fetchMedia(int id){
        Media media = databaseHandler.getMedia(id);
        if(media==null){
            Call<Media> mediaCall = apiInterface.getMedia(id);
            mediaCall.enqueue(new Callback<Media>() {
                @Override
                public void onResponse(Call<Media> call, Response<Media> response) {
                    if(response.isSuccessful()){
                        databaseHandler.addMedia(response.body());
                        post.setFeat_media_url(response.body().getSourceUrl());
                        Log.e("Source","Media From Server");
                        isMediaFetched=true;
                        fetchAuthor(post.getAuthor());
                    }
                }

                @Override
                public void onFailure(Call<Media> call, Throwable t) {
                    Log.e("GetPost","Request Failed: "+t.getMessage());
                }
            });
        }else{
            isMediaFetched = true;
            post.setFeat_media_url(media.getSourceUrl());
            Log.e("Source","Media From DB");
            fetchAuthor(1);
        }

    }

    private void fetchAuthor(int id){
        Call<Author> authorCall = apiInterface.getAuthor(id);
        Log.e("Request URL","URL: "+authorCall.request().url());
        authorCall.enqueue(new Callback<Author>() {
            @Override
            public void onResponse(Call<Author> call, Response<Author> response) {
                if(response.isSuccessful()){
                    isAuthorFetched=true;
                    post.setPostAuthor(response.body());
                    checkToProceed();
                }
            }

            @Override
            public void onFailure(Call<Author> call, Throwable t) {

            }
        });
    }

    private void checkToProceed(){
        listner.onSuccess(post);
        post=null;
    }
}
