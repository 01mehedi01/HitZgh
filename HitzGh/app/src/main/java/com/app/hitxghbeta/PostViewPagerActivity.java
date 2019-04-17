package com.app.hitxghbeta;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.app.hitxghbeta.adapters.PostViewPagerAdapter;
import com.app.hitxghbeta.others.FlipPageViewTransformer;
import com.app.wplib.database.DatabaseHandler;
import com.app.wplib.models.post.Post;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import es.dmoral.toasty.Toasty;

import static com.app.hitxghbeta.MainApplication.POST_IDS;

public class PostViewPagerActivity extends BaseActivity {

    public static Post thisPost;
    public List<Post> postList = new ArrayList<>();
    private PostFragment postFragment;
    private YouTubePlayer youTubePlayer;
    private DatabaseHandler databaseHandler;
    private ExecutorService executorService;
    private HashMap<String, Future<?>> queue = new HashMap<String, Future<?>>();
    private TextToSpeech tts;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view_pager);

        databaseHandler = new DatabaseHandler(this);

        Toolbar toolbar = findViewById(R.id.postFragmentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostViewPagerActivity.super.onBackPressed();
            }
        });

        //tts = new TextToSpeech(this,this);

        viewPager = findViewById(R.id.postViewPager);
        if(POST_IDS.size()>0)
            setupViewPager(viewPager);

        loadInterstitialAd(true);

    }

    private void setupViewPager(final ViewPager viewPager) {
        PostViewPagerAdapter adapter = new PostViewPagerAdapter(getSupportFragmentManager(),getApplicationContext(),POST_IDS);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(false,new FlipPageViewTransformer());
        //viewPager.setOffscreenPageLimit(getResources().getStringArray(R.array.tab_name).length);
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Do the work here
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Dont put here
            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void openComments(View view){
        if(thisPost!=null) {
            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_save).setVisible(true);
        menu.findItem(R.id.action_speak).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_comments){
            openComments(null);
        }else if(id == R.id.action_share){
            initShare(null);
        }else if(id==R.id.action_save){
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.postViewPager + ":" + viewPager.getCurrentItem());
            postFragment = (PostFragment) page;
            if(postFragment!=null){
                postFragment.savePostOffline();
            }else {
                Toasty.error(getApplicationContext(),"Error saving post",Toast.LENGTH_SHORT).show();
            }
        }else if(id==R.id.action_speak){
            Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.postViewPager + ":" + viewPager.getCurrentItem());
            postFragment = (PostFragment) page;
            if(postFragment!=null){
                postFragment.speakOut();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void speakOut(String content) {
        if(youTubePlayer==null||!youTubePlayer.isPlaying()) {
            String text = Jsoup.parse(content).text();
            if (text != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(text);
                } else {
                    ttsUnder20(text);
                }
                Toasty.info(getApplicationContext(), getString(R.string.read_out_loud_text), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toasty.info(getApplicationContext(), getString(R.string.pause_youtube), Toast.LENGTH_SHORT).show();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
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
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        Toasty.info(getApplicationContext(),getResources().getString(R.string.wait_while_image_is_downloading), Toast.LENGTH_LONG).show();
        int notificationid = url.hashCode();
        String saveTopath = getPhotoPath(PostViewPagerActivity.this);
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
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Uri imageUri = FileProvider.getUriForFile(PostViewPagerActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(path));
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(imageUri);
                sendBroadcast(intent);

                if (flag == 1) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    imageUri = FileProvider.getUriForFile(PostViewPagerActivity.this,
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
        Toasty.error(getApplicationContext(),getResources().getString(R.string.image_downloading_failed),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // Toast.makeText(this, "PostViewPagerActivity", Toast.LENGTH_SHORT).show();
    }
}
