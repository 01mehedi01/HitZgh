package com.app.hitxghbeta;

import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


public class WebViewActivity extends AppCompatActivity {

    String url;
    private TextView titleView,textViewUrl;
    private String downloadUrl,type,disposition;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if(getIntent()!=null){
            url = getIntent().getStringExtra("URL");
        }

        Toolbar toolbar = findViewById(R.id.webviewToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        titleView = findViewById(R.id.webviewTitle);
        textViewUrl = findViewById(R.id.webviewUrl);
        textViewUrl.setText(url);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());

        webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimetype,
                                        long contentLength) {
                downloadUrl = url;
                disposition = contentDisposition;
                type = mimetype;
                //askPermission();
              //  Toast.makeText(WebViewActivity.this, "Cool", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    } else {
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(url));
                        String filename= URLUtil.guessFileName(downloadUrl, disposition, type);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toasty.success(getApplicationContext(), "Downloading "+filename, //To notify the Client that the file is being downloaded
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    String filename= URLUtil.guessFileName(downloadUrl, disposition, type);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toasty.success(getApplicationContext(), "Downloading "+filename, //To notify the Client that the file is being downloaded
                            Toast.LENGTH_LONG).show();
                }



            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    titleView.setText(title);
                    titleView.setSelected(true);
                }
            }

        });


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toasty.info(getApplicationContext(),"Click downlaod link again to start downloading",Toast.LENGTH_SHORT).show();
                } else {

                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            textViewUrl.setText(url);
            view.loadUrl(url);
            return true;
        }


    }
}
