package woolamania.in.woolamania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
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

public class WatchVideoSplash extends AppCompatActivity {
    private static int timeOut = 4000;
    private Firebase firebase;
    private String deviceId;
    private TextView text;
    private InterstitialAd mInterstitialAd;

    private boolean backpressed= false;

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

        if(!MainActivity.isConnected(WatchVideoSplash.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        text= (TextView)findViewById(R.id.progressText);
        text.setText("Loading..");
        text.setVisibility(View.VISIBLE);
        FetchJsonFreeRoll process= new FetchJsonFreeRoll();
        process.execute();
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        firebase= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastVideoTime");


//        //ads section---------------------------------------------------------------------
//        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
//        mInterstitialAd = new InterstitialAd(WatchVideoSplash.this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
//        mInterstitialAd.setAdListener(new AdListener(){
//            @Override
//            public void onAdLoaded() {
//                displayAd();
//            }
//
//        });
//
//        //----------------------------------------------------------------------------------


        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String s= dataSnapshot.getValue(String.class);
                WatchVideoActivity.lastVideoTime= Long.parseLong(s);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });






        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                text.setVisibility(View.GONE);
                if(!backpressed) {
                    Intent intent = new Intent(WatchVideoSplash.this, WatchVideoActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }, timeOut);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
