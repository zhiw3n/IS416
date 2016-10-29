package mobile.noise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LocationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_result);

        Bundle b = getIntent().getExtras().getBundle("Room");

        TextView v = (TextView) findViewById(R.id.roomText);
        v.setText(b.getString("Location"));

        v = (TextView) findViewById(R.id.brightnessText);
        v.setText("Current brightness: " + b.getString("LightGroup") + " (" + b.getString("LightLevel") + ")");

        v = (TextView) findViewById(R.id.noiseText);
        v.setText("Current noise: " + b.getString("NoiseGroup") + " (" + b.getString("NoiseLevel") + ")");
    }
}
