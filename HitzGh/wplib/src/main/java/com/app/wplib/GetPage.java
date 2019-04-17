package com.app.wplib;

import android.content.Context;


import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.page.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 20/01/18.
 */

public class GetPage {

    public interface onComepleteListner{
        void onSuccess(Page page);
        void onError(String msg);
    }

    private Page page;
    private int id;
    private boolean isMediaFetched;
    private boolean isAuthorFetched;
    private ApiInterface apiInterface;
    private Context context;
    private DatabaseHandler databaseHandler;
    private onComepleteListner onComepleteListner;

    public void setOnComepleteListner(GetPage.onComepleteListner onComepleteListner) {
        this.onComepleteListner = onComepleteListner;
    }

    public GetPage(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void execute(){
        databaseHandler = new DatabaseHandler(context);
        if(id!=0){
            Call<Page> pageCall = apiInterface.getPageById(id);
            pageCall.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {
                    if(response.isSuccessful()){
                        page = response.body();
                        proceed();
                        /*if(page.getFeaturedMedia()!=0) {
                            fetchMedia(page.getFeaturedMedia());
                        }else {
                            proceed();
                        }*/
                    }else {
                        onComepleteListner.onError(response.code()+" Error");
                    }
                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    onComepleteListner.onError(t.getMessage()+" Error");
                }
            });

        }else {
            onComepleteListner.onError("Invalid Post ID :(");
        }
    }

    private void proceed(){
        onComepleteListner.onSuccess(page);
    }

}
