package com.app.hitxghbeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class IntroScreen extends WelcomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    protected void setVivasayamLanguage(){
        final SharedPreferences sharedPref = getSharedPreferences(Config.DEF_SHAREF_PREF, Context.MODE_PRIVATE);;
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle("Choose language");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        arrayAdapter.add("English");
        arrayAdapter.add("Tamil");
        arrayAdapter.add("Malayalam");
        arrayAdapter.add("Telugu");
        arrayAdapter.add("Kanndam");
        arrayAdapter.add("Hindi");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                Toast.makeText(getApplicationContext(),strName,Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("language",which);
                editor.apply();
            }
        });
        builderSingle.show();
    }

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("fonts/Quicksand-Medium.ttf")
                .defaultDescriptionTypefacePath("fonts/OpenSans-Regular.ttf")
                .defaultHeaderTypefacePath("fonts/Quicksand-Medium.ttf")
                .page(new BasicPage(R.drawable.hq_icon,
                        "Welcome",
                        "Hello And thank You For Installing HitzGh App On Your Andriod Device")
                        .background(R.color.md_deep_purple_500)
                )
                .page(new BasicPage(R.drawable.ic_layers,
                        "Your Best Source For ",
                         "Latest Music And Music Videos Downloads With Just A Click ")
                        .background(R.color.md_indigo_500)
                )
                .page(new BasicPage(R.drawable.ic_blocks,
                        "Push Notification",
                        "Great News! You Will Automatically Be Notified When New Music/Video is Added")
                        .background(R.color.md_deep_purple_500)
                )
                .page(new BasicPage(R.drawable.ic_brush,
                        "Contact Us On:",
                        "Email: info@hitzgh.com \n Website: www.hitxgh.com \n Phone: +233 245 464 567 \n Whatsapp: +233245464567")
                        .background(R.color.md_indigo_500)
                )
                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
