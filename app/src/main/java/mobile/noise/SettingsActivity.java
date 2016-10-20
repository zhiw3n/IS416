package mobile.noise;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import mobile.noise.mobile.noise.sensorservices.SensorType;

// import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;


public class SettingsActivity extends Activity {

    String [] SPINNERLIST = {"1 sec","10 secs","20 secs","30 secs"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /*
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        MaterialBetterSpinner betterSpinner=(MaterialBetterSpinner) findViewById(R.id.duration_dropdown);
        betterSpinner.setAdapter(arrayAdapter);
        */

        findViewById(R.id.accelerometerSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.sensorOn.add(SensorType.ACCELEROMETER);
                } else {
                    try {
                        MainActivity.sensorOn.remove(SensorType.ACCELEROMETER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.cameraSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.sensorOn.add(SensorType.CAMERA);
                } else {
                    try {
                        MainActivity.sensorOn.remove(SensorType.CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.lightSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.sensorOn.add(SensorType.LIGHT);
                } else {
                    try {
                        MainActivity.sensorOn.remove(SensorType.LIGHT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.microphoneSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.sensorOn.add(SensorType.MICROPHONE);
                } else {
                    try {
                        MainActivity.sensorOn.remove(SensorType.MICROPHONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.proximitySwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.sensorOn.add(SensorType.PROXIMITY);
                } else {
                    try {
                        MainActivity.sensorOn.remove(SensorType.PROXIMITY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Set switches to correct state.
        if (!MainActivity.sensorOn.contains(SensorType.ACCELEROMETER)) {
            ((CompoundButton) findViewById(R.id.accelerometerSwitch)).setChecked(false);
        }

        if (!MainActivity.sensorOn.contains(SensorType.CAMERA)) {
            ((CompoundButton) findViewById(R.id.cameraSwitch)).setChecked(false);
        }

        if (!MainActivity.sensorOn.contains(SensorType.LIGHT)) {
            ((CompoundButton) findViewById(R.id.lightSwitch)).setChecked(false);
        }

        if (!MainActivity.sensorOn.contains(SensorType.MICROPHONE)) {
            ((CompoundButton) findViewById(R.id.microphoneSwitch)).setChecked(false);
        }

        if (!MainActivity.sensorOn.contains(SensorType.PROXIMITY)) {
            ((CompoundButton) findViewById(R.id.proximitySwitch)).setChecked(false);
        }
    }
}
