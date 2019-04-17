package com.app.hitxghbeta.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.app.hitxghbeta.ImageSliderActivity;
import com.app.hitxghbeta.PostActivity;

import java.util.List;

/**
 * Created by Ankit on 10-03-2017.
 */

public class WebAppInterface {
    private Context mContext;
    private WebView webView;
    List<String> img;
    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c, WebView webView, List<String> imgs) {
        mContext = c;
        this.webView = webView;
        this.img = imgs;
    }

    @JavascriptInterface
    public void siteUrlClicked(String url){
        String slug = getSlugFromUrl(url);
        Intent intent = new Intent(mContext,PostActivity.class);
        intent.putExtra(PostActivity.POST_URL_STRING,url);
        intent.putExtra(PostActivity.POST_SLUG,slug);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void imageClicked(String image,int index){
        Intent intent = new Intent(mContext,ImageSliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("length",img.size());
        intent.putExtra("scrollto",index);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for(int j=0;j<img.size();j++)
            intent.putExtra("image"+j,""+img.get(j));
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void youtubeUrl(String url){
        Toast.makeText(mContext,"Url: "+url,Toast.LENGTH_SHORT).show();
    }

    private String getSlugFromUrl(String url){
        Uri uri = Uri.parse(url);
        return uri.getLastPathSegment();
    }
}
