package woolamania.in.woolamania;



import android.content.Intent;

import android.provider.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import hotchemi.android.rate.AppRate;


public class Home extends AppCompatActivity implements View.OnClickListener{


    public static String refferedme;
    private TextView points;
    private Button freeRoll,lottery;
    private Firebase connect,mfirebase;
    private InterstitialAd mInterstitialAd;
    private Firebase point;
    private String deviceId;


    private void displayAd() {
        mInterstitialAd.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(!MainActivity.isConnected(Home.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        AppRate.with(Home.this)
                .setInstallDays(10)
                .setLaunchTimes(5)
                .setRemindInterval(10)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(Home.this);


        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(Home.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        Firebase.setAndroidContext(this);

        points= findViewById(R.id.points);
        connect= new Firebase("https://woolamania.firebaseio.com/User");
        point= new  Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");

        findViewById(R.id.freeroll).setOnClickListener(this);
        findViewById(R.id.lottery).setOnClickListener(this);
        findViewById(R.id.watchVideo).setOnClickListener(Home.this);
        findViewById(R.id.homebtn).setOnClickListener(Home.this);
        findViewById(R.id.redeembtn).setOnClickListener(Home.this);
        findViewById(R.id.accountbtn).setOnClickListener(Home.this);

        point.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                String s= dataSnapshot.getValue(String.class);
                points.setText(s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if(refferedme!=null){
            mfirebase=new Firebase("https://woolamania.firebaseio.com/RefferalsList/"+refferedme);
            mfirebase.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    String getpoints= dataSnapshot.getValue(String.class);
                    int getPoints= Integer.parseInt(getpoints);
                    mfirebase.setValue(String.valueOf(1+getPoints));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.freeroll:

                startActivity( new Intent(Home.this,FreeRollSplash.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;

            case R.id.lottery:
                startActivity( new Intent(Home.this,LotterySplash.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;

            case R.id.watchVideo:
                startActivity( new Intent(Home.this,WatchVideoSplash.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.homebtn:
                break;
            case R.id.redeembtn:
                startActivity(new Intent(Home.this, Withdraw.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.accountbtn:
                startActivity(new Intent(Home.this, Account.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;

        }
    }

}









































      /* tv=(TextView) findViewById(R.id.textView);
        tv.setText("hello");
 Firebase fb= new Firebase("https://woolamania.firebaseio.com/User/"+s2+"/email");
        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s1= dataSnapshot.getValue(String.class);
                tv.setText(s1);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

*/











