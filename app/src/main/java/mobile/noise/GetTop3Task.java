package mobile.noise;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
/**
 * Created by WenJiez on 26-10-16.
 */

public class GetTop3Task extends AsyncTask<Void, Void, String> {

    private String json_url;
    private String graph_url;
    private Activity loaderActivity;

    public GetTop3Task(Activity loaderActivity) {
        this.loaderActivity = loaderActivity;
    }

    @Override
    protected void onPreExecute() {
        json_url = "https://processing-angeliad.rhcloud.com/getTopRoom.php";

    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            URL url = new URL(json_url);

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

            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    
    @Override
    protected void onPostExecute(String result) {
         try {
            Intent intent = new Intent(loaderActivity, BestLocationActivity.class);

             JSONArray jArray = new JSONArray(result);
             for(int i=0; i < jArray.length(); i++)
             {
                 JSONObject json_data = jArray.getJSONObject(i);
                 Log.i("rank " + (i+1) + " : ", json_data.getString("location"));
                 Log.i("noise level : ", json_data.getString("noiseAverage")+" dB");
                 Log.i("noise group : ", json_data.getString("noise"));
                 Log.i("light level : ", json_data.getString("lightAverage")+" Lux");
                 Log.i("light group : ", json_data.getString("light"));
                 Log.i("score : ", json_data.getString("score"));


                 Bundle roomBundle = new Bundle();
                 roomBundle.putString("Location", json_data.getString("location"));

                 roomBundle.putString("NoiseLevel", json_data.getString("noiseAverage")+" dB");
                 roomBundle.putString("NoiseGroup", json_data.getString("noise"));
                 roomBundle.putString("LightLevel", json_data.getString("lightAverage")+" Lux");
                 roomBundle.putString("LightGroup", json_data.getString("light"));
                 roomBundle.putString("Score", json_data.getString("score"));
                 JSONArray noiseGraphPoints = json_data.getJSONArray("noiseGraphPoints");
                double[] list = new double[5];
                 for (int s=0;s<noiseGraphPoints.length();s++){
                     list[s]=noiseGraphPoints.getDouble(s);

                 }

                 JSONArray lightGraphPoints = json_data.getJSONArray("lightGraphPoints");
                 double[] lightList = new double[5];
                 for (int s=0;s<lightGraphPoints.length();s++){
                     lightList[s]=lightGraphPoints.getDouble(s);

                 }
                 roomBundle.putDoubleArray("noiseGraphPoints",list);
                 roomBundle.putDoubleArray("lightGraphPoints",lightList);
                 intent.putExtra("Room " + (i+1), roomBundle);
             }

             loaderActivity.startActivity(intent);

        } catch(Exception e) {
             Log.e("Get Top 3", "Error parsing data "+e.toString());
             Log.e("Get Top 3", "Retrying...");
             this.execute();
        }

    }




}
