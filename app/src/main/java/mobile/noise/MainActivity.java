package mobile.noise;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.AndroidCameraMotionService;
import mobile.noise.mobile.noise.sensorservices.GetLocationTask;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.NoiseEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;
import mobile.noise.mobile.noise.sensorservices.SensorType;

public class MainActivity extends AppCompatActivity {
    private String JSON_STRING;
    public String answer;
    public boolean isRunning;
    public static boolean ifOutput;
    private TextView textView;
    public static String thiefArray;

    private final String USER_AGENT = "Mozilla/5.0";
    public static String ip;
    public static String macAddress;
    public static String pointLocation;

    private Camera camera1;
    private Camera.Parameters parameters;
    boolean isFlashLightOn = false;

    private CameraCaptureSession mSession;
    private CaptureRequest.Builder mBuilder;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;

    private Spinner spinner1;
    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();
    public static ArrayList<SensorType> sensorOn = new ArrayList<SensorType>();
    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readPreferences();
        if (running) {
            ((Button) findViewById(R.id.startBtn)).setText("Stop Service");
        }


        new getPointAddress().execute();
        //Threads run every 20 second to determine if there is thief.
        new GetLocationTask().execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new JSONTask().execute("http://processing-angeliad.rhcloud.com/getThief.php");
                }
            }
        }).start();

        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running) {
                    addIntentsToQueue();
                    for (Intent i : serviceQueue) {
                        startService(i);
                    }

                    ((Button) v).setText("Stop Service");
                    running = true;
                } else {
                    for (Intent i : serviceQueue) {
                        stopService(i);
                    }
                    serviceQueue = new ArrayList<Intent>();

                    ((Button) v).setText("Start Service");
                    running = false;
                }
            }
        });
    }

    public void goToSearch(View v) {
        startActivity(new Intent(MainActivity.this, FloorActivity.class));
    }

    public void goToRanking(View v) {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) {
            startActivity(new Intent(MainActivity.this, LoadingBestLocationActivity.class));
        } else {
            Toast.makeText(this, "This function is only available with an active network connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToSettings(View v) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void goToInfo(View v) {
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
    }

    @Override
    public void onStop() {
        super.onStop();
        writePreferences();
    }

    private void addIntentsToQueue() {
        if (sensorOn.contains(SensorType.ACCELEROMETER)) {
            serviceQueue.add(new Intent(MainActivity.this, AccelerometerEventService.class));
        }

        if (sensorOn.contains(SensorType.CAMERA)) {
            serviceQueue.add(new Intent(MainActivity.this, AndroidCameraMotionService.class));
        }

        if (sensorOn.contains(SensorType.LIGHT)) {
            serviceQueue.add(new Intent(MainActivity.this, LightEventService.class));
        }

        if (sensorOn.contains(SensorType.MICROPHONE)) {
            serviceQueue.add(new Intent(MainActivity.this, NoiseEventService.class));
        }

        if (sensorOn.contains(SensorType.PROXIMITY)) {
            serviceQueue.add(new Intent(MainActivity.this, ProximityEventService.class));
        }
    }

    private void readPreferences() {
        SharedPreferences pref = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();

        if (!pref.contains("RUNNING")) {
            prefEditor.putBoolean("RUNNING", false);
        }
        if (!pref.contains("ACCELEROMETER")) {
            prefEditor.putBoolean("ACCELEROMETER", true);
        }
        if (!pref.contains("CAMERA")) {
            prefEditor.putBoolean("CAMERA", true);
        }
        if (!pref.contains("LIGHT")) {
            prefEditor.putBoolean("LIGHT", true);
        }
        if (!pref.contains("MICROPHONE")) {
            prefEditor.putBoolean("MICROPHONE", true);
        }
        if (!pref.contains("PROXIMITY")) {
            prefEditor.putBoolean("PROXIMITY", true);
        }

        prefEditor.commit();

        sensorOn = new ArrayList<>();
        if (pref.getBoolean("RUNNING", true)) {
            running = true;
        }
        if (pref.getBoolean("ACCELEROMETER", true)) {
            sensorOn.add(SensorType.ACCELEROMETER);
        }
        if (pref.getBoolean("CAMERA", true)) {
            sensorOn.add(SensorType.CAMERA);
        }
        if (pref.getBoolean("MICROPHONE", true)) {
            sensorOn.add(SensorType.MICROPHONE);
        }
        if (pref.getBoolean("LIGHT", true)) {
            sensorOn.add(SensorType.LIGHT);
        }
        if (pref.getBoolean("PROXIMITY", true)) {
            sensorOn.add(SensorType.PROXIMITY);
        }

        Log.e("MAIN", sensorOn.toString());
    }

    private void writePreferences() {
        SharedPreferences.Editor prefEditor = this.getPreferences(MODE_PRIVATE).edit();

        prefEditor.putBoolean("RUNNING", running);
        prefEditor.putBoolean("ACCELEROMETER", sensorOn.contains(SensorType.ACCELEROMETER));
        prefEditor.putBoolean("CAMERA", sensorOn.contains(SensorType.CAMERA));
        prefEditor.putBoolean("MICROPHONE", sensorOn.contains(SensorType.MICROPHONE));
        prefEditor.putBoolean("LIGHT", sensorOn.contains(SensorType.LIGHT));
        prefEditor.putBoolean("PROXIMITY", sensorOn.contains(SensorType.PROXIMITY));

        prefEditor.commit();

        Log.e("MAIN", sensorOn.toString());
    }

    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Notification!");
        builder.setContentText("Potential Movement After Hours. Click for more.");
        Intent intent = new Intent(this, NotificationInfoActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationInfoActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }

    class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(params[0].trim());
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer StringBuffer = new StringBuffer();
                while ((JSON_STRING = reader.readLine()) != null) {
                    StringBuffer.append(JSON_STRING + "\n");
                }
                return StringBuffer.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ;
            if (result != null) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
<<<<<<< HEAD
                        //This is done to ensure that there are values in the location and is not false positive
                        if (!json_data.getString("location").contains("SIS")) {
                            thiefArray =  "There is a movement at " + json_data.getString("location")+"\n"
                                    + " at this time " + json_data.getString("time") +"\n" +
                                    " Please Proceed to the venue to investigate the root cause of the movement. " + "\n"
                            + "Deploy Push Notifications to This Device if necessary ";
                            showNotification();
                        }

=======
                        if (!json_data.getString("location").isEmpty()) {
                            thiefArray = "There is a movement at " + json_data.getString("location")
                                    + " at this time " + json_data.getString("time");
                            showNotification();
                        }
>>>>>>> origin/master
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

<<<<<<< HEAD
                //Prevents Concurrency Issue with Camera
                if (!sensorOn.contains(SensorType.CAMERA)) {
                    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                    camera1 = Camera.open();
                    parameters = camera1.getParameters();

                    if (isFlashLightOn) {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera1.setParameters(parameters);
                        camera1.stopPreview();
                        isFlashLightOn = false;
                    } else {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera1.setParameters(parameters);
                        camera1.startPreview();
                        isFlashLightOn = true;
                    }
                    //to flash longer, comment out the bottom 3 lines
                    camera1.stopPreview();
                    camera1.release();
                    camera1 = null;
=======

                //much more work needs to be done here
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                camera1 = Camera.open();
                parameters = camera1.getParameters();

                if (isFlashLightOn) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera1.setParameters(parameters);
                    camera1.stopPreview();
                    isFlashLightOn = false;
                } else {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera1.setParameters(parameters);
                    camera1.startPreview();
                    isFlashLightOn = true;
                }


            }
        }
    }

    //returns the location
    class getPointAddress extends AsyncTask<String, String, String> {
        String json_url;

        @Override
        protected String doInBackground(String... params) {
            try {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface
                            .getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = (NetworkInterface) en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf
                                .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()) {
                                String ipaddress = inetAddress.getHostAddress().toString();
                                Log.i("ip", ipaddress);
                                ip = ipaddress;
                            }
                        }
                    }
                } catch (SocketException ex) {
                    //  Log.e(TAG, "Exception in Get IP Address: " + ex.toString());
                }
                try {
                    String url = "http://hestia/analytics_sandbox/smulabs/index.php/login/signin_2";
                    URL obj = new URL(url);
                    HttpURLConnection con = null;
                    BufferedReader reader = null;
                    con = (HttpURLConnection) obj.openConnection();

                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    String urlParameters = "username=group6&password=group654321";

                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
//                System.out.println("\nSending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + urlParameters);
//                System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
//                System.out.println(response.toString());
                    Log.i("response", response.toString());
                } catch (Exception e) {

                }

                try {
                    String url = "http://hestia/analytics_sandbox/smulabs/index.php/get_mac_from_ip";
                    URL obj = new URL(url);
                    HttpURLConnection con = null;
                    BufferedReader reader = null;
                    con = (HttpURLConnection) obj.openConnection();


                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    String urlParameters = "ip=10.124.131.91";

                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
//                System.out.println("\nSending 'POST' request to URL : " + url);
//                System.out.println("Post parameters : " + urlParameters);
//                System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    //print result
//                System.out.println(response.toString());
                    String[] tempNames = new String(response).split(",");
//                System.out.println(tempNames[0]);
                    macAddress = tempNames[0].substring(8, tempNames[0].length() - 1);
                    //   Log.i("macAddress", macAddress);
                } catch (Exception e) {

                }
                try {
                    String url = "http://hestia/analytics_sandbox/smulabs/index.php/Point_location/getUserLocationByMAC";
                    URL obj = new URL(url);
                    HttpURLConnection con = null;
                    BufferedReader reader = null;
                    con = (HttpURLConnection) obj.openConnection();

                    //add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", USER_AGENT);
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    String urlParameters = "encoded_mac=" + macAddress;

                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    //    System.out.println("\nSending 'POST' request to URL : " + url);
                    //    System.out.println("Post parameters : " + urlParameters);
                    //    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString().trim();

                } catch (Exception e) {

                }
            } catch (Exception e) {
                return pointLocation = CustomOnItemSelectedListener.globalSpinnerValue;
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                try {
                    JSONObject j = new JSONObject(result);
                    Iterator<String> keys = j.keys();
                    String lastKey = "";
                    while (keys.hasNext()) {
                        String Key = keys.next();
                        if (Key.compareTo(lastKey) >= 0) {
                            lastKey = Key;
                        }
                    }
                    Log.i("LAST KEY", lastKey);

                    JSONArray z = j.getJSONArray(lastKey);
                    for (int a = 0; a < z.length(); a++) {
                        JSONObject f = z.getJSONObject(a);
                        Log.i("time", f.get("time").toString());
                        Log.i("location", f.get("mapped_location").toString());
                    }
                } catch (JSONException e) {

                        Log.i("JSON Error",e.toString());
                }

                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    JSONObject lastLocation = (JSONObject) jArray.get(jArray.length() - 1);
                    pointLocation = lastLocation.toString();
                    //   Log.i("LAST", lastLocation.toString());
                } catch (Exception e) {

                }

            } else {
                //if results = null;
                pointLocation = CustomOnItemSelectedListener.globalSpinnerValue;
            }
        }

    }
}
