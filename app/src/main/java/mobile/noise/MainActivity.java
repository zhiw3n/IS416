package mobile.noise;

import android.app.Activity;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC4;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setMaxFrameSize(400, 400);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        diff1 = new Mat(height, width, CvType.CV_8UC1);
        diff2 = new Mat(height, width, CvType.CV_8UC1);
        result = new Mat(height, width, CvType.CV_8UC1);

        mats = new ArrayList<Mat>();
    }

    @Override
    public void onCameraViewStopped() {
        diff1.release();
        diff2.release();
        result.release();
    }

    private Mat diff1, diff2, result;
    private ArrayList<Mat> mats;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        SystemClock.sleep(200);

        if (mats.size() < 3) {
            mats.add(inputFrame.gray());
        } else {
            mats.add(inputFrame.gray());

            Core.absdiff(mats.get(0), mats.get(1), diff1);
            Core.absdiff(mats.get(1), mats.get(2), diff2);
            Core.bitwise_and(diff1, diff2, result);

            Imgproc.threshold(result, result, 40, 255, Imgproc.THRESH_TOZERO);

            if (countNonZero(result) > 0) {
                Log.i(TAG, "There was movement with " + countNonZero(result) + " elements.");
            }

            mats.get(0).release();
            mats.remove(0);
            return result;
        }

        return inputFrame.rgba();
    }
}