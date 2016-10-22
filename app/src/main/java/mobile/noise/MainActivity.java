package mobile.noise;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    public static ArrayList<SensorType> sensorOn = new ArrayList<SensorType>();
    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readPreferences();
        if (running) {
            ((Button) findViewById(R.id.startBtn)).setText("Stop Service");
        }

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

        prefEditor.commit();

        Log.e("MAIN", sensorOn.toString());
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
}
