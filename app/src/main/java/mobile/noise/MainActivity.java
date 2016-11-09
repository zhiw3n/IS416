package mobile.noise;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.ArrayList;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.AndroidCameraMotionService;
import mobile.noise.mobile.noise.sensorservices.CustomOnItemSelectedListener;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.NoiseEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;
import mobile.noise.mobile.noise.sensorservices.SensorType;

public class MainActivity extends AppCompatActivity{
    private Spinner spinner1;

    private String JSON_STRING;
    public String answer;
    public boolean isRunning;
    public static boolean ifOutput;
    private TextView textView;
    public static String thiefArray;
    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();
    public static ArrayList<SensorType> sensorOn = new ArrayList<SensorType>() {{

        add(SensorType.ACCELEROMETER);
        add(SensorType.CAMERA);
        add(SensorType.LIGHT);
        add(SensorType.MICROPHONE);
        add(SensorType.PROXIMITY);

    }};


    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnSpinnerItemSelection();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    new JSONTask().execute("http://processing-angeliad.rhcloud.com/getThief.php");
                }
            }
        }).start();

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!running) {
                    addIntentsToQueue();
                    for (Intent i : serviceQueue) {
                       startService(i);
                      //  showNotification();
                        //startActivity(new Intent(MainActivity.this, NotificationPage.class));
                    }

                    ((Button) v).setText("Stop Service");
                    running = true;
                } else {
                    for (Intent i : serviceQueue) {
                        stopService(i);
                    }
                    serviceQueue = new ArrayList<Intent>();

                    ((Button) v).setText("Start Service");
                    running = false;
                }
            }
        });

        findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FloorActivity.class));
            }
        });

        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.infoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
            }
        });
   }

    private void addIntentsToQueue() {
        if (sensorOn.contains(SensorType.ACCELEROMETER)) {
            serviceQueue.add(new Intent(MainActivity.this, AccelerometerEventService.class));
        }
        if (sensorOn.contains(SensorType.CAMERA)) {
            serviceQueue.add(new Intent(MainActivity.this, AndroidCameraMotionService.class));
        }
        if (sensorOn.contains(SensorType.LIGHT)) {
            serviceQueue.add(new Intent(MainActivity.this, LightEventService.class));
        }
        if (sensorOn.contains(SensorType.MICROPHONE)) {
            serviceQueue.add(new Intent(MainActivity.this, NoiseEventService.class));
        }
        //adds Noise Services Automatically


        if (sensorOn.contains(SensorType.PROXIMITY)) {
            serviceQueue.add(new Intent(MainActivity.this, ProximityEventService.class));
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Notification!");
        builder.setContentText("Potential Movement After Hours. Please Click On Notification");
        Intent intent = new Intent(this, NotificationPage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationPage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }

    class JSONTask extends AsyncTask<String, String, String> {
        String json_url;
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;

            BufferedReader reader = null;
            try {
                URL url = new URL(params[0].trim());

                connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer StringBuffer = new StringBuffer();

                while ((JSON_STRING = reader.readLine()) != null) {

                    StringBuffer.append(JSON_STRING + "\n");
                }
                // stream.close();

                //  textView.setText(StringBuffer.toString());
                return StringBuffer.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            setContentView(R.layout.second_layout);
//            textView = (TextView) findViewById(R.id.textView2);
        //    textView.setText(result);
            if (result != null) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        thiefArray = "There is a movement at "  + json_data.getString("location")
                        + " at this time " + json_data.getString("time") ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showNotification();
             }
        }
    }
}
