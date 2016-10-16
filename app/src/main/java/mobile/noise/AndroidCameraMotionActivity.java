package mobile.noise;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AndroidCameraMotionActivity extends AppCompatActivity {

    private Intent mMotionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera_motion);

        mMotionIntent = new Intent(this, AndroidCameraMotionService.class);
        startService(mMotionIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopService(mMotionIntent);
    }
}
