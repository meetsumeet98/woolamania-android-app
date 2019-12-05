package woolamania.in.woolamania;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LotteryWinners extends AppCompatActivity {

    public static String winners;


    private TextView a,b,c,d,e,round,resultText;

    private Firebase result;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_winners);
        if(!MainActivity.isConnected(LotteryWinners.this)){
            Toast.makeText(getApplicationContext(),"No internet connection! ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Firebase.setAndroidContext(this);

        resultText=findViewById(R.id.result);

        result= new Firebase("https://woolamania.firebaseio.com/winnerslist/resultText");
        result.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s= dataSnapshot.getValue(String.class);
                resultText.setText("Result of round "+LotteryActivity.round_no+" : "+s);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        round=findViewById(R.id.round);
        a=findViewById(R.id.first);
        b=findViewById(R.id.second);
        c=findViewById(R.id.third);
        d=findViewById(R.id.fourth);
        e= findViewById(R.id.fifth);


        round.setText("Winners of Round : "+LotteryActivity.round_no);
        if(!winners.isEmpty()) {
            a.setText(winners.substring(0, 20));
            b.setText(winners.substring(20,40 ));
            c.setText(winners.substring(40, 60));
            d.setText(winners.substring(60, 80));
            e.setText(winners.substring(80, 100));

        }
    }
}
