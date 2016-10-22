package mobile.noise.mobile.noise.sensorservices;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by prabeesh on 7/14/2015.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {
  AlertDialog alertDialog;
    Context ctx;
    BackgroundTask(Context ctx)
    {
      this.ctx =ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String addNoise_url = "https://processing-angeliad.rhcloud.com/addNoise.php";
        String addLight_url = "https://processing-angeliad.rhcloud.com/addLight.php";
        String addAccelerometer_url = "https://processing-angeliad.rhcloud.com/addAccelerometer.php";
        String addProximity_url = "https://processing-angeliad.rhcloud.com/addProximity.php";
        String getLatestNoise_url = "https://processing-angeliad.rhcloud.com/getLatestNoise.php";

        String method = params[0];
       if(method.equals("recordNoise"))
        {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addNoise_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data = URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"+
                        URLEncoder.encode("result","UTF-8")+"="+URLEncoder.encode(result,"UTF-8")+"&"+
                        URLEncoder.encode("location","UTF-8")+"="+URLEncoder.encode(location,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
              return "Sensor Values Inserted";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        else if(method.equals("recordLight"))
        {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addLight_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data = URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"+
                        URLEncoder.encode("result","UTF-8")+"="+URLEncoder.encode(result,"UTF-8")+"&"+
                        URLEncoder.encode("location","UTF-8")+"="+URLEncoder.encode(location,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(method.equals("recordProximity"))
        {
            String time = params[1];
            String result = params[2];
            String location = params[3];

            try {
                URL url = new URL(addProximity_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data = URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"+
                        URLEncoder.encode("result","UTF-8")+"="+URLEncoder.encode(result,"UTF-8")+"&"+
                        URLEncoder.encode("location","UTF-8")+"="+URLEncoder.encode(location,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream IS = httpURLConnection.getInputStream();
                IS.close();

                httpURLConnection.disconnect();
                return "Sensor Values Inserted";
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
       else if(method.equals("recordAccelerometer"))
       {
           String time = params[1];
           String result = params[2];
           String location = params[3];

           try {
               URL url = new URL(addAccelerometer_url);
               HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
               httpURLConnection.setRequestMethod("POST");
               httpURLConnection.setDoOutput(true);
               httpURLConnection.setDoInput(true);
               OutputStream outputStream = httpURLConnection.getOutputStream();
               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

               String data = URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8")+"&"+
                       URLEncoder.encode("result","UTF-8")+"="+URLEncoder.encode(result,"UTF-8")+"&"+
                       URLEncoder.encode("location","UTF-8")+"="+URLEncoder.encode(location,"UTF-8");

               bufferedWriter.write(data);
               bufferedWriter.flush();
               bufferedWriter.close();
               outputStream.close();

               InputStream IS = httpURLConnection.getInputStream();
               IS.close();

               httpURLConnection.disconnect();
               return "Sensor Values Inserted";
           } catch (IOException e) {
               e.printStackTrace();
           }

       }
       else if(method.equals("getLatestNoise"))
        {
            String login_name = params[1];
            String login_pass = params[2];
            try {
                URL url = new URL(addNoise_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("login_name","UTF-8")+"="+URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("login_pass","UTF-8")+"="+URLEncoder.encode(login_pass,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
