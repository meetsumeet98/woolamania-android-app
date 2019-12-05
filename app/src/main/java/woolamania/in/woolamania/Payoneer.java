package woolamania.in.woolamania;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import hotchemi.android.rate.AppRate;

public class Payoneer extends AppCompatActivity implements View.OnClickListener{

    Button withdraw;
    public static TextView points,email,dollar;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private String getpoints;
    private Firebase Points;

    private Long getPoints,balance;
    private String deviceId;
    private InterstitialAd mInterstitialAd;
    private Firebase payoneer,lastwithdrawal;


    private void displayAd() {
        mInterstitialAd.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payoneer);
        if(!MainActivity.isConnected(Payoneer.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppRate.with(Payoneer.this)
                .setInstallDays(10)
                .setLaunchTimes(5)
                .setRemindInterval(10)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(Payoneer.this);

        Firebase.setAndroidContext(this);
        dollar=(TextView) findViewById(R.id.dollar);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        payoneer= new Firebase("https://woolamania.firebaseio.com/Payments/payoneer/"+deviceId);
        lastwithdrawal= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastWithdrawal");
        radioGroup= findViewById(R.id.radioGroup);
        Points.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getpoints= dataSnapshot.getValue(String.class);
                getPoints= Long.parseLong(getpoints);
                dollar.setText(getpoints);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        withdraw= (Button)findViewById(R.id.withdrawbtn);

        points= (TextView)findViewById(R.id.points);
        email= (TextView)findViewById(R.id.method);
        withdraw.setOnClickListener(this);

        //ads section---------------------------------------------------------------------
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(Payoneer.this);
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
            case R.id.withdrawbtn:
                validate();
                break;

        }
    }

    private void validate() {
        String Email = email.getText().toString().trim();

        if(Email.isEmpty()){
            email.setError("Please enter Payoneer email address");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){

            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }

        int radioId= radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);

        switch (radioId){
            case R.id.radioOne:
                if(getPoints!=null) {
                    if (getPoints < 13000) {
                        insufficientFunds();
                    } else {
                        balance = getPoints - 13000;
                        Points.setValue(String.valueOf(balance));
                        payoneer.push().child("5 dollar").setValue(Email);
                        alert("5");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Poor network! please try again.",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.radioTwo:
                if(getPoints!=null) {
                    if (getPoints < 25000) {
                        insufficientFunds();
                    } else {
                        balance = getPoints - 25000;
                        Points.setValue(String.valueOf(balance));
                        payoneer.push().child("10 dollar").setValue(Email);
                        alert("10");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Poor network! please try again.",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.radioThree:
                if(getPoints!=null) {
                    if (getPoints < 37000) {
                        insufficientFunds();
                    } else {
                        balance = getPoints - 37000;
                        Points.setValue(String.valueOf(balance));
                        payoneer.push().child("15 dollar").setValue(Email);
                        alert("15");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Poor network! please try again.",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.radioFour:
                if(getPoints!=null) {
                    if (getPoints < 48000) {
                        insufficientFunds();
                    } else {
                        balance = getPoints - 48000;
                        Points.setValue(String.valueOf(balance));
                        payoneer.push().child("20 dollar").setValue(Email);
                        alert("20");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Poor network! please try again.",Toast.LENGTH_SHORT).show();
                }
                break;
        }


    }

    private void alert(String i) {
        new SweetAlertDialog(Payoneer.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Successful !")
                .setContentText("Withdraw request for "+ i + " $ Payoneer is successful.\nYou will receive the money by 10th of every month.")
                .show();
        lastwithdrawal.setValue(i+" Dollars");
        email.setText(" ");

    }

    private void insufficientFunds() {
        new SweetAlertDialog(Payoneer.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Insufficient Funds!")
                .setContentText("Minimum 13,000 coins are required for withdrawal.\nRefer your friends, participate in Lottery to earn fast.")
                .show();
    }
}
