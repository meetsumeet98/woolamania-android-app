package woolamania.in.woolamania;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WatchVideoActivity extends AppCompatActivity implements View.OnClickListener {
    public static long lastVideoTime;
    private Long currentTime;
    private Button videobtn;
    private TextView pointdisplay,v1,loading;
    private ProgressBar progressBar;
    private   int getPoints;

    private Firebase points,lastvidtimeref;
    private InterstitialAd mInterstitialAd;
    private String deviceId;

    private RewardedVideoAd mRewardedVideoAd;





    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();

    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        if(!MainActivity.isConnected(WatchVideoActivity.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentTime= Long.parseLong(FreeRoll.currenttime);
        loading= (TextView)findViewById(R.id.loading);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        videobtn=(Button)findViewById(R.id.video1);
        pointdisplay=(TextView)findViewById(R.id.pointsdisplay);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        v1=(TextView)findViewById(R.id.v1);
        lastvidtimeref= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastVideoTime");




        // ----------------------RewardedVideoAds-------------------------------------------
        if((currentTime - lastVideoTime)>14400){
                progressBar.setVisibility(View.VISIBLE);
                loading.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);

            int min = (int) (currentTime - lastVideoTime) / 60;
            int sec = (int) (currentTime - lastVideoTime);

            int showhour = 3;
            int showmin= 59;
            int showsec= 60;

            showhour = (showhour- (int) min / 60);
            showmin = (showmin- (min%60));
            showsec= (showsec- sec%60);

            loading.setText("Next Video in  " + String.valueOf(showhour) + " hrs: " + String.valueOf(showmin) + " min: " + String.valueOf(showsec) + " sec");


        }
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));

        // Get reference to singleton RewardedVideoAd object
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(WatchVideoActivity.this);
        mRewardedVideoAd.loadAd(getString(R.string.ad_unit_id), new AdRequest.Builder().build());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {


            @Override
            public void onRewardedVideoAdLoaded() {

                if((currentTime - lastVideoTime)>14400){
                    progressBar.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    videobtn.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    videobtn.setOnClickListener(WatchVideoActivity.this);
                }
                else {
                    videobtn.setVisibility(View.GONE);
                    v1.setVisibility(View.GONE);
                }


            }

            @Override
            public void onRewardedVideoAdOpened() {
                lastvidtimeref.setValue(String.valueOf(currentTime));

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                videobtn.setVisibility(View.GONE);
                v1.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in "+"3 hrs: 59 min: 59 sec");
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                videobtn.setVisibility(View.GONE);
                v1.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in "+"3 hrs: 59 min: 59 sec");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {
                new SweetAlertDialog(WatchVideoActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Congratulations!")
                        .setContentText("10 coins earned successfully ! ")
                        .setCustomImage(R.drawable.coins)
                        .show();

                videobtn.setVisibility(View.GONE);
                v1.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in "+" 3 hrs: 59 min: 59 sec");
                points.setValue(String.valueOf(getPoints+10));

            }
        });

        //----------------------------------------------------------------------------------


        points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String getpoints = dataSnapshot.getValue(String.class);
                getPoints=Integer.parseInt(getpoints);
                pointdisplay.setText(getpoints);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }


    @Override
    public void onClick(View v) {

        mRewardedVideoAd.show();

    }
}
