package mobile.noise;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;

public class MainActivity extends Activity {

    private static boolean running = false;
    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // TODO Get label of start button.
        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    addIntentsToQueue();
                    for (Intent i : serviceQueue) {
                        startService(i);
                    }
                    // TODO Change label of button to "Stop".
                } else {
                    for (Intent i : serviceQueue) {
                        stopService(i);
                    }
                    serviceQueue = new ArrayList<Intent>();
                    // TODO Change label of button to "Start".
                }
            }
        });
    }

    private void addIntentsToQueue() {
        /*
        if (findViewById(R.id.lightToggle).isChecked()) {
            serviceQueue.add(new Intent(MainActivity.this, LightEventService.class));
        }

        if (findViewById(R.id.accelToggle).isChecked()) {
            serviceQueue.add(new Intent(MainActivity.this, AccelerometerEventService.class));
        }

        if (findViewById(R.id.proxToggle).isChecked()) {
            serviceQueue.add(new Intent(MainActivity.this, ProximityEventService.class));
        }

        if (findViewById(R.id.motionToggle).isChecked()) {
            serviceQueue.add(new Intent(MainActivity.this, AndroidCameraMotionService.class));
        }

        if (findViewById(R.id.noiseToggle).isChecked()) {
            serviceQueue.add(new Intent(MainActivity.this, NoiseEventService.class));
        }
        */
    }
}
