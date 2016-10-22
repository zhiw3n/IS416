package mobile.noise;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Notification extends Activity {
    EditText ET_NAME, ET_USER_NAME, ET_USER_PASS;
    String name, user_name, user_pass;
    WebView view;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_noisy_areas);
        new BackGroundTask().execute();
    }
    public void showNotification(String result) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Notification");
        builder.setContentText("Noisy Area Detected!");
        Intent intent = new Intent(this, NotificationPage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationPage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());


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
            //  int AggregatedResult = Integer.parseInt(result);
            //send notifications here
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(result);
            try {
                Thread.sleep(10000);
            } catch(Exception e ){

            }
            showNotification(result);;
            if (result != null) {
                try
                {
                    String returnString = "";
                    JSONArray jArray = new JSONArray(result);
                    for(int i=0;i<jArray.length();i++)
                    {
                        JSONObject json_data = jArray.getJSONObject(i);
                        Log.i("log_tag","Result:"+json_data.getString("Result"));
                        returnString = json_data.getString("Result");
                    }
                }
                catch(JSONException e)
                {
                    Log.e("log_tag", "Error parsing data "+e.toString());
                    Toast.makeText(getApplicationContext(), "Error Parsing",  Toast.LENGTH_LONG).show();
                }
            }

        }
    }

/*
    public void processJSON(String result) {

        int value = Integer.parseInt(result);

        if (value >= 30000) {


            //send notificaitons
        }
    }

}
*/


/*
        ET_NAME = (EditText)findViewById(R.id.name);
        ET_USER_NAME= (EditText)findViewById(R.id.new_user_name);
        ET_USER_PASS = (EditText)findViewById(R.id.new_user_pass);


    }
    public void userReg(View view)
    {

        name = ET_NAME.getText().toString();
        user_name = ET_USER_NAME.getText().toString();
        user_pass =ET_USER_PASS.getText().toString();
        String method = "register";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,name,user_name,user_pass);
        finish();

    }
*/


}