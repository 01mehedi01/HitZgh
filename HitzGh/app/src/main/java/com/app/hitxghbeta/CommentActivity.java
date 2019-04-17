package com.app.hitxghbeta;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hitxghbeta.adapters.CommentItemAdapter;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetComments;
import com.app.wplib.models.comment.Comment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.items.ProgressItem;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.itemanimators.SlideRightAlphaAnimator;

import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CommentActivity extends BaseActivity {

    public static final String ARG_POST = "comment_post";
    public static final String ARG_PAENT = "comment_parent";
    public static final String ARG_ALLOW_COMMENTS = "allow_comments";



    private static String TAG = CommentActivity.class.getSimpleName();
    private WebView mWebViewComments;
    private FrameLayout mContainer;
    private ProgressBar progressBar;
    boolean isLoading;
    private WebView mWebviewPop;
    private String postUrl;

    private static final int NUMBER_OF_COMMENTS = 5;

    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter = new ItemAdapter();
    private ApiInterface apiInterface;
    private GetComments getComments;
    private ItemAdapter<ProgressItem> footerAdapter = new ItemAdapter<>();
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ProgressBar progressBarr;
    private RecyclerView recyclerView;
    private boolean isActive;
    private boolean allowComments;
    private Integer post,parent;
    private int page=1,totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

//        if(getIntent()!=null){
//            post = getIntent().getIntExtra(ARG_POST,0);
//            parent = getIntent().getIntExtra(ARG_PAENT,0);
//            allowComments = getIntent().getBooleanExtra(ARG_ALLOW_COMMENTS,false);
//        }
//        if(0==post){
//            post = null;
//        }
//        toolbar = findViewById(R.id.commentToolbar);
//        toolbarTitle = findViewById(R.id.commentPageTitle);
//        progressBar = findViewById(R.id.commentsProgressBar);
//        recyclerView = findViewById(R.id.commentsRecyclerView);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommentActivity.super.onBackPressed();
//            }
//        });
//        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
//            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
//        }
//        //Prepare for req
//        apiInterface =  ApiClient.getClient().create(ApiInterface.class);
//        getComments = new GetComments(apiInterface,getApplicationContext());
//        getComments.setPost(post);
//        getComments.setParent(parent);
//        getComments.setListner(new GetComments.Listner() {
//            @Override
//            public void onSuccessful(List<Comment> commentList, int totalComments, int totalPage) {
//                toolbarTitle.setText(totalComments+" "+getResources().getString(R.string.comments));
//                totalPages = totalPage;
//                addData(commentList);
//                if(commentList.size()==0){
//                    findViewById(R.id.addCommentBtn).setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
//                    Toasty.info(getApplicationContext(),getResources().getString(R.string.no_comments),Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        //Adapter
//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        footerAdapter = ItemAdapter.items();
//        fastAdapter = FastAdapter.with(Arrays.asList(itemAdapter,footerAdapter));
//        recyclerView.setAdapter(fastAdapter);
//        recyclerView.setItemAnimator(new SlideRightAlphaAnimator());
//        fastAdapter.withSelectable(true);
//        fastAdapter.withEventHook(new CommentItemAdapter.CommentItemClickEvent());
//        sendRequest(page);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebViewComments = (WebView) findViewById(R.id.commentsView);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        postUrl = getIntent().getStringExtra("url");

        // finish the activity in case of empty url
        if (TextUtils.isEmpty(postUrl)) {
            Toast.makeText(getApplicationContext(), "The web url shouldn't be empty", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setLoading(true);
        loadComments();
    }

//    private void sendRequest(int page){
//        getComments.setPage(page);
//        getComments.execute();
//    }
//
//    private void addData(List<Comment> commentList){
//        for(Comment c:commentList){
//            itemAdapter.add(new CommentItemAdapter(getApplicationContext(),c));
//        }
//        proceed();
//    }
//
//    private void proceed(){
//        progressBar.setVisibility(View.GONE);
//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//            @Override
//            public void onLoadMore(int currentPage) {
//                footerAdapter.clear();
//                footerAdapter.add(new ProgressItem().withEnabled(false));
//                if(page<totalPages) {
//                    page++;
//                    Toasty.info(getApplicationContext(),getString(R.string.loading_more_comments),Toast.LENGTH_SHORT).show();
//                    sendRequest(page);
//                }else{
//                    Toasty.success(getApplicationContext(),getResources().getString(R.string.all_comments_loaded),Toast.LENGTH_SHORT).show();
//                    footerAdapter.clear();
//                }
//            }
//        });
//    }
//
//    public void addComment(View view){
//        if(allowComments) {
//            Intent intent = new Intent(getApplicationContext(), AddCommentActivity.class);
//            intent.putExtra(AddCommentActivity.ARG_PARENT, parent);
//            intent.putExtra(AddCommentActivity.ARG_POST, post);
//            startActivity(intent);
//        }else {
//            Toasty.info(getApplicationContext(),"New comments are not allowed on this posts",Toast.LENGTH_SHORT).show();
//        }
//    }

    private void loadComments() {

        mWebViewComments.setWebViewClient(new UriWebViewClient());
        mWebViewComments.setWebChromeClient(new UriChromeClient());
        mWebViewComments.getSettings().setJavaScriptEnabled(true);
        mWebViewComments.getSettings().setAppCacheEnabled(true);
        mWebViewComments.getSettings().setDomStorageEnabled(true);
        mWebViewComments.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebViewComments.getSettings().setSupportMultipleWindows(true);
        mWebViewComments.getSettings().setSupportZoom(false);
        mWebViewComments.getSettings().setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebViewComments.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebViewComments, true);
        }

        // facebook comment widget including the article url
        String html = "<!doctype html> <html lang=\"en\"> <head></head> <body> " +
                "<div id=\"fb-root\"></div> <script>(function(d, s, id) { var js, fjs = d.getElementsByTagName(s)[0]; if (d.getElementById(id)) return; js = d.createElement(s); js.id = id; js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.6\"; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'facebook-jssdk'));</script> " +
                "<div class=\"fb-comments\" data-href=\"" + postUrl + "\" " +
                "data-numposts=\"" + NUMBER_OF_COMMENTS + "\" data-order-by=\"reverse_time\">" +
                "</div> </body> </html>";

        mWebViewComments.loadDataWithBaseURL("http://www.nothing.com", html, "text/html", "UTF-8", null);
        mWebViewComments.setMinimumHeight(200);
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;

        if (isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

        invalidateOptionsMenu();
    }


    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String host = Uri.parse(url).getHost();

            return !host.equals("m.facebook.com");

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String host = Uri.parse(url).getHost();
            setLoading(false);
            if (url.contains("/plugins/close_popup.php?reload")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        mContainer.removeView(mWebviewPop);
                        loadComments();
                    }
                }, 600);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            setLoading(false);
        }

    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(getApplicationContext());
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.setWebChromeClient(this);
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setDomStorageEnabled(true);
            mWebviewPop.getSettings().setSupportZoom(false);
            mWebviewPop.getSettings().setBuiltInZoomControls(false);
            mWebviewPop.getSettings().setSupportMultipleWindows(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.i(TAG, "onConsoleMessage: " + cm.message());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isLoading) {
            getMenuInflater().inflate(R.menu.comment_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.action_refresh) {
            mWebViewComments.reload();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //isActive=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //isActive=true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
