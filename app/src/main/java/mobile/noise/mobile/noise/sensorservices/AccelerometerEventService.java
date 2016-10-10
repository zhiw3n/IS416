package mobile.noise.mobile.noise.sensorservices;

import android.hardware.Sensor;

/**
 * Created by Fabian on 10-Oct-16.
 */

public class AccelerometerEventService extends SensorEventService {
    @Override
    public int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public String getClassTag() {
        return "Accelerometer Sensor Event Service";
    }
}
