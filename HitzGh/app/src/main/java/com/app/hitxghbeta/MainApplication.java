package com.app.hitxghbeta;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Anubhav on 02-08-2017.
 */

public class MainApplication extends Application {

    public static List<Integer> POST_IDS = new ArrayList<>();
    public static boolean isFromMainActivity = true;

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification osNotification) {
                        JSONObject data = osNotification.payload.additionalData;
                        int post = 0;
                        if(data!=null){
                            Log.e("Print",data.toString());
                        }
                    }
                })
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenResult result) {
                        OSNotificationAction.ActionType actionType = result.action.type;
                        JSONObject data = result.notification.payload.additionalData;
                        String type;


                        if (data != null) {
                            type = data.optString("type");
                            switch (type) {
                                case "post": {
                                    int postId = data.optInt("value");
                                    Log.i("OneSignalExample", "customkey set with value: " + postId);
                                    Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                                    intent.putExtra(PostActivity.POST_ID_STRING, postId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                }
                                case "web": {
                                    String url = data.optString("value");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.android.chrome");
                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException ex) {
                                        intent.setPackage(null);
                                        startActivity(intent);
                                    }
                                    break;
                                }
                                case "message": {
                                    String title, message;
                                    title = data.optString("title");
                                    message = data.optString("message");
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    if (Config.defaultHomeScreen == 1){
                                        intent = new Intent(getApplicationContext(), MainActivity.class);
                                    }else if(Config.defaultHomeScreen==2){
                                        intent = new Intent(getApplicationContext(),CarouselPostListActivity.class);
                                    }else if(Config.defaultHomeScreen==3){
                                        intent = new Intent(getApplicationContext(),ViewPagerActivity.class);
                                    }
                                    intent.putExtra(MainActivity.SHOW_DIALOG, true);
                                    intent.putExtra(MainActivity.DIALOG_TITLE, title);
                                    intent.putExtra(MainActivity.DIALOG_MSG, message);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    break;
                                }
                                default:
                                    Toast.makeText(getApplicationContext(),"None Selected",Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (actionType == OSNotificationAction.ActionType.ActionTaken)
                            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

                    }
                })
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //Changing app language below Android Oreo
        if(Config.enableRTL) {
            Locale locale = new Locale("ar");
            Locale.setDefault(locale);

            Configuration config = getApplicationContext().getResources().getConfiguration();
            config.setLocale(locale);
            getApplicationContext().createConfigurationContext(config);
            if (Build.VERSION.SDK_INT >= 21) {
                getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics());
            }
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/RobotoCondensed-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
