package com.app.hitxghbeta;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * Created by anubhav on 21/01/18.
 */

public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();
        if(data!=null) {
            getSlug(data);
            finish();
        }else {
            Toasty.error(getApplicationContext(),"Invalid URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getUrlSlug(Uri url){
        String link = url.toString();
    }

    private void getSlug(Uri url){
        String link = url.toString();
        if(url.toString().equals(Config.siteUrl)||url.toString().equals(Config.siteUrl+"/")){
            Intent intent = new Intent(this,MainActivity.class);
            if(Config.defaultHomeScreen==2){
                intent = new Intent(this,CarouselPostListActivity.class);
            }else if(Config.defaultHomeScreen==3){
                intent = new Intent(this,ViewPagerActivity.class);
            }
            startActivity(intent);
        }else if(url.getQueryParameter("p")!=null){
            String id = url.getQueryParameter("p");
            if(id!=null) {
                Intent intent = new Intent(DeepLinkActivity.this, PostActivity.class);
                intent.putExtra(PostActivity.POST_URL_STRING, link);
                intent.putExtra(PostActivity.POST_ID_STRING, id);
                startActivity(intent);
            }
        }else if(url.toString().contains("/category/")){
            if(url.getLastPathSegment()!=null){
                Intent intent = new Intent(DeepLinkActivity.this,CategoryActivity.class);
                intent.putExtra(CategoryActivity.ARG_SLUG,url.getLastPathSegment());
                startActivity(intent);
                Toasty.success(getApplicationContext(),"Loading Category: "+url.getLastPathSegment(),Toast.LENGTH_SHORT).show();
            }
        }else{
            String slug = url.getLastPathSegment();
            if(url.toString().contains(".html")){
                Intent intent = new Intent(DeepLinkActivity.this,PostActivity.class);
                intent.putExtra(PostActivity.POST_URL_STRING,link);
                intent.putExtra(PostActivity.POST_SLUG,slug.replace(".html",""));
                startActivity(intent);
            }else if(url.toString().matches("-?\\d+")){
                Toast.makeText(getApplicationContext(),"1st",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeepLinkActivity.this,PostActivity.class);
                intent.putExtra(PostActivity.POST_URL_STRING,link);
                intent.putExtra(PostActivity.POST_ID_STRING,slug);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"2nd",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeepLinkActivity.this,PostActivity.class);
                intent.putExtra(PostActivity.POST_URL_STRING,link);
                intent.putExtra(PostActivity.POST_ID_STRING,slug);
                startActivity(intent);
            }
        }
    }
}
