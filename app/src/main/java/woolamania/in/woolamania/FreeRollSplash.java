package woolamania.in.woolamania;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
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

public class FreeRollSplash extends AppCompatActivity {
    private int timeOut =6000;
    public static String referalcode;
    public static String myrefcode;
    private boolean backpressed= false;
    private Firebase firebase,mfirebase,newrefs,points;
    private String deviceId,getnewrefs;
    private TextView text;
    private InterstitialAd mInterstitialAd;


    private void displayAd() {
        mInterstitialAd.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backpressed= true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_roll_splash);
        if(!MainActivity.isConnected(FreeRollSplash.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;

        }
        FetchJsonFreeRoll process= new FetchJsonFreeRoll();
        process.execute();





        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        firebase= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastRollTime");


        newrefs= new Firebase("https://woolamania.firebaseio.com/RefferalsList/"+myrefcode);
        points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");


        newrefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getnewrefs= dataSnapshot.getValue(String.class);
                Account.newreferals = getnewrefs;

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FreeRoll.lastrolltime= dataSnapshot.getValue(String.class);
                FreeRoll.lastRollTime= Long.parseLong(FreeRoll.lastrolltime);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        text= (TextView)findViewById(R.id.progressText);
        text.setText("Loading..");
        text.setVisibility(View.VISIBLE);



        //ads section---------------------------------------------------------------------
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(FreeRollSplash.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        //----------------------------------------------------------------------------------



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                findViewById(R.id.progressBar).setVisibility(View.GONE);
                text.setVisibility(View.GONE);
                if(!backpressed) {
                    Intent intent = new Intent(FreeRollSplash.this, FreeRoll.class);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);}
                 }
        }, timeOut);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
