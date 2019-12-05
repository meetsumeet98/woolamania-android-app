package woolamania.in.woolamania;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FreeRoll extends AppCompatActivity {


    public static String referaStatus;
    public static String referalcode;


    private Button freeRollBtn;
    private TextView rollText,nextRoll,pointdisplay;
    private Firebase firebase,points,mfirebase;
    private String deviceId,getpoints;
    public static String lastrolltime;
    public static String currenttime;
    public static long lastRollTime;
    private long currentTime;
    private int randomNumber;
    private long rewardpoints,getPoints;

    private InterstitialAd mInterstitialAd;



    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_roll);

        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        firebase= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastRollTime");
        mfirebase= new Firebase("https://woolamania.firebaseio.com/RefferalsList/"+referalcode);


        //ads section--------------------------------------------------------------------

        mInterstitialAd = new InterstitialAd(FreeRoll.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });


        //-----------------------------------------------------------------------------------





        currentTime= Long.parseLong(currenttime);
        rollText= (TextView)findViewById(R.id.rollText);
        nextRoll= (TextView)findViewById(R.id.nextroll);
        pointdisplay=(TextView)findViewById(R.id.points);

        freeRollBtn = (Button)findViewById(R.id.freeRoll);


        RandomNumber random= new RandomNumber();
        randomNumber= random.Rand();


        points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getpoints= dataSnapshot.getValue(String.class);
                getPoints=Integer.parseInt(getpoints);
                pointdisplay.setText(getpoints);


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });



        if(randomNumber<=9000){
            rewardpoints=2;

        }else{
            if(randomNumber>=9001 && randomNumber<= 9040) {
                rewardpoints = 10;
            }
            else  {
                rewardpoints= 50;
            }
        }



        if((currentTime-FreeRoll.lastRollTime)>900){
            freeRollBtn.setVisibility(View.VISIBLE);
            freeRollBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    freeRollBtn.setVisibility(View.GONE);
                    nextRoll.setVisibility(View.VISIBLE);
                    rollText.setText("14 min : 59 sec");
                    rollText.setVisibility(View.VISIBLE);
                    new SweetAlertDialog(FreeRoll.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                            .setTitleText("Congratulations!")
                            .setContentText("Lucky Number : "+String.valueOf(randomNumber)+"\n You have won "+rewardpoints+" coins! ")
                            .setCustomImage(R.drawable.coins)
                            .show();

                    firebase.setValue(FreeRoll.currenttime);
                    points.setValue(String.valueOf(getPoints+rewardpoints));


                }
            });
        }else {
            rollText.setVisibility(View.VISIBLE);
            nextRoll.setVisibility(View.VISIBLE);
            int min=(int)(14-(currentTime-lastRollTime)/60);   
            long sec= (60-(currentTime-lastRollTime)%60);
            String minString= String.valueOf(min);
            String secString= String.valueOf(sec);
            freeRollBtn.setVisibility(View.GONE);
            rollText.setText(minString+" min : "+secString+" sec");

        }

    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void displayAd() {
        mInterstitialAd.show();
    }
}
