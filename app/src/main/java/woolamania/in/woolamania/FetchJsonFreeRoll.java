package woolamania.in.woolamania;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;

public class FetchJsonFreeRoll extends AsyncTask<Void,Void,Void> {

    private String data= "";
    private String Timestamp;
    private long timestamp;
    @Override
    protected Void doInBackground(Void... voids) {
        /*try {
            URL url= new URL("http://www.convert-unix-time.com/api?timestamp=now");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream= httpURLConnection.getInputStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));

            String line= "";

            line= bufferedReader.readLine();

            JSONObject jsonObject= new JSONObject(line);
            timestamp= jsonObject.getLong("timestamp");


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        timestamp = System.currentTimeMillis()/1000;

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        super.onPostExecute(aVoid);
        Timestamp= String.valueOf(timestamp);
        FreeRoll.currenttime= Timestamp;

    }

}
