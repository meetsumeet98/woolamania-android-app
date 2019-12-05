package woolamania.in.woolamania;

import android.content.Intent;
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

public class LotteryActivity extends AppCompatActivity implements View.OnClickListener {

    public static long lastTicketTime;
    public static String prize_amounts,round_no;
    public static int localvalue;

    private long currentTime;
    private InterstitialAd mInterstitialAd;
    private Button winners,dailyticket;
    private TextView mytickets,round,first,second,third,fourth,fifth;
    private Firebase tickets,lotteryround;

    private String deviceId;
    private int getTickets;
    private TextView loading;
    private ProgressBar progressBar;
    private RewardedVideoAd mRewardedVideoAd;
    private Firebase lasttkttimeref;
    private Firebase localValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        if(!MainActivity.isConnected(LotteryActivity.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        first= (TextView)findViewById(R.id.first);
        second= (TextView)findViewById(R.id.second);
        third= (TextView)findViewById(R.id.third);
        fourth= (TextView)findViewById(R.id.fourth);
        fifth= (TextView)findViewById(R.id.fifth);



        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        lasttkttimeref= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastTicketTime");
        lotteryround= new Firebase("https://woolamania.firebaseio.com/winnerslist/lotteryRound");
        tickets = new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/mytickets");
        localValue=new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/localvalue");

        round=(TextView)findViewById(R.id.round);


        first.setText(prize_amounts.substring(0,5)+"  Coins");
        second.setText(prize_amounts.substring(5,10)+"  Coins");
        third.setText(prize_amounts.substring(10,15)+"  Coins");
        fourth.setText(prize_amounts.substring(15,20)+"  Coins");
        fifth.setText(prize_amounts.substring(20,25)+"  Coins");



        lotteryround.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                round_no= dataSnapshot.getValue(String.class);
                round.setText("Round : "+round_no);
                if(Integer.parseInt(round_no) != localvalue){
                    tickets.setValue("0");
                    localvalue++;
                    localValue.setValue(String.valueOf(localvalue));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        currentTime= Long.parseLong(FreeRoll.currenttime);

        loading= (TextView)findViewById(R.id.loading);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);

        mytickets= (TextView)findViewById(R.id.points);



        tickets.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getTickets= dataSnapshot.getValue(Integer.class);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        winners= (Button)findViewById(R.id.winners);
        dailyticket= (Button)findViewById(R.id.dailyticket);
        mytickets.setOnClickListener(this);
        winners.setOnClickListener(this);




//        //ads section---------------------------------------------------------------------
//        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
//        mInterstitialAd = new InterstitialAd(LotteryActivity.this);
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
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



        // ----------------------RewardedVideoAds-------------------------------------------
        if((currentTime - lastTicketTime)>3600){
            progressBar.setVisibility(View.VISIBLE);
            loading.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.GONE);

            int min=(int)(59-(currentTime-lastTicketTime)/60);
            long sec= (60-(currentTime-lastTicketTime)%60);
            String minString= String.valueOf(min);
            String secString= String.valueOf(sec);
            loading.setText("Next Video in "+min+" min: "+sec+" sec");
        }



        // Get reference to singleton RewardedVideoAd object
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(LotteryActivity.this);
        mRewardedVideoAd.loadAd(getString(R.string.ad_unit_id), new AdRequest.Builder().build());
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {


            @Override
            public void onRewardedVideoAdLoaded() {
             Toast.makeText(LotteryActivity.this,"video loaded",Toast.LENGTH_SHORT);
                if((currentTime - lastTicketTime)>120){
                    progressBar.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    dailyticket.setVisibility(View.VISIBLE);
                    dailyticket.setOnClickListener(LotteryActivity.this);

                }else {
                  dailyticket.setVisibility(View.GONE);
                }


            }

            @Override
            public void onRewardedVideoAdOpened() {
                lasttkttimeref.setValue(String.valueOf(currentTime));

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                dailyticket.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in 59 min : 59 sec.");
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                dailyticket.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in 59 min : 59 sec.");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {
                tickets.setValue(String.valueOf(getTickets+1));
                Toast.makeText(getApplicationContext(),"earned 10 coins",Toast.LENGTH_SHORT).show();
                lasttkttimeref.setValue(FreeRoll.currenttime);
                dailyticket.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                loading.setText("Next Video in 59 min : 59 sec.");
            }
        });

        //----------------------------------------------------------------------------------

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void displayAd() {
        mInterstitialAd.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.winners:
                startActivity(new Intent(LotteryActivity.this,LotteryWinners.class));
                 break;

            case R.id.points:

                new SweetAlertDialog(LotteryActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("You have "+String.valueOf(getTickets)+" tickets.")
                        .setContentText("Earn more tickets to increase your chances of winning.")
                        .show();

                break;

            case R.id.dailyticket:
                mRewardedVideoAd.show();
                break;

        }
    }
}
