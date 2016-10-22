package mobile.noise;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jessXW on 18/10/16.
 */
public class NotificationPage extends Activity {
    private String JSON_STRING;
    public String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        TextView textView = (TextView) findViewById(R.id.textView2);

        new BackGroundTask().execute();
        try {
            Thread.sleep(1000);
        }catch(Exception e){

        }

        textView.setText(answer);
    }

    class BackGroundTask extends AsyncTask<Void, Void, String> {
        String json_url;
        @Override
        protected void onPreExecute() {
            // json_url = "https://processing-angeliad.rhcloud.com/getLatestNoise.php";
            json_url = "https://processing-angeliad.rhcloud.com/getLatestNoise.php";
        }
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                ;
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
                answer = result;
        }
    }

}
