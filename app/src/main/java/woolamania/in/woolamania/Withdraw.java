package woolamania.in.woolamania;

import android.content.Intent;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class Withdraw extends AppCompatActivity implements View.OnClickListener {

    private TextView pointsdisplay;
    private Firebase mfirebase;
    private InterstitialAd mInterstitialAd;
    private String deviceId;

    private void displayAd() {
        mInterstitialAd.show();
    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mInterstitialAd.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Withdraw.this,Home.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        if(!MainActivity.isConnected(Withdraw.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Firebase.setAndroidContext(this);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        findViewById(R.id.paypal).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.paytm).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.payoneer).setOnClickListener((View.OnClickListener) this);
        mfirebase=new  Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        pointsdisplay= findViewById(R.id.points);
        mfirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s= dataSnapshot.getValue(String.class);
                pointsdisplay.setText(s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //ads section---------------------------------------------------------------------
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(Withdraw.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        //----------------------------------------------------------------------------------


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.paypal:
               // Payment.email.setHint("Email");
                startActivity(new Intent(Withdraw.this,Payment.class));
                break;
            case R.id.paytm:
               // Payment.email.setHint("Paytm Number");

                startActivity(new Intent(Withdraw.this,Paytm.class));

                break;
            case R.id.payoneer:
               // Payment.email.setHint("Payoneer Email");

                startActivity(new Intent(Withdraw.this,Payoneer.class));

                break;
        }
    }


}
