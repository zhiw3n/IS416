package mobile.noise.mobile.noise.sensorservices;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opencv.core.Core.countNonZero;

public class AndroidCameraMotionService extends Service {

    private static final int DELAY = 200;
    private static final double SENSITIVITY = 0.5; // In the interval of 0 and 1.
    private static final int CAMERA_INDEX = 1;
    private static final String TAG = "AC Motion Service";
    private long lastTimestamp;
    private static final double CAMERA_DELAY = 3e3;

    private Camera mCamera;
    private SurfaceTexture mTexture;
    private SurfaceView mView;

    private int mWidth, mHeight, mType;
    private Mat mDiff1, mDiff2, mResult;
    private ArrayList<Mat> mMats;
    private boolean mRunning;

    private Camera.PreviewCallback mPicture = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Mat m = new Mat(mWidth, mHeight, mType);
            m.put(0, 0, data);
            mMats.add(m);

            if (mMats.size() >= 3) {
                Core.absdiff(mMats.get(0), mMats.get(1), mDiff1);
                Core.absdiff(mMats.get(1), mMats.get(2), mDiff2);
                Core.bitwise_and(mDiff1, mDiff2, mResult);

                if (countNonZero(mResult) > (1 - SENSITIVITY) * mWidth * mHeight) {
                    if (System.currentTimeMillis() - lastTimestamp > CAMERA_DELAY) {
                        lastTimestamp = System.currentTimeMillis();
                        Log.e(TAG, "There was movement with " + countNonZero(mResult) + " elements.");

                        String location = GetLocationTask.location;
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                        String time = df.format(new Date()).toString();
                        recordMovement(time, "1", location);
                    }
                }

                mMats.get(0).release();
                mMats.remove(0);
            }
        }
    };

    public void recordMovement(String time, String result, String location) {
        String method = "recordCamera";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, time, result, location);
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                }
                break;

                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onCreate() {
        try {
            mCamera.release();
        } catch (Exception e) {
            Log.e(TAG, "Camera was already released");
        }

        mCamera = null;
        try {
            mCamera = Camera.open(CAMERA_INDEX);
            Log.d(TAG, "Camera has address: " + mCamera.toString());
        } catch (Exception e) {
            Log.e(TAG, "Could not load the camera!");
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mTexture = new SurfaceTexture(10);
                mCamera.setPreviewTexture(mTexture);
            } else {
                mView = new SurfaceView(this);
                mCamera.setPreviewDisplay(mView.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to set preview.");
        }

        Camera.Parameters pam = mCamera.getParameters();
        pam.setColorEffect(Camera.Parameters.EFFECT_MONO);

        List<Camera.Size> sizes = pam.getSupportedPreviewSizes();
        mWidth = sizes.get(sizes.size() - 1).width;
        mHeight = sizes.get(sizes.size() - 1).height;
        pam.setPreviewSize(mWidth, mHeight);

        mType = CvType.CV_8UC1;

        // OpenCVLoader.initDebug();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mDiff1 = new Mat(mHeight, mWidth, CvType.CV_8UC1);
                mDiff2 = new Mat(mHeight, mWidth, CvType.CV_8UC1);
                mResult = new Mat(mHeight, mWidth, CvType.CV_8UC1);
                mMats = new ArrayList<Mat>();
            }
        }, 2000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Handler created at: " + SystemClock.elapsedRealtime());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if (mRunning) {
                    mCamera.startPreview();
                    mCamera.setOneShotPreviewCallback(mPicture);

                    handler.postDelayed(this, DELAY);
                }
            }
        }, 3000);
        mRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mRunning = false;
        mCamera.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
