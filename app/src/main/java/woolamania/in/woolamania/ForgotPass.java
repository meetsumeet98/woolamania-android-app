package woolamania.in.woolamania;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPass extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth user;
    private InterstitialAd mInterstitialAd;


    private void displayAd() {
        mInterstitialAd.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ForgotPass.this,LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        progressBar= (ProgressBar)findViewById(R.id.progressBar);
        user= FirebaseAuth.getInstance();

        //ads section---------------------------------------------------------------------
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(ForgotPass.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        //----------------------------------------------------------------------------------


        findViewById(R.id.forgotsubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail= ((EditText) findViewById(R.id.forgotpassemail)).getText().toString().trim();
                findViewById(R.id.forgotsubmit).setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                if(useremail.isEmpty()){
                    ((EditText) findViewById(R.id.forgotpassemail)).setError("Email is required");
                    findViewById(R.id.forgotpassemail).requestFocus();
                }else{
                    user.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                new SweetAlertDialog(ForgotPass.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Passsword Reset Email Sent!")
                                        .setContentText("Please check inbox for password reset link.").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        finish();
                                        startActivity(new Intent(ForgotPass.this,MainActivity.class));
                                    }
                                }).show();

                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                findViewById(R.id.forgotsubmit).setVisibility(View.VISIBLE);
                                new SweetAlertDialog(ForgotPass.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Password reset email can't be sent!")
                                        .setContentText("please check if you entered correct registered email address")
                                        .show();

                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
