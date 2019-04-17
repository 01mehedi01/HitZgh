package com.app.hitxghbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.wplib.ApiInterface;
import com.app.wplib.GetSettings;
import com.app.wplib.models.Settings;
import com.stephentuso.welcome.WelcomeHelper;

import es.dmoral.toasty.Toasty;

/**
 * Created by anubhav on 16/01/18.
 */

public class SplashActivity extends AppCompatActivity {

    private static final String INTRO_SCREEN_CODE = "intro_screen";
    private static final int REQUEST_WELCOME_SCREEN_RESULT = 13;
    WelcomeHelper welcomeScreen;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    boolean show,settingsSaved,screenLaunched;
    private Settings userSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences(Config.DEF_SHAREF_PREF,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        settingsSaved = sharedPref.getBoolean(Config.IS_SETTINGS_SAVED,false);

        if(settingsSaved)
            goToMainActivity();
        else {
            getSettings();
        }
        if(Config.enableIntroSlider) {
            show = sharedPref.getBoolean(INTRO_SCREEN_CODE,false);
            welcomeScreen = new WelcomeHelper(this, IntroScreen.class);
            welcomeScreen.show(savedInstanceState, REQUEST_WELCOME_SCREEN_RESULT);
        }
    }

    private void getSettings(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetSettings getSettings = new GetSettings(apiInterface,getApplicationContext());
        getSettings.setListner(new GetSettings.onComplete() {
            @Override
            public void onSuccess(Settings settings) {
                userSettings = settings;
                saveSettings();
                Log.e("SplashActivity","Settings successful");
            }

            @Override
            public void onError(String msg) {
                if (sharedPref.getBoolean(Config.IS_SETTINGS_SAVED,false)){
                    goToMainActivity();
                }else {
                    Toasty.error(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
            }
        });
        getSettings.execute();
    }

    private void saveSettings(){
        editor.putString(Config.APP_NAME,userSettings.getAppTitle());
        editor.putInt(Config.APP_VERSION,userSettings.getAppVersion());
        editor.putBoolean(Config.IS_FORCE_UPDATE,userSettings.isForceUpdate());
        editor.putString(Config.TOOLBAR_COLOR,userSettings.getToolbarColor());
        editor.putInt(Config.NO_OF_SECTIONS,userSettings.getSections().size());
        editor.putString(Config.SLIDER_CATEGORY,userSettings.getSliderCategory());
        editor.putBoolean(Config.IS_SLIDER_ENABLED,userSettings.isSliderEnabled());
        editor.putBoolean(INTRO_SCREEN_CODE,false);
        editor.putString("update_title",userSettings.getUpdateTitle());
        editor.putString("update_body",userSettings.getUpdateBody());
        for (int i=0;i<userSettings.getSections().size();i++){
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_TITLE,userSettings.getSections().get(i).getTitle());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_SMALL_IMAGE_URL,userSettings.getSections().get(i).getImage());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_CATEGORY,userSettings.getSections().get(i).getCategoryId());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_TYPE,userSettings.getSections().get(i).getType());
            editor.putString(Config.SECTION_PREFIX+i+Config.SECTION_COUNT,userSettings.getSections().get(i).getCount());
        }
        editor.putBoolean(Config.IS_SETTINGS_SAVED,true);
        editor.apply();
        if(show) {
            goToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_WELCOME_SCREEN_RESULT) {

            if (resultCode == RESULT_OK) {
                editor.putBoolean(INTRO_SCREEN_CODE,true);
                goToMainActivity();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Canceled (RESULT_CANCELED)", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void goToMainActivity(){
        if(!screenLaunched) {
            Intent intent = new Intent(this, MainActivity.class);
            if (Config.defaultHomeScreen == 2) {
                intent = new Intent(this, CarouselPostListActivity.class);
            } else if (Config.defaultHomeScreen == 3) {
                intent = new Intent(this, ViewPagerActivity.class);
            }
            screenLaunched=true;

            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(Config.enableIntroSlider) {
            welcomeScreen.onSaveInstanceState(outState);
        }
    }

}
