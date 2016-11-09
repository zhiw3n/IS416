package mobile.noise;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by jessXW on 18/10/16.
 */
public class NotificationPage extends Activity {
    private String JSON_STRING;
    public String answer;
    public boolean isRunning;
    public static boolean ifOutput;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        textView = (TextView) findViewById(R.id.textView2);
        textView.setText(MainActivity.thiefArray);
     //   new JSONTask().execute("http://processing-angeliad.rhcloud.com/getTopRoom.php");
    }

//    public void showNotification() {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("Notification!");
//        builder.setContentText("Noisy Area Detected! Click to view more details.");
//        Intent intent = new Intent(this, NotificationPage.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(NotificationPage.class);
//        stackBuilder.addNextIntent(intent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
//        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NM.notify(0, builder.build());
//    }
//    class JSONTask extends AsyncTask<String, String, String> {
//        String json_url;
//
//        @Override
//        protected String doInBackground(String... params) {
//            HttpURLConnection connection = null;
//
//            BufferedReader reader = null;
//            try {
//                URL url = new URL(params[0].trim());
//
//                connection = (HttpURLConnection) url.openConnection();
//
//                InputStream stream = connection.getInputStream();
//
//                reader = new BufferedReader(new InputStreamReader(stream));
//
//                StringBuffer StringBuffer = new StringBuffer();
//
//                while ((JSON_STRING = reader.readLine()) != null) {
//
//                    StringBuffer.append(JSON_STRING + "\n");
//                }
//
//                // stream.close();
//
//                //  textView.setText(StringBuffer.toString());
//                return StringBuffer.toString().trim();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    if (reader != null) {
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
////            setContentView(R.layout.second_layout);
////            textView = (TextView) findViewById(R.id.textView2);
//            textView.setText(result);
//            if (result != null) {
//                // showNotification();
//                try {
//                    JSONArray jArray = new JSONArray(result);
//                    int rank = 1;
//                    for (int i = 0; i < jArray.length(); i++) {
//
//                        JSONObject json_data = jArray.getJSONObject(i);
//                        Log.i("rank " + rank + " : ", json_data.getString("location"));
//                        Log.i("noise level : ", json_data.getString("noiseAverage") + " dB");
//                        Log.i("noise group : ", json_data.getString("noise"));
//                        Log.i("light level : ", json_data.getString("lightAverage") + " Lux");
//                        Log.i("light group : ", json_data.getString("light"));
//                        Log.i("score : ", json_data.getString("score"));
//                        rank++;
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//    }
}
