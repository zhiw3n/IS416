package mobile.noise;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.AndroidCameraMotionService;
import mobile.noise.mobile.noise.sensorservices.CustomOnItemSelectedListener;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.NoiseEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;
import mobile.noise.mobile.noise.sensorservices.SensorType;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner1;
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

        //start notification page
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
        //adds Noise Services Automatically
          serviceQueue.add(new Intent(MainActivity.this, NoiseEventService.class));
        /*
        if (sensorOn.contains(SensorType.MICROPHONE)) {
            sensorOn.add(new Intent(MainActivity.this, NoiseEventService.class));
        }
        */
        if (sensorOn.contains(SensorType.PROXIMITY)) {
            serviceQueue.add(new Intent(MainActivity.this, ProximityEventService.class));
        }
    }
    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
}
