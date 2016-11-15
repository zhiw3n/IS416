package mobile.noise.mobile.noise.sensorservices;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Created by WenJiez on 11-Nov-16.
 */

public class GetLocationTask extends AsyncTask<String, String, String> {
    String json_url;

    private final String USER_AGENT = "Mozilla/5.0";
    private static String ip;
    private static String macAddress;
    public static String location = "No Room";

    @Override
    protected String doInBackground(String... params) {

        System.setProperty("java.net.preferIPv4Stack" , "true");

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String ipaddress = inetAddress.getHostAddress().toString();
                        Log.i("ip", ipaddress);
                        ip = ipaddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("MainActivity", "Exception in Get IP Address: " + ex.toString());
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
            // System.out.println("\nSending 'POST' request to URL : " + url);
            // System.out.println("Post parameters : " + urlParameters);
            // System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            // System.out.println(response.toString());
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

            String urlParameters = "ip="+ip;
            Log.i("IP Address",ip);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            // System.out.println("\nSending 'POST' request to URL : " + url);
            // System.out.println("Post parameters : " + urlParameters);
            // System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print result
            // System.out.println(response.toString());
            String[] tempNames = new String(response).split(",");
            // System.out.println(tempNames[0]);
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
            try {
                Log.e("GetLocationTaks",result);

                JSONObject j = new JSONObject(result);
                Iterator<String> keys = j.keys();

                String lastKey = "";

                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.compareTo(lastKey) >= 0) {
                        lastKey = key;
                    }
                }

                Log.i("GetLocation", "Last key is: " + lastKey);
                location = j.getJSONArray(lastKey).getJSONObject(0).get("mapped_location").toString();
                Log.i("GetLocation", "Locatio at last key is: " + location);
            } catch (Exception e) {
                location = "No Room";
                e.printStackTrace();
            }

        } else {
            location = "No Room";
            Log.e("MainActivity", "Did not get location from location API");
        }
    }

}