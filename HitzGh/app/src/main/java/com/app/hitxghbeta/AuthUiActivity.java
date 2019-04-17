package com.app.hitxghbeta;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;

import static com.firebase.ui.auth.util.ExtraConstants.IDP_RESPONSE;


public class AuthUiActivity extends AppCompatActivity {

    private static final String GOOGLE_TOS_URL = "https://www.google.com/policies/terms/";
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            showSignInScreen();
        }

    }

    private void showSignInScreen() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.mipmap.ic_launcher)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    private void showSnackbar(String string){
        Toast.makeText(getApplicationContext(),string, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar("Unknown Response");
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toasty.success(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(this, MainActivity.class);
            if(Config.defaultHomeScreen==1){
                in = new Intent(this, MainActivity.class);
            }else if(Config.defaultHomeScreen==2){
                in = new Intent(this, CarouselPostListActivity.class);
            }else if(Config.defaultHomeScreen==3){
                in = new Intent(this, ViewPagerActivity.class);
            }
            in.putExtra(IDP_RESPONSE, IdpResponse.fromResultIntent(data));
            startActivity(in);
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar("Signin Cancelled");
            finish();
            return;
        }

        if (resultCode == ErrorCodes.NO_NETWORK) {
            showSnackbar("No Network");
            return;
        }
        if(resultCode == ErrorCodes.UNKNOWN_ERROR){
            showSnackbar("Unknown Error");
        }

        showSnackbar("Unknown Signin Response");
    }
}
