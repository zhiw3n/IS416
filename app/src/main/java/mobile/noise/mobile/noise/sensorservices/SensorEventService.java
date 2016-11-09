package mobile.noise.mobile.noise.sensorservices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fabian Schmitt on 05-Oct-16.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public abstract class SensorEventService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor sensor;
    private String method;
    private long lastTimestamp;
    private static final double DELAY = 3e9;

    private String JSON_STRING;
    public String answer;
    public boolean isRunning;
    public static boolean ifOutput;
    private TextView textView;

    abstract public int getSensorType();

    @Override
    public void onCreate() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(this.getSensorType());
        lastTimestamp = 0;
        Log.i(this.toString(), "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i(this.toString(), "onStartCommand()");
        return Service.START_STICKY;
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
          Log.i(this.toString(), "onDestroy()");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float[] values = sensorEvent.values;
        if (sensorEvent.timestamp - lastTimestamp > DELAY) {
            lastTimestamp = sensorEvent.timestamp;
            Log.i(this.toString(), "onSensorChanged() with value " + values[0]);

            String finalResult = "" + values[0];

            DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            Date dateobj = new Date();
           // Date currentDate = new Date(System.currentTimeMillis() - 3600 * 1000);
            Date currentDate = new Date();
            String time = df.format(currentDate).toString();
            recordInput(time, finalResult, CustomOnItemSelectedListener.globalSpinnerValue);
       }
    }

    public void recordInput(String time, String result, String location )
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(this.getSensorType());
        String sensorName = sensor.toString();

        if(sensorName.contains("Light")){
            method = "recordLight";
        }
        if(sensorName.contains("Proximity")){
            method = "recordProximity";
        }
        if(sensorName.contains("Accel")){
            method = "recordAccelerometer";
        }

        if(sensorName.contains("Camera")){
            method = "recordCamera";
        }

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,time,result,location);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do nothing.
    }
    @Override
    public String toString() {
        return sensor.getName() + "Sensor Event Service";
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
            textView.setText(result);
            if (result != null) {

                try {
                    JSONArray jArray = new JSONArray(result);
                    int rank = 1;
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json_data = jArray.getJSONObject(i);
                        Log.i("rank " + rank + " : ", json_data.getString("location"));
                        Log.i("noise level : ", json_data.getString("noiseAverage") + " dB");
                        Log.i("noise group : ", json_data.getString("noise"));
                        Log.i("light level : ", json_data.getString("lightAverage") + " Lux");
                        Log.i("light group : ", json_data.getString("light"));
                        Log.i("score : ", json_data.getString("score"));
                        rank++;

                    }
                    // showNotification(JSONArray jArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
