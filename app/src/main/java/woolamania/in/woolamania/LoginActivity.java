
package woolamania.in.woolamania;

import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import static woolamania.in.woolamania.R.id.sign_up;


public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {

    private Button button;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private InterstitialAd mInterstitialAd;

    private TextView progressText,Signuptext;

    private String Email,deviceId;
    private String Password;
    private Query mfirebase,existance;
    private String launchdetector;


    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(!MainActivity.isConnected(LoginActivity.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Firebase.setAndroidContext(this);


        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mfirebase= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId+"/refstatus");
        existance= new Firebase("https://woolamania.firebaseio.com/User/"+deviceId);
        mfirebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                launchdetector= dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        MobileAds.initialize(getApplicationContext(),getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(LoginActivity.this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                displayAd();
            }

        });




        button = (Button)findViewById(R.id.signinbutton);
        email= (EditText)findViewById(R.id.Email);
        password= (EditText)findViewById(R.id.password);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressText=(TextView)findViewById(R.id.progressText) ;
        mAuth = FirebaseAuth.getInstance();
        Signuptext= findViewById(R.id.sign_up);
        findViewById(R.id.forgotpass).setOnClickListener(this);
        button.setOnClickListener(this);
        findViewById(sign_up).setOnClickListener(this);





    }

    private void displayAd() {
        mInterstitialAd.show();
    }


    private void userLogin() {

        Email= email.getText().toString().trim();

        Password= password.getText().toString().trim();

        final FirebaseUser user1= FirebaseAuth.getInstance().getCurrentUser();
        if (Email.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {

            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }
        if (Password.isEmpty()) {

            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (Password.length() < 6) {
            password.setError("Minimum password length is 6");
            password.requestFocus();
            return;
        }

        Query query= existance.orderByValue().equalTo(Email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getApplicationContext(),"Incorrect email address for this device.",Toast.LENGTH_SHORT).show();
                    return;
                }
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                      login();
                    }
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }

    private void login() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        Signuptext.setVisibility(View.GONE);
        findViewById(R.id.forgotpass).setVisibility(View.GONE);

        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Firebase.setAndroidContext(getApplicationContext());
                final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                if(task.isSuccessful()){
                    if(user.isEmailVerified()) {
                        Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_SHORT).show();

                        if(launchdetector.equals("false")) {
                            Intent intent = new Intent(LoginActivity.this, RefferalCodeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            finish();

                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            finish();

                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        }
                    }
                    else{
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        Signuptext.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"please verify your email ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressText.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    Signuptext.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        mInterstitialAd.show();
        switch (v.getId()){
            case R.id.signinbutton:
                userLogin();
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, signUpActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
            case R.id.forgotpass:
                startActivity(new Intent(this, ForgotPass.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;


        }
    }


}
