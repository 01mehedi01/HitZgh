package com.app.hitxghbeta;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.util.Util;

public class ContactUSActivity extends AppCompatActivity {
    private  Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);


        toolbar = findViewById(R.id.contactus_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void CallUsButtonPress(View view) {
       // Toast.makeText(this, "CallUsButtonPress", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+233245464567"));
        startActivity(intent);
    }


    public void EmailUsButtonPress(View view) {
        //Toast.makeText(this, "EmailUsButtonPress", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","email@email.com", null));
        try {
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        }catch (Resources.NotFoundException exc){
            Toast.makeText(this, " Not found", Toast.LENGTH_SHORT)
                    .show();
        }catch(ActivityNotFoundException exception){
            Toast.makeText(this, " Not found", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void WhatsappButtonPress(View view) {

        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setComponent(new  ComponentName("com.whatsapp","com.whatsapp.Conversation"));
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("233245464567")+"@s.whatsapp.net");
        startActivity(sendIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.exit){
            finish();
            moveTaskToBack(true);
        }
      return   super.onOptionsItemSelected(item);
    }
}
