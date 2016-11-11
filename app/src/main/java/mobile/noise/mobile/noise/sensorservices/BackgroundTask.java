package mobile.noise.mobile.noise.sensorservices;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by WenJie on 10/15/2016.
 */

public class BackgroundTask extends AsyncTask<String, Void, String> {
    AlertDialog alertDialog;
    Context ctx;

    BackgroundTask(Context ctx) {
        this.ctx = ctx;
    }

    private String JSON_STRING;
    public String answer;
    public static JSONObject jsonObj;

    @Override
    protected String doInBackground(String... params) {
        String addNoise_url = "https://processing-angeliad.rhcloud.com/addNoise.php";
        String addLight_url = "https://processing-angeliad.rhcloud.com/addLight.php";
        String addAccelerometer_url = "https://processing-angeliad.rhcloud.com/addAccelerometer.php";
        String addProximity_url = "https://processing-angeliad.rhcloud.com/addProximity.php";
        String getTopRoom_url = "https://processing-angeliad.rhcloud.com/getTopRoom.php";
        //to be updated

        String recordCamera_url = "https://processing-angeliad.rhcloud.com/addCamera.php";
        String method = params[0];
        if (method.equals("recordNoise")) {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                //  http://10.0.2.2:8080/practice/addNoise.php;
                URL url = new URL(addNoise_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (method.equals("recordLight")) {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addLight_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (method.equals("recordProximity")) {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addProximity_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (method.equals("recordAccelerometer")) {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addAccelerometer_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (method.equals("recordCamera")) {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(recordCamera_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                        URLEncoder.encode("result", "UTF-8") + "=" + URLEncoder.encode(result, "UTF-8") + "&" +
                        URLEncoder.encode("location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (method.equals("getTopRoom")) {

            try {
                URL url = new URL("https://processing-angeliad.rhcloud.com/getTopRoom.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                try {
                    jsonObj = new JSONObject(stringBuilder.toString());
                } catch (Exception e) {

                }
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

        //prints values
        /*
      if(result.contains("Inserted"))
      {
          Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
      }
        else
      {
        alertDialog.setMessage(result);
          alertDialog.show();
      }
    */
    }
}
