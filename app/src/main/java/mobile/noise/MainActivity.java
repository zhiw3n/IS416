package mobile.noise;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import mobile.noise.mobile.noise.sensorservices.AccelerometerEventService;
import mobile.noise.mobile.noise.sensorservices.AndroidCameraMotionService;
import mobile.noise.mobile.noise.sensorservices.CustomOnItemSelectedListener;
import mobile.noise.mobile.noise.sensorservices.LightEventService;
import mobile.noise.mobile.noise.sensorservices.NoiseEventService;
import mobile.noise.mobile.noise.sensorservices.ProximityEventService;
import mobile.noise.mobile.noise.sensorservices.SensorType;

public class MainActivity extends AppCompatActivity{
    private Spinner spinner1;
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

    private static ArrayList<Intent> serviceQueue = new ArrayList<Intent>();
    public static ArrayList<SensorType> sensorOn = new ArrayList<SensorType>() {{
        add(SensorType.ACCELEROMETER);
//        add(SensorType.CAMERA);
        add(SensorType.LIGHT);
        add(SensorType.MICROPHONE);
        add(SensorType.PROXIMITY);
    }};


    public static boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnSpinnerItemSelection();
        //get Locations
        new getPointAddress().execute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
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

        findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FloorActivity.class));
            }
        });

        findViewById(R.id.settingsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.infoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InfoActivity.class));
            }
        });
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

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Notification!");
        builder.setContentText("Potential Movement After Hours. Please Click On Notification");
        Intent intent = new Intent(this, NotificationPage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationPage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NM.notify(0, builder.build());
    }
@TargetApi(23)
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
            super.onPostExecute(result);;
            if (result != null) {
                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        if(!json_data.getString("location").isEmpty()) {
                        thiefArray = "There is a movement at "  + json_data.getString("location")
                                + " at this time " + json_data.getString("time") ;
                                showNotification();
                      }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //much more work needs to be done here
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                camera1 = Camera.open() ;
               parameters = camera1.getParameters();

                if(isFlashLightOn){
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera1.setParameters(parameters);
                    camera1.stopPreview();
                    isFlashLightOn = false;
                }else{
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera1.setParameters(parameters);
                    camera1.startPreview();
                    isFlashLightOn = true;
                }
                //to flash longer, comment out the bottom 3 lines
                camera1.stopPreview();
                camera1.release();
                camera1 = null;


            }
        }
    }

    //returns the location
    class getPointAddress extends AsyncTask<String, String, String> {
        String json_url;

        @Override
        protected String doInBackground(String... params) {

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

                    return response.toString().trim();

                } catch (Exception e) {

                }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result != null) {
                try{
                    JSONObject j = new JSONObject(result);
               Iterator<String> keys= j.keys();
                    String lastKey = "";
                    while (keys.hasNext())
                    {
                        String Key = keys.next();
                        if(Key.compareTo(lastKey) >= 0){
                            lastKey=Key;
                        }
                    }
                    Log.i("LAST KEY", lastKey);

                    JSONArray z = j.getJSONArray(lastKey);
                    for(int a = 0 ; a < z.length(); a++){
                        JSONObject f = z.getJSONObject(a);
                        Log.i("time", f.get("time").toString());
                        Log.i("location", f.get("mapped_location").toString());
                    }
                }
                catch(Exception e){
                }
                try {
                    JSONArray jArray = new JSONArray(result);
                    ArrayList<String> newArray = new ArrayList();
                    JSONObject lastLocation = (JSONObject) jArray.get(jArray.length()-1);
                    pointLocation = lastLocation.toString();
                    Log.i("LAST", lastLocation.toString());
                }
                catch (Exception e){

                }

            } else{
                //if results = null;
                pointLocation = CustomOnItemSelectedListener.globalSpinnerValue;
            }
        }

    }






}
