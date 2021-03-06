package mobile.noise;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.ArrayList;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.AndroidCameraMotionService;
import mobile.noise.mobile.noise.sensorservices.GetLocationTask;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.NoiseEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;
import mobile.noise.mobile.noise.sensorservices.SensorType;

public class MainActivity extends AppCompatActivity {
    private String JSON_STRING;
    public static String thiefArray;
    private Thread thiefThread;

    private Spinner spinner1;
    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();
    public static ArrayList<SensorType> sensorOn = new ArrayList<SensorType>();
    public static boolean notificationOn = false;
    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readPreferences();
        if (running) {
            ((Button) findViewById(R.id.startBtn)).setText("Stop Service");
        }

        // addListenerOnSpinnerItemSelection();

        new GetLocationTask().execute();

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running) {
                    addIntentsToQueue();
                    for (Intent i : serviceQueue) {
                        startService(i);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (notificationOn) {
            thiefThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()) {
                        try {
                            Thread.sleep(20000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new JSONTask().execute("http://processing-angeliad.rhcloud.com/getThief.php");
                    }
                }
            });
            thiefThread.start();
        } else {
            try {
                thiefThread.interrupt();
            } catch(Exception e) {

            }
        }

        try {
            ((TextView) findViewById(R.id.currentLocationText)).setText("Current Room: " + GetLocationTask.location);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSearch(View v) {
        startActivity(new Intent(MainActivity.this, FloorActivity.class));
    }

    public void goToRanking(View v) {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            startActivity(new Intent(MainActivity.this, LoadingBestLocationActivity.class));
        } else {
            Toast.makeText(this, "This function is only available with an active network connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToSettings(View v) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void goToInfo(View v) {
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }

    public void goToLight(View v) {
        if (running) {
            ((Button) findViewById(R.id.startBtn)).performClick();
        }
        startActivity(new Intent(MainActivity.this, LightActivity.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        writePreferences();
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

        if (sensorOn.contains(SensorType.PROXIMITY)) {
            serviceQueue.add(new Intent(MainActivity.this, ProximityEventService.class));
        }
    }

    private void readPreferences() {
        SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();

        if (!pref.contains("RUNNING")) {
            prefEditor.putBoolean("RUNNING", false);
        }
        if (!pref.contains("ACCELEROMETER")) {
            prefEditor.putBoolean("ACCELEROMETER", true);
        }
        if (!pref.contains("CAMERA")) {
            prefEditor.putBoolean("CAMERA", true);
        }
        if (!pref.contains("LIGHT")) {
            prefEditor.putBoolean("LIGHT", true);
        }
        if (!pref.contains("MICROPHONE")) {
            prefEditor.putBoolean("MICROPHONE", true);
        }
        if (!pref.contains("PROXIMITY")) {
            prefEditor.putBoolean("PROXIMITY", true);
        }
        if (!pref.contains("NOTIFICATION")) {
            prefEditor.putBoolean("NOTIFICATION", false);
        }

        prefEditor.commit();

        sensorOn = new ArrayList<>();
        if (pref.getBoolean("RUNNING", true)) {
            running = true;
        }
        if (pref.getBoolean("ACCELEROMETER", true)) {
            sensorOn.add(SensorType.ACCELEROMETER);
        }
        if (pref.getBoolean("CAMERA", true)) {
            sensorOn.add(SensorType.CAMERA);
        }
        if (pref.getBoolean("MICROPHONE", true)) {
            sensorOn.add(SensorType.MICROPHONE);
        }
        if (pref.getBoolean("LIGHT", true)) {
            sensorOn.add(SensorType.LIGHT);
        }
        if (pref.getBoolean("PROXIMITY", true)) {
            sensorOn.add(SensorType.PROXIMITY);
        }
        if(pref.getBoolean("NOTIFICATION", true)) {
            notificationOn = true;
        }

        Log.e("MAIN", sensorOn.toString());
    }

    private void writePreferences() {
        SharedPreferences.Editor prefEditor = this.getPreferences(MODE_PRIVATE).edit();

        prefEditor.putBoolean("RUNNING", running);
        prefEditor.putBoolean("ACCELEROMETER", sensorOn.contains(SensorType.ACCELEROMETER));
        prefEditor.putBoolean("CAMERA", sensorOn.contains(SensorType.CAMERA));
        prefEditor.putBoolean("MICROPHONE", sensorOn.contains(SensorType.MICROPHONE));
        prefEditor.putBoolean("LIGHT", sensorOn.contains(SensorType.LIGHT));
        prefEditor.putBoolean("PROXIMITY", sensorOn.contains(SensorType.PROXIMITY));
        prefEditor.putBoolean("NOTIFICATION", notificationOn);

        prefEditor.commit();

        Log.e("MAIN", sensorOn.toString());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /*
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    */

    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.notification);
        builder.setContentTitle("Notification!");
        builder.setContentText("Potential Movement After Hours. Click for more.");
        Intent intent = new Intent(this, NotificationInfoActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationInfoActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }

    class JSONTask extends AsyncTask<String, String, String> {

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

            if (result != null) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        if (!json_data.getString("location").isEmpty()) {
                            thiefArray = "There is a movement at " + json_data.getString("location")
                                    + " at this time " + json_data.getString("time");
                            showNotification();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
