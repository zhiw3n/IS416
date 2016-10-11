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

/**
 * Created by Fabian Schmitt on 05-Oct-16.
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public abstract class SensorEventService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor sensor;

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
            Log.i(this.toString(), "onSensorChanged() with value " + values);
        }
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
