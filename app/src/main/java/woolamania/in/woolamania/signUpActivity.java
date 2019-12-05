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

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;


public class signUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email, password, confirmpass;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView progressText,Signintext;
    private Button button;

    private Firebase connect,globalround;
    private String pass,cpass,deviceId,Email,Password,roundno;


    private Firebase User, points, childEmail,lastWithdrawal,localvalue,childPassword, childDeviceId,lastRollTime,Reffaralslist,addmycode,mytickets,refstatus,refcode,lastVideoTime,lastTicketTime;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(signUpActivity.this,LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if(!MainActivity.isConnected(signUpActivity.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

       Firebase.setAndroidContext(this);
        connect= new Firebase("https://woolamania.firebaseio.com/User");
      //  globalround= new Firebase("https://woolamania.firebaseio.com/winnerslist/lotteryRound");
        Reffaralslist= new Firebase("https://woolamania.firebaseio.com/RefferalsList");
        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mAuth= FirebaseAuth.getInstance();
        email= (EditText)findViewById(R.id.Email);
        password =(EditText)findViewById(R.id.password);
        confirmpass=(EditText)findViewById(R.id.confirmpass);
        progressBar= (ProgressBar)findViewById(R.id.progressBar);
        progressText=(TextView)findViewById(R.id.progressText1);
        button = (Button)findViewById(R.id.signupbutton);
        Signintext=(TextView)findViewById(R.id.signintext);



        findViewById(R.id.signupbutton).setOnClickListener(this);
        findViewById(R.id.signintext).setOnClickListener(this);

    /*    globalround.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                roundno= dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
*/
    }

    private void checkIfExists(){

        Email = email.getText().toString().trim();
        Password= password.getText().toString().trim();
        pass= password.getText().toString().trim();
        cpass= confirmpass.getText().toString().trim();


        if (Email.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (Password.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            email.setError("Please enter a valid email address");
            email.requestFocus();
            return;
        }
        if(Password.length()<6){
            password.setError("Minimum password length is 6");
            password.requestFocus();
            return;
        }
        if (!(pass.equals(cpass))){
            confirmpass.setError("Passwords don't match !");
            confirmpass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        Signintext.setVisibility(View.GONE);

        Query query= connect.orderByKey().equalTo(deviceId);
        query.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    registerUser();

                }
                for(com.firebase.client.DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Toast.makeText(getApplicationContext(),"You already have an account please login",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        Signintext.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    private void registerUser(){








        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        Signintext.setVisibility(View.GONE);




        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        sendEmailVerification();



                        Toast.makeText(getApplicationContext(),"Registration Successful !", Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);

                        Intent intent= new Intent(signUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        finish();

                    }
                else{
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                        Toast.makeText(getApplicationContext(), "User already exist ! ", Toast.LENGTH_SHORT).show();
                        progressText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        Signintext.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "some unknown error occurred..", Toast.LENGTH_SHORT).show();
                        progressText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        Signintext.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }



            private void sendEmailVerification() {
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();


        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                        RefferalCode r= new RefferalCode();
                        String rc= r.randString();
                        User= connect.child(deviceId);

                        childEmail = User.child("email");
                        childEmail.setValue(Email);

                        childPassword = User.child("password");
                        childPassword.setValue(Password);

                        childDeviceId = User.child("deviceid");
                        childDeviceId.setValue(deviceId);

                        lastRollTime=User.child("lastRollTime");
                        lastRollTime.setValue("0");


                        refcode=User.child("refcode");
                        refcode.setValue(rc);

                        points= User.child("points");
                        points.setValue("0");


                        lastVideoTime= User.child("lastVideoTime");
                        lastVideoTime.setValue("0");


                        lastTicketTime= User.child("lastTicketTime");
                        lastTicketTime.setValue("0");

                        mytickets=User.child("mytickets");
                        mytickets.setValue("0");

                        refstatus= User.child("refstatus");
                        refstatus.setValue("false");

                        addmycode= Reffaralslist.child(rc);
                        addmycode.setValue("0");

                        lastWithdrawal= User.child("lastWithdrawal");
                        lastWithdrawal.setValue("0");

                        localvalue= User.child("localvalue");
                        localvalue.setValue(roundno);

                        FirebaseAuth.getInstance().signOut();
                        finish();
                        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


                    }

                }
            });
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.signupbutton:

                checkIfExists();
                break;

            case R.id.signintext:

                startActivity(new Intent(signUpActivity.this, LoginActivity.class ));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
