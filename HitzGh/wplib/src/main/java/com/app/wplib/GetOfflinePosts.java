package com.app.wplib;

import android.content.Context;

import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.media.Media;
import com.app.wplib.models.post.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 29/11/17.
 */

public class GetOfflinePosts {
    List<Post> postList = new ArrayList<>();
    List<Media> mediaList = new ArrayList<>();
    private Context context;
    private DatabaseHandler databaseHandler;

    public GetOfflinePosts(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
    }

    public List<Post> byCategory(String categoryIds){

        return postList;
    }

    public List<Post> recent(){
        //postList = databaseHandler.getAllPosts();
        return postList;
    }


}
