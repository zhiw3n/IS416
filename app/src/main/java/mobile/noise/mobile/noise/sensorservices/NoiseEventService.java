package mobile.noise.mobile.noise.sensorservices;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Spinner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;

public class NoiseEventService extends Service {
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
    private boolean isRunning;
    private static final String TAG = "Noise Service";
    private Spinner spinner1;
    MediaRecorder mRecorder;
    private String aggregatedResults;
    private  String value;
    private int Counter = 20;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate!");
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand!");
        Log.d("myTag", "CAT");
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");
        try
        {
            mRecorder.prepare();
            Log.d("myTag", "CAT");
        }catch (IOException ioe) {
            Log.e("[Monkey]", "IOException: " +
                    Log.getStackTraceString(ioe));

        }catch (SecurityException e) {
            Log.e("[Monkey]", "SecurityException: " +
                    Log.getStackTraceString(e));
        }
        try  {
            mRecorder.start();
            Log.i("myTag", "ALIVE");
        }catch (SecurityException e) {
            Log.e("[Monkey]", "SecurityException: " +
                    Log.getStackTraceString(e));
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        //get sound once every 5 seconds
                        Thread.sleep(3000);

                    } catch (Exception e) {
                        Log.i(TAG, "Problem!!!!");
                    }

                    if (isRunning != false) {

                        Log.i(TAG, "Sensor output!");
                        int amplitutde = mRecorder.getMaxAmplitude();
                        double amplitudeEMA = getAmplitudeEMA();
                        double finalValue = Math.abs(20 * Math.log10(amplitutde));
                        String result = "" + amplitutde;


                        String location = CustomOnItemSelectedListener.globalSpinnerValue;

                        String finalResult = "" + Double.toString(finalValue-10);

                        if (finalResult.contains("In")) {
                            finalResult = "70";
                        }

                         finalResult = finalResult.substring(0, 2);
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                        Date dateobj = new Date();
                        Date currentDate = new Date(System.currentTimeMillis() - 3600 * 1000);
                        String time = df.format(currentDate).toString();
                        recordNoise(time, finalResult, location);

                        Log.i("Volume Time: ", df.format(currentDate).toString());
                        Log.i("Volume Result: ", Double.toString(finalValue));
                        Log.i("ampltitudeEMA: ", Integer.toString(amplitutde));
                        Log.i("Volume Location: ", location);
                        finalValue = 0;
                    }
                }
            }
        });
        t.start();
        return Service.START_STICKY;
    }
    /*
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    */

    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public void recordNoise(String time, String result, String location )
    {
        String method = "recordNoise";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,time,result,location);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;
    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy!");
        isRunning = false;
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }
}

