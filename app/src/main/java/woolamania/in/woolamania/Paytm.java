package woolamania.in.woolamania;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hotchemi.android.rate.AppRate;

public class Paytm extends AppCompatActivity implements View.OnClickListener {
    private Button withdraw;
    public static TextView points,email,rs;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private String getpoints;
    private Firebase Points,paytm,child,infochild,lastwithdrawal;

    private Long getPoints,balance;
    private String deviceId;
    private InterstitialAd mInterstitialAd;

    private void displayAd() {
        mInterstitialAd.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);
        if(!MainActivity.isConnected(Paytm.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppRate.with(Paytm.this)
                .setInstallDays(10)
                .setLaunchTimes(5)
                .setRemindInterval(10)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(Paytm.this);


        Firebase.setAndroidContext(this);
        rs=(TextView) findViewById(R.id.rs);

        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        lastwithdrawal= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/lastWithdrawal");
        paytm= new Firebase("https://woolamania.firebaseio.com/Payments/paypal/"+deviceId);

        radioGroup= findViewById(R.id.radioGroup);
        Points.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String getpoints= dataSnapshot.getValue(String.class);
                getPoints= Long.parseLong(getpoints);
                rs.setText(getpoints);
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
        mInterstitialAd = new InterstitialAd(Paytm.this);
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
            email.setError("Please enter Paytm number");
            email.requestFocus();
            return;
        }


        if(checkNo(Email) || Email.length()>12 || Email.length()<10){
            email.setError("Enter a valid phone number");
            email.requestFocus();
            return;
        }

        int radioId= radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);
        paytm= new Firebase("https://woolamania.firebaseio.com/Payments/paytm/"+deviceId);
        switch (radioId){
            case R.id.radioOne:
                if(getPoints!=null) {
                    if (getPoints < 13000) {

                        insufficientFunds();
                    } else {
                        balance = getPoints - 13000;
                        Points.setValue(String.valueOf(balance));

                        paytm.push().child("325 rupees").setValue(Email);
                        alert("325 ₹");

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
                        paytm.push().child("650 rupees").setValue(Email);
                        alert("650 ₹");

                    }
                } else{
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
                        paytm.push().child("975 rupees").setValue(Email);
                        alert("975 ₹");

                    }
                } else{
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
                        paytm.push().child("1300 rupees").setValue(Email);
                        alert("1300 ₹");
                    }
                } else{
                    Toast.makeText(getApplicationContext(),"Poor network! please try again.",Toast.LENGTH_SHORT).show();
                }
                break;
        }




    }


    private void alert(String i) {
        new SweetAlertDialog(Paytm.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Successful !")
                .setContentText("Withdraw request for "+ i + " Paytm is successful.\nYou will receive the money by 10th of every month")
                .show();
        lastwithdrawal.setValue(i+" Rs.");
        email.setText(" ");

    }

    private void insufficientFunds() {
        new SweetAlertDialog(Paytm.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Insufficient Funds!")
                .setContentText("Refer your friends, participate in Lottery to earn fast.")
                .show();
    }

    public Boolean checkNo(String sn){
        Boolean check =true;
        String no= "\\d*\\.?\\d+";
        CharSequence inputstr= sn;

        Pattern pte = Pattern.compile(no,Pattern.CASE_INSENSITIVE);
        Matcher matcher= pte.matcher(inputstr);

        if(matcher.matches()){
            check= false;
        }
        return check;
    }
}
