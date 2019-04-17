package com.app.hitxghbeta;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.app.hitxghbeta.fragments.HorizontalPostsFragment;
import com.app.hitxghbeta.webview.CustomWebViewClient;
import com.app.hitxghbeta.webview.VideoEnabledWebChromeClient;
import com.app.hitxghbeta.webview.VideoEnabledWebView;
import com.app.hitxghbeta.webview.WebAppInterface;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetPost;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

import static com.app.hitxghbeta.PostActivity.POST_ID_STRING;

public class PostFragment extends Fragment implements TextToSpeech.OnInitListener{

    private int postId;

    private boolean isActive;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private ImageView imageView;
    private YouTubePlayer ytPlayer;
    private VideoEnabledWebView postWebview;
    private VideoEnabledWebChromeClient webChromeClient;
    private String url,videoId,allCategories;
    private Post thisPost;
    private AdView mAdView;
    private TextToSpeech tts;
    private ProgressBar progressBar;
    private Button commentBtn,shareBtn;
    private TextView titleView,dateView,relatedPostTitleView;
    private YouTubePlayer youTubePlayer;
    View nonVideoLayout;
    ViewGroup videoLayout;
    List<String> images = new ArrayList<>();


    private DatabaseHandler databaseHandler;
    private ExecutorService executorService;
    private HashMap<String, Future<?>> queue = new HashMap<String, Future<?>>();
    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(int id) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putInt(POST_ID_STRING, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(POST_ID_STRING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        tts = new TextToSpeech(getActivity(),this);
        databaseHandler = new DatabaseHandler(getActivity());
        nonVideoLayout = view.findViewById(R.id.nonVideoLayout);
        videoLayout = view.findViewById(R.id.videoLayout);
        mAdView = view.findViewById(R.id.postAdView);
        progressBar = view.findViewById(R.id.pd_progressBar);
        titleView = view.findViewById(R.id.postTitle);
        dateView = view.findViewById(R.id.postDate);
        imageView = view.findViewById(R.id.postImg);
        postWebview = view.findViewById(R.id.postWebview);
        commentBtn = view.findViewById(R.id.commentBtn);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComments(v);
            }
        });
        shareBtn = view.findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initShare(v);
            }
        });

        postWebview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        postWebview.setLongClickable(false);
        postWebview.setHapticFeedbackEnabled(false);
        relatedPostTitleView = view.findViewById(R.id.relatedPostsTitle);
        fetchPost();
        return view;
    }

    private void setupYouTubePlayer(final String videoId){
        imageView.setVisibility(View.GONE);
        youTubePlayerFragment = new YouTubePlayerSupportFragment();
        youTubePlayerFragment.initialize(getResources().getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                ytPlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoId);
                youTubePlayer.play();
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_youtube_player, youTubePlayerFragment);
        fragmentTransaction.commit();
    }

    private void fetchPost(){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            GetPost getPost = new GetPost(apiInterface,getActivity());
            getPost.setPostId(postId);
            getPost.setListner(new GetPost.Listner() {
                @Override
                public void onSuccess(Post post) {
                    PostViewPagerActivity.thisPost = post;
                    thisPost = post;
                    url = thisPost.getLink();
                    allCategories = thisPost.getCategories().toString();
                    Log.e("Categories","Cats: "+allCategories);
                    allCategories = allCategories.substring(1,allCategories.length()-1);
                    proceed(post);
                }

                @Override
                public void onError(String msg) {
                    Toasty.error(getActivity(),getResources().getString(R.string.error_loading_post),Toast.LENGTH_SHORT).show();
                }
            });
            getPost.execute();
    }

    private void proceed(Post post){
        progressBar.setVisibility(View.GONE);
        titleView.setText(post.getTitle().getRendered());
        String metaString = "Posted by "+thisPost.getAuthorName()+" on "+post.getDate().substring(0,post.getDate().indexOf("T"));
        dateView.setText(metaString);
        if(post.getBetterFeaturedImage()!=null)
            path = post.getBetterFeaturedImage().getPostThumbnail();
        else
            path = null;
        if(getActivity()!=null) {
            Glide.with(getActivity())
                    .load(path)
                    .placeholder(R.color.md_green_100)
                    .error(R.drawable.no_image_placeholder)
                    .into(imageView);
        }
        String additionalCss = "";
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            additionalCss = "*{color:#FFFFFF!important}";
        }
        String content = "<link rel=\"stylesheet\" href=\"defaultstyles.css\" /><link rel=\"stylesheet\" href=\"styles.css\" /><style>"+additionalCss+"</style>"+post.getContent().getRendered()+"<script src=\"script.js\"></script>";
        Document doc = Jsoup.parse(content);
        Elements imgs = doc.select("img");
        int x=0;
        for (Element e:imgs){
            images.add(e.attr("src"));
            e.attr("onclick","imageClicked('"+e.attr("src")+"',"+x+")");
            x++;
        }
        Elements elements = doc.select("a[href^=\""+Config.siteUrl+"\"]");
        for(Element e:elements){
            e.attr("onclick","siteUrlClicked('"+e.attr("href")+"')");
            e.attr("href","#");
            Log.e("Tag","Tag: "+e.html());
        }
        Elements images = doc.select("img[srcset]");
        for (Element img: images){
            img.removeAttr("srcset");
            Log.e("Tag","Tag; "+img.toString());
        }
        Elements iframes = doc.select("iframe");
        if(iframes.size()>0) {
            Log.e("Iframe","Size: "+iframes.size());
            String tag = iframes.first().toString();
            String url0 = tag.substring(tag.indexOf("src=\"") + 5, tag.length());
            videoId = url0.substring(0, url0.indexOf("\" "));
            getYouTubeId(videoId);
        }
        writewebview(doc.html());
        /*if(linkMode){
            new MyTask().execute(url);
        }else {
            writewebview(doc.html());
        }*/
    }

    public void savePostOffline(){
        if(thisPost!=null&&isActive) {
            databaseHandler.addOfflinePost(thisPost);
            Toasty.success(getActivity(),getResources().getString(R.string.post_saved_successfully),Toast.LENGTH_SHORT).show();
        }else {
            Toasty.error(getActivity(),"Error saving the post",Toast.LENGTH_SHORT).show();
        }
    }

    private void getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            //initializeYoutubePlayer(matcher.group());
            //setupYouTubePlayer(matcher.group());
        }
    }

    private void writewebview(String html){
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, postWebview) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                    videoLayout.setVisibility(View.VISIBLE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    videoLayout.setVisibility(View.GONE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }
        });
        postWebview.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        postWebview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        postWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        postWebview.getSettings().setDomStorageEnabled(true);
        postWebview.setWebChromeClient(webChromeClient);
        postWebview.setWebViewClient(new CustomWebViewClient(getActivity(),images));
        postWebview.addJavascriptInterface(new WebAppInterface(getActivity(),postWebview,images), "Android");
        postWebview.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
        if(isActive) {
            relatedPostTitleView.setVisibility(View.VISIBLE);
            replaceFragment(R.id.relatedPosts, new HorizontalPostsFragment().newInstance(false, false, allCategories, null,thisPost.getId()+""), null, null);
        }
    }

    public void openComments(View view){
        if(thisPost!=null) {
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            if (thisPost != null) {
                intent.putExtra(CommentActivity.ARG_POST, thisPost.getId());
                if (thisPost.getCommentStatus().equals("open")) {
                    intent.putExtra(CommentActivity.ARG_ALLOW_COMMENTS, true);
                } else {
                    intent.putExtra(CommentActivity.ARG_ALLOW_COMMENTS, false);
                }
                startActivity(intent);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        postWebview.onPause();
        isActive=false;
        if (mAdView != null) {
            mAdView.pause();
        }
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(postWebview, (Object[]) null);
            if(tts.isSpeaking())
                tts.shutdown();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch(NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=true;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    public void initShare(View view){
        if(thisPost!=null){
            path = thisPost.getBetterFeaturedImage().getMediumLarge();
            choice=1;
            askPermission();
        }
    }

    String path;
    int choice = 0;
    private static final int REQUEST_WRITE_STORAGE = 112;

    public void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            } else {
                // GetVendorList();
                downloadimage(path, choice);
                //imgDownload(path);
            }
        } else {
            //imgDownload(path);
            //GetVendorList();
            downloadimage(path, choice);
        }
    }

    public void downloadimage(String url, int flag) {
        Toasty.info(getActivity(),getResources().getString(R.string.wait_while_image_is_downloading), Toast.LENGTH_LONG).show();
        int notificationid = url.hashCode();
        String saveTopath = getPhotoPath(getActivity());
        File storePath = new File(saveTopath, notificationid + ".jpeg");
        if (storePath.exists()) {
            onDownloadImageSuccess(storePath.getPath(), flag);
        } else {
            executorService = Executors.newFixedThreadPool(5);
            DownloadLoader dm = new DownloadLoader(url, storePath.getPath(),
                    flag, notificationid);
            Future<?> future = executorService.submit(dm);
            queue.put(url, future);
        }
    }

    public static String getPhotoPath(Context context) {


        File folder = new File(Environment.getExternalStorageDirectory(),
                context.getResources().getString(R.string.app_name));

        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder.getPath();
    }

    private class DownloadLoader implements Runnable {
        private String urlPath;
        private String storePath;
        private FileOutputStream fileOutput = null;
        private HttpURLConnection urlConnection = null;
        private InputStream inputStream = null;
        private boolean isComplete;
        private File file;
        int flag = 0;

        public DownloadLoader(String path, String storePath, int flag,
                              int notificationid) {

            this.flag = flag;
            this.urlPath = path;
            this.storePath = storePath;

        }

        public void run() {
            try {
                file = new File(storePath);
                URL url = new URL(urlPath);
                urlConnection = (HttpURLConnection) url.openConnection();
                file.createNewFile();
                urlConnection.connect();
                fileOutput = new FileOutputStream(file);
                inputStream = urlConnection.getInputStream();
                long totalSize = urlConnection.getContentLength();

                int downloadedSize = 0;
                byte[] buffer = new byte[50 * 1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    final int prog = (int) ((downloadedSize * 100) / totalSize);
                }
                if (totalSize == file.length()) {
                    isComplete = true;
                    onDownloadImageSuccess(file.getPath(), flag);
                } else {
                    isComplete = false;
                    onDownloadImageFailed();
                }

            } catch (Exception e) {
                e.printStackTrace();
                isComplete = false;
                onDownloadImageFailed();
            } finally {
                try {
                    if (fileOutput != null)
                        fileOutput.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
        }
    }

    public void onDownloadImageSuccess(final String path, final int flag) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Uri imageUri = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(path));
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(imageUri);
                getActivity().sendBroadcast(intent);

                if (flag == 1) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    imageUri = FileProvider.getUriForFile(getActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            new File(path));
                    share.putExtra(Intent.EXTRA_STREAM, imageUri);
                    share.putExtra(Intent.EXTRA_TEXT,thisPost.getTitle().getRendered()+"\n"+thisPost.getLink());
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, getResources().getString(R.string.share_via_text)));
                }

            }
        });

    }

    public void onDownloadImageFailed() {
        Toasty.error(getActivity(),getResources().getString(R.string.image_downloading_failed),Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.e("TTS", "Initilize success!");
            int result = tts.setLanguage(Locale.getDefault());
            tts.setPitch(0.7f);
            tts.setSpeechRate(1f);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                Toasty.error(getActivity(),"Language not supported",Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void speakOut() {
        if(thisPost!=null) {
            String content = thisPost.getContent().getRendered();
            if (youTubePlayer == null || !youTubePlayer.isPlaying()) {
                String text = Jsoup.parse(content).text();
                if (text != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ttsGreater21(text);
                    } else {
                        ttsUnder20(text);
                    }
                    Toasty.info(getActivity(), getString(R.string.read_out_loud_text), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toasty.info(getActivity(), getString(R.string.pause_youtube), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
