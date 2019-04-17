package com.app.hitxghbeta.webview;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.app.hitxghbeta.ImageSliderActivity;

import java.util.List;

/**
 * Created by anubhav on 06/01/18.
 */

public class CustomWebViewClient extends WebViewClient {

    private Context context;
    private List<String> images;
    private DownloadManager dm;
    long queyeId;

    public CustomWebViewClient(Context context,List<String> img) {
        this.context = context;
        this.images = img;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if(request.getUrl().toString().contains(".jpg")||request.getUrl().toString().contains(".jpg")){
            for (int i=0;i<images.size();i++){
                if (images.get(i).equals(request.getUrl().toString())){
                    //openImages(i);
                }
            }
            return true;
        }else {
            Log.e("Url","Url: "+request.getUrl().toString());
            openWebUrl(request.getUrl().toString());
            return true;
        }
    }

    @Deprecated
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.contains(".jpg")||url.contains(".jpg")){
            for (int i=0;i<images.size();i++){
                if (images.get(i).equals(url)){
                    //openImages(i);
                }
            }
            return true;
        }else {
            openWebUrl(url);
            return true;
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    private void openWebUrl(String url){
//         Toast.makeText(context, "Cool Man"+url, Toast.LENGTH_SHORT).show();
//         DownloadPojoClass downloadPojoClass = new DownloadPojoClass();
//         downloadPojoClass.setUri(url);
//         if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M  && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                 != PackageManager.PERMISSION_GRANTED){
//
//         }
 //       Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        context.startActivity(intent);
//         PostActivity postActivity = new PostActivity();
//         postActivity.DownLoadFile(url);

       try {
           dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
           DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
           queyeId = dm.enqueue(request);
           Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
       }catch(Resources.NotFoundException ex){
           Toast.makeText(context, "File Note Found", Toast.LENGTH_SHORT).show();
        }catch (NullPointerException ex){
           Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show();
       }

    }

    private void openImages(int i){
        Intent intent = new Intent(context,ImageSliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("length",images.size());
        intent.putExtra("scrollto",i);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        for(int j=0;j<images.size();j++)
            intent.putExtra("image"+j,""+images.get(j));
        context.startActivity(intent);
    }


}
