package mobile.noise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by jessXW on 18/10/16.
 */
public class NotificationInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(MainActivity.thiefArray);

        Log.e("NotificationInfoActivity","ThiefArray is: " + MainActivity.thiefArray);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(NotificationInfoActivity.this, MainActivity.class));
    }
}
