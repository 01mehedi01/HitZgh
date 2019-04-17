package com.app.hitxghbeta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.wplib.ApiInterface;
import com.app.wplib.models.comment.Comment;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCommentActivity extends BaseActivity {

    public static final String ARG_PARENT = "add_comment_arg_parent";
    public static final String ARG_POST = "add_comment_arg_post";

    public static final String NAME_PREF = "name_pref";
    public static final String EMAIL_PREF = "email_pref";

    private Toolbar toolbar;
    private TextInputEditText nameView,emailView,commentView;

    private boolean nightModeEnabled;
    private int parent;
    private int post;
    private boolean isActive;
    private String name;
    private String email;
    private String comment;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(getIntent()!=null){
            post = getIntent().getIntExtra(ARG_POST,0);
            parent = getIntent().getIntExtra(ARG_PARENT,0);
        }
        toolbar = findViewById(R.id.addCommentToolbar);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            nightModeEnabled=true;
            toolbar.setBackgroundColor(getResources().getColor(R.color.toolbarColor));
        }else {
            nightModeEnabled=false;
        }
        nameView = findViewById(R.id.nameEditText);
        emailView = findViewById(R.id.emailView);
        commentView = findViewById(R.id.commentTextView);
        nameView.setText(sharedPref.getString(NAME_PREF,""));
        emailView.setText(sharedPref.getString(EMAIL_PREF,""));
        getAuthDetails();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCommentActivity.super.onBackPressed();
            }
        });
    }

    private void getAuthDetails(){
        if(Config.allowOnlySignedInUsersToComment&&auth.getCurrentUser()!=null){
            if (auth.getCurrentUser().getDisplayName()==null)
                return;
            if(auth.getCurrentUser().getEmail()==null)
                return;
            nameView.setText(auth.getCurrentUser().getDisplayName());
            emailView.setText(auth.getCurrentUser().getEmail());
            if(auth!=null&&auth.getCurrentUser()!=null){
                nameView.setEnabled(false);
                emailView.setEnabled(false);
            }
        }else {
            showLoginDialog();
        }
    }

    private void showLoginDialog(){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this);
            if(nightModeEnabled)
                builder = new AlertDialog.Builder(this,R.style.NightDialogTheme);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setMessage(getResources().getString(R.string.login_before_comment))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),AuthUiActivity.class);
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    public void postComment(View view){
        name = nameView.getText().toString();
        email = emailView.getText().toString();
        comment = commentView.getText().toString();

        if(post==0){
            Toasty.error(getApplicationContext(),getResources().getString(R.string.invalid_post), Toast.LENGTH_SHORT).show();
            return ;
        }

        if(name==null|| TextUtils.isEmpty(name)){
            Toasty.error(getApplicationContext(),getResources().getString(R.string.error_name_field), Toast.LENGTH_SHORT).show();
            return ;
        }
        if(email==null|| TextUtils.isEmpty(email)){
            Toasty.error(getApplicationContext(),getResources().getString(R.string.error_email_field), Toast.LENGTH_SHORT).show();
            return ;
        }
        if(comment==null|| TextUtils.isEmpty(comment)){
            Toasty.error(getApplicationContext(),getResources().getString(R.string.error_comment_field), Toast.LENGTH_SHORT).show();
            return ;
        }

        saveData(name,email);
        Toasty.info(getApplicationContext(),getResources().getString(R.string.posting_coment_msg),Toast.LENGTH_SHORT).show();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Comment> postComment = apiInterface.postComment(post,parent,name,email,comment);
        Log.e("Making Request","To: "+postComment.request().url());
        postComment.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.isSuccessful()){
                    Toasty.success(getApplicationContext(),"Comment posted",Toast.LENGTH_SHORT).show();
                    showDialog(getResources().getString(R.string.posting_success_title),response.body().getStatus());
                }else {
                    Log.e("Error Response","Error: "+response.message());
                    Toasty.error(getApplicationContext(),getResources().getString(R.string.failed_posting_comment),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("Error Message","Msg: "+t.getLocalizedMessage());
                Toasty.error(getApplicationContext(),getResources().getString(R.string.failed_posting_comment),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData(String name,String email){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(NAME_PREF,name);
        editor.putString(EMAIL_PREF,email);
        editor.apply();
    }

    private void showDialog(String title,String msg){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this);
            if(nightModeEnabled)
                builder = new AlertDialog.Builder(this,R.style.NightDialogTheme);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setMessage(getResources().getString(R.string.posting_success_body)+msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        if(isActive) {
            builder.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive=false;
    }
}
