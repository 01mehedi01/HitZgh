package com.app.wplib;

import android.content.Context;
import android.util.Log;

import com.app.wplib.models.Settings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anubhav on 23/01/18.
 */

public class GetSettings {

    public interface onComplete{
        void onSuccess(Settings settings);
        void onError(String msg);
    }

    private onComplete listner;
    private ApiInterface apiInterface;
    private Context context;
    private Settings settings;

    public void setListner(onComplete listner) {
        this.listner = listner;
    }

    public GetSettings(ApiInterface apiInterface, Context context) {
        this.apiInterface = apiInterface;
        this.context = context;
    }

    public void execute(){
        Call<Settings> settingsCall = apiInterface.getSettings();
        Log.e("Making Request","To: "+settingsCall.request().url());
        settingsCall.enqueue(new Callback<Settings>() {
            @Override
            public void onResponse(Call<Settings> call, Response<Settings> response) {
                if (response.isSuccessful()) {
                    settings = response.body();
                    Log.e("Update","Update Title: "+settings.getUpdateTitle());
                    if(settings!=null) {
                        listner.onSuccess(settings);
                    }
                }else {
                    listner.onError("Response Code: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<Settings> call, Throwable t) {
                Log.e("Error","Mesg: "+t.getMessage());
                listner.onError("Error retrieving settings: ");
            }
        });
    }
}
