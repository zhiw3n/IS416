package mobile.noise;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import mobile.noise.R;

public class LightActivity extends AppCompatActivity {

    private static ImageView torch;
    private Camera camera;
    private String JSON_STRING;
    private boolean runThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        torch = (ImageView) findViewById(R.id.torch_icon);

        Log.i("LightActivity", "The device has a flash: " + this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH));

        try {
            camera.release();
        } catch (Exception e) {
            Log.e("LightActivity", "Camera was already released");
        }

        camera = Camera.open();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                SurfaceTexture mTexture = new SurfaceTexture(10);
                camera.setPreviewTexture(mTexture);
            } else {
                SurfaceView mView = new SurfaceView(this);
                camera.setPreviewDisplay(mView.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LightActivity", "Failed to set preview.");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runThread) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // TODO change to correct URL.
                    new JSONTask().execute("http://processing-angeliad.rhcloud.com/getLightInfo.php");
                }
            }
        }).start();
        runThread = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        runThread = false;
        camera.release();
    }

    private void torchOff() {
        Runnable swapImage = new Runnable() {
            public void run() {
                torch.setImageResource(R.drawable.bulb_off);
            }
        };
        torch.post(swapImage);
    }

    private void torchOn() {
        Runnable swapImage = new Runnable() {
            public void run() {
                torch.setImageResource(R.drawable.bulb_on);
            }
        };
        torch.post(swapImage);
    }


    class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://processing-angeliad.rhcloud.com/getLightInfo.php");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String JSON_STRING;
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //no records inside

                Log.i("Light", "Retrieving JSON " + stringBuilder.toString().trim().isEmpty());

                if(stringBuilder.toString().trim().isEmpty()){
                    torchOff();
                    camera.stopPreview();
                } else {
                    torchOn();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                }

            } catch (Exception e ){

            }
            return null;
        }
    }
}
