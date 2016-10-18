package mobile.noise;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;

public class Main3Activity extends Activity {

    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Main Activity", "Start button clicked.");

                serviceQueue.add(new Intent(Main3Activity.this, LightEventService.class));
                serviceQueue.add(new Intent(Main3Activity.this, AccelerometerEventService.class));
                serviceQueue.add(new Intent(Main3Activity.this, ProximityEventService.class));
                serviceQueue.add(new Intent(Main3Activity.this, AndroidCameraMotionService.class));
                // serviceQueue.add(new Intent(Main3Activity.this, NoiseEventService.class));

                for (Intent i : serviceQueue) {
                    startService(i);
                }
           }
        });

        findViewById(R.id.stopBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Main Activity", "Stop button clicked");

                for (Intent i : serviceQueue) {
                    stopService(i);
                }
                serviceQueue = new ArrayList<Intent>();
            }
        });
    }
}
