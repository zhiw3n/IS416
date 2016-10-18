package mobile.noise;

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
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.countNonZero;

public class AndroidCameraMotionService extends Service {

    private static final int DELAY = 500;
    private static final double SENSITIVITY = 0.01;
    private static final String TAG = "AC Motion Service";
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

            Log.i(TAG, "mMat of size " + mMats.size() + " contains: " + mMats);

            if (mMats.size() >= 3) {
                Core.absdiff(mMats.get(0), mMats.get(1), mDiff1);
                Core.absdiff(mMats.get(1), mMats.get(2), mDiff2);
                Core.bitwise_and(mDiff1, mDiff2, mResult);

                Imgproc.threshold(mResult, mResult, (int) (SENSITIVITY * 255), 255, Imgproc.THRESH_TOZERO);

                if (countNonZero(mResult) > 0) {
                    Log.e(TAG, "There was movement with " + countNonZero(mResult) + " elements.");
                } else {
                    Log.i(TAG, "No movement with mDiff1: " + countNonZero(mDiff1) + " | mDiff2: " + countNonZero(mDiff2) + " | mResult: " + countNonZero(mResult));
                }

                mMats.get(0).release();
                mMats.remove(0);
            }
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }

        /*
        @Override
        public void finish() {
            // TODO destroy something.
            // http://answers.opencv.org/question/14717/using-default-baseloadercallback-in-an-android-service/
        }
        */
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
            mCamera = Camera.open();
            Log.d(TAG, "Camera has address: " + mCamera.toString());
        } catch (Exception e){
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
        mWidth = sizes.get(1).width;
        mHeight = sizes.get(1).height;
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
        // TODO start loop.
        // TODO start preview.
        // TODO take picture.

        Log.i(TAG, "Handler created at: " + SystemClock.elapsedRealtime());
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.i(TAG, "First run at: " + SystemClock.elapsedRealtime());

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
