package mobile.noise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.*;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

        double[] lightGraphPoints = b.getDoubleArray("lightGraphPoints");
        double[] noiseGraphPoints = b.getDoubleArray("noiseGraphPoints");
        GraphView graph = (GraphView) findViewById(R.id.noiseGraph);


        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(0, noiseGraphPoints[0]),
                new DataPoint(1, noiseGraphPoints[1]),
                new DataPoint(2, noiseGraphPoints[2]),
                new DataPoint(3, noiseGraphPoints[3]),
                new DataPoint(4, noiseGraphPoints[4])

        });
        graph.addSeries(series);


        GraphView graph2 = (GraphView) findViewById(R.id.brightnessGraph);
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, lightGraphPoints[0]),
                new DataPoint(1, lightGraphPoints[1]),
                new DataPoint(2, lightGraphPoints[2]),
                new DataPoint(3, lightGraphPoints[3]),
                new DataPoint(4, lightGraphPoints[4])
        });
        graph2.addSeries(series2);
    }
}
