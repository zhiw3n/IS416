package mobile.noise;

<<<<<<< HEAD
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
=======
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mobile.noise.R;
import mobile.noise.GetTop3Task;
>>>>>>> origin/master

public class LoadingBestLocationActivity extends AppCompatActivity {

    private GetTop3Task bgroundTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_best_location);
<<<<<<< HEAD
=======

>>>>>>> origin/master
        bgroundTask = new GetTop3Task(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bgroundTask.execute();
    }
}
