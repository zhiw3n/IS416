package mobile.noise;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import mobile.noise.mobile.noise.sensorservices.SensorType;

// import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinner = (Spinner) findViewById(R.id.durationSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.duration_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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

        findViewById(R.id.notificationSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CompoundButton) v).isChecked()) {
                    MainActivity.notificationOn = true;
                } else {
                    MainActivity.notificationOn = false;
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

        if (MainActivity.running) {
            ((CompoundButton) findViewById(R.id.accelerometerSwitch)).setEnabled(false);
            ((CompoundButton) findViewById(R.id.cameraSwitch)).setEnabled(false);
            ((CompoundButton) findViewById(R.id.lightSwitch)).setEnabled(false);
            ((CompoundButton) findViewById(R.id.microphoneSwitch)).setEnabled(false);
            ((CompoundButton) findViewById(R.id.proximitySwitch)).setEnabled(false);
        }

        if (!MainActivity.notificationOn) {
            ((CompoundButton) findViewById(R.id.notificationSwitch)).setChecked(false);
        }
    }
}
