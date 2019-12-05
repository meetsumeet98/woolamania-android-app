package woolamania.in.woolamania;

import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RefferalCodeActivity extends AppCompatActivity implements View.OnClickListener{
    private Firebase mfirebase,mfb;
    private EditText refferal_code;
    private Button btn,skipbtn;

    private String code;
    private Firebase referalStatus,points;
    private String deviceId;
    private InterstitialAd mInterstitialAd;


    private void displayAd() {
        mInterstitialAd.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refferal_code);
        if(!MainActivity.isConnected(RefferalCodeActivity.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Firebase.setAndroidContext(this);
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        referalStatus =  new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/refstatus");
        points= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/points");
        refferal_code= (EditText)findViewById(R.id.RefferalCode);
        btn = (Button)findViewById(R.id.submitcode);
        skipbtn= findViewById(R.id.skip);
        mfirebase = new Firebase("https://woolamania.firebaseio.com/RefferalsList");


        //ads section---------------------------------------------------------------------
        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(RefferalCodeActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        //----------------------------------------------------------------------------------



        btn.setOnClickListener(this);
        skipbtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {



        switch (v.getId()){
            case R.id.submitcode:

                code = refferal_code.getText().toString().trim();
                if(code.isEmpty()){
                    refferal_code.setError("please enter the Refferal Code");
                    refferal_code.requestFocus();
                    return;
                }
                final SweetAlertDialog dialog= new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
                dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                dialog.setTitleText("Checking..");
                dialog.setCancelable(false);
                dialog.show();

                mfb=new Firebase("https://woolamania.firebaseio.com/RefferalsList/"+code);
                Query query= mfirebase.orderByKey().equalTo(code);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            dialog.hide();
                            new SweetAlertDialog(RefferalCodeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Incorrect Referral Code! !")
                                    .show();
                            return;
                        }
                        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            if(singleSnapshot.exists()){
                                dialog.hide();
                                int getPoints= dataSnapshot.child(code).getValue(Integer.class);

                                referalStatus.setValue("true");
                                mfb.setValue(String.valueOf(++getPoints));
                                Toast.makeText(getApplicationContext(),"exisys",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RefferalCodeActivity.this,Home.class));

                            }
                        }
                    }



                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                points.setValue("100");
                break;
            case R.id.skip:

                referalStatus.setValue("true");
                points.setValue("50");
                startActivity(new Intent(RefferalCodeActivity.this,Home.class));
                break;
        }



    }
}
