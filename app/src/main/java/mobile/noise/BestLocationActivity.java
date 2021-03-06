package mobile.noise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class BestLocationActivity extends AppCompatActivity {

    private Bundle room1Bundle, room2Bundle, room3Bundle;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_best_location);

        Bundle b = getIntent().getExtras();

        Log.i("Best Location", b.toString());

        room1Bundle = b.getBundle("Room 1");
        room2Bundle = b.getBundle("Room 2");
        room3Bundle = b.getBundle("Room 3");
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            TextView v = (TextView) findViewById(R.id.ranked1Text);
            v.setText(room1Bundle.getString("Location"));

            v = (TextView) findViewById(R.id.ranked2Text);
            v.setText(room2Bundle.getString("Location"));


            v = (TextView) findViewById(R.id.ranked3Text);
            v.setText(room3Bundle.getString("Location"));
        } catch(Exception e) {
            e.printStackTrace();
            Log.e("BestLocationActivity", "Less than 3 rooms found!");

            if (room1Bundle == null) {
                ImageButton b = (ImageButton) findViewById(R.id.ranked1Btn);
                b.setEnabled(false);
            }

            if (room2Bundle == null) {
                ImageButton b = (ImageButton) findViewById(R.id.ranked2Btn);
                b.setEnabled(false);
            }

            if (room3Bundle == null) {
                ImageButton b = (ImageButton) findViewById(R.id.ranked3Btn);
                b.setEnabled(false);
            }
        }

    }

    public void firstPlaceClicked(View v) {
        Intent i = new Intent(BestLocationActivity.this, LocationResultActivity.class);
        i.putExtra("Room", room1Bundle);
        startActivity(i);
    }

    public void secondPlaceClicked(View v) {
        Intent i = new Intent(BestLocationActivity.this, LocationResultActivity.class);
        i.putExtra("Room", room2Bundle);
        startActivity(i);
    }

    public void thirdPlaceClicked(View v) {
        Intent i = new Intent(BestLocationActivity.this, LocationResultActivity.class);
        i.putExtra("Room", room3Bundle);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(BestLocationActivity.this, MainActivity.class));
    }
}
