package com.app.wplib;

import android.content.Context;
import android.util.Log;

import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.comment.Comment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 28/11/17.
 */

public class GetComments {

    public interface Listner{
        void onSuccessful(List<Comment> commentList, int totalComments,int totalPage);
    }

    public GetComments(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    private Listner listner;
    private List<Comment> commentList = new ArrayList<>();
    private List<Comment> replyList = new ArrayList<>();
    private List<Comment> tempList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private ApiInterface apiInterface;
    private int currentPage;
    private int commentQueue;
    private int totalPages;
    private int totalComments;
    private Context context;
    private int post;
    private int page;
    private Integer parent;
    private String search;
    private String orderBy;
    private String contextEmbed = "embed";

    public void setListner(Listner listner) {
        this.listner = listner;
    }

    public void setApiInterface(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setParent(Integer parent){ this.parent = parent; }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        Call<List<Comment>> call = apiInterface.getCommentByPostId(post,parent,page,search,orderBy);
        Log.e("Making Request","To: "+call.request().url());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()){
                    commentQueue = response.body().size();
                    if(response.body().size()>0) {
                        commentList.clear();
                        replyList.clear();
                        totalPages = Integer.parseInt(response.headers().get("x-wp-totalpages"));
                        totalComments = Integer.parseInt(response.headers().get("x-wp-total"));
                        if (response.body() != null) {
                            commentList.addAll(response.body());
                        }
                        for (int i = 0; i < commentList.size(); i++) {
                            getRepliesInformation(commentList.get(i).getId(), i);
                        }
                    }else{
                        checkIfSuccessful();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });
    }

    private void getRepliesInformation(int thisParent, final int position){
        Call<List<Comment>> gets = apiInterface.getCommentByPostId(post,thisParent,page,search,orderBy);
        gets.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                commentQueue--;
                if(response.isSuccessful()){
                    int x = Integer.parseInt(response.headers().get("x-wp-total"));
                    commentList.get(position).setChild(x);
                    checkIfSuccessful();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                commentQueue--;
                checkIfSuccessful();
            }
        });
    }

    private void checkIfSuccessful(){
        if(commentQueue==0){
            listner.onSuccessful(commentList,totalComments,totalPages);
        }
    }
}
