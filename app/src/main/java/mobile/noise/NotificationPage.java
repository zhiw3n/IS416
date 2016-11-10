package mobile.noise;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by jessXW on 18/10/16.
 */
public class NotificationPage extends Activity {
    private String JSON_STRING;
    public String answer;
    public boolean isRunning;
    public static boolean ifOutput;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        textView = (TextView) findViewById(R.id.textView2);
        textView.setText(MainActivity.thiefArray);

    }
}
