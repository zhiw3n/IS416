package mobile.noise.mobile.noise.sensorservices;

import android.hardware.Sensor;

/**
 * Created by Fabian on 10-Oct-16.
 */

public class LightEventService extends SensorEventService {

    @Override
    public int getSensorType() {
        return Sensor.TYPE_LIGHT;
    }
}
