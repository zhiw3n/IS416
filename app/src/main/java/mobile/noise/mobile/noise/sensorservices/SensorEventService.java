package mobile.noise.mobile.noise.sensorservices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

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
    private static final double DELAY = 1e9;

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

            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
            String time = "" + System.currentTimeMillis() / 1000;
            String location = CustomOnItemSelectedListener.globalSpinnerValue;
            String finalResult = "" + values[0];
            time = time.toString();
            recordInput(df.format(dateobj).toString(), finalResult, location);

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
}
