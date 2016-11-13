package mobile.noise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LoadingBestLocationActivity extends AppCompatActivity {

    private GetTop3Task bgroundTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_best_location);
        bgroundTask = new GetTop3Task(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bgroundTask.execute();
    }
}
