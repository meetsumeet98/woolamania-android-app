package woolamania.in.woolamania;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Account extends AppCompatActivity implements View.OnClickListener{

    public static String newreferals;
    public static String myrefcode;
    private TextView logout,Faq,Lastwithdrawal,Invite,useremail,contactUs;
    private InterstitialAd mInterstitialAd;

    private Firebase points,newrefs,lastwithdraw,myCode;

    private String deviceId,getpoints,lastwithdrawal;
    private int getPoints;

    private int p;
    private void displayAd() {
        mInterstitialAd.show();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if(!MainActivity.isConnected(Account.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Firebase.setAndroidContext(this);

        useremail= findViewById(R.id.useremail);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        useremail.setText(user.getEmail());

        logout = (TextView) findViewById(R.id.logout);
        logout.setOnClickListener(this);


        Faq=(TextView) findViewById(R.id.faq);
        Faq.setOnClickListener(this);

        Lastwithdrawal= (TextView)findViewById(R.id.lastwithdrawal);
        Lastwithdrawal.setOnClickListener(this);

        Invite= findViewById(R.id.share);
        Invite.setOnClickListener(this);

        contactUs=findViewById(R.id.contactus);
        contactUs.setOnClickListener(this);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        points = new Firebase("https://woolamania.firebaseio.com/User/" + deviceId + "/points");
        myCode = new Firebase("https://woolamania.firebaseio.com/User/" + deviceId + "/refcode");
        newrefs= new Firebase("https://woolamania.firebaseio.com/RefferalsList/"+myrefcode);
        lastwithdraw= new Firebase("https://woolamania.firebaseio.com/User/" + deviceId + "/lastWithdrawal");
        if(newreferals!=null) {
            int pts= Integer.parseInt(newreferals);
            p= pts*100;
            if (pts != 0) {
                new SweetAlertDialog(Account.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Congratulations!")
                        .setContentText("You have " + String.valueOf(pts) + " new Referals\n" + String.valueOf(p) + " Coins are added to your main balance")
                        .setCustomImage(R.drawable.coins)
                        .show();
                newrefs.setValue("0");

            }



            points.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getpoints = dataSnapshot.getValue(String.class);

                    getPoints = Integer.parseInt(getpoints);

                    points.setValue(String.valueOf(p + getPoints));
                    p=0;



                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });



        }

        lastwithdraw.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastwithdrawal= dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        myCode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myrefcode= dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        /*ads section---------------------------------------------------------------------

        mInterstitialAd = new InterstitialAd(Account.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });

        ----------------------------------------------------------------------------------*/


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout:

            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(Account.this, LoginActivity.class));
            break;

            case R.id.faq:

                startActivity(new Intent(Account.this, Faq.class));
                break;

            case R.id.lastwithdrawal:
                new SweetAlertDialog(Account.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText(lastwithdrawal)
                        .setContentText("You will receive withdrawals by 10th of every month")
                        .show();
                break;

            case R.id.share:
                new SweetAlertDialog(Account.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Refferal Code: "+" "+myrefcode)
                        .setContentText("Invite your friend and you both will get 100 coins bonus.")
                        .show();
                break;

            case R.id.contactus:

                new SweetAlertDialog(Account.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("woolamania.care@outlook.com")
                        .setContentText("For any queries, please feel free to write us. We will get back to you within 48 hours.")
                        .show();
                break;
        }
    }
}
