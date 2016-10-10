package mobile.noise;

import android.app.Activity;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

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

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        Log.i(TAG, "onCreate() " + mOpenCvCameraView.toString());

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        m1 = null;
        m2 = null;
        m3 = null;
        diff1 = null;
        diff2 = null;
        result = null;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    private Mat m1, m2, m3, diff1, diff2, result, tmp;

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (m1 == null) {
            m1 = inputFrame.rgba();
            tmp = new Mat(m1.size(), CV_8UC1);
            Imgproc.cvtColor(m1, tmp, Imgproc.COLOR_RGBA2GRAY);
            m1 = tmp;
        } else if (m2 == null) {
            m2 = inputFrame.rgba();
            tmp = new Mat(m2.size(), CV_8UC1);
            Imgproc.cvtColor(m2, tmp, Imgproc.COLOR_RGBA2GRAY);
            m2 = tmp;
        } else if (m3 == null) {
            m3 = inputFrame.rgba();
            tmp = new Mat(m3.size(), CV_8UC1);
            Imgproc.cvtColor(m3, tmp, Imgproc.COLOR_RGBA2GRAY);
            m3 = tmp;

            diff1 = new Mat(m1.size(), m1.depth());
            Core.absdiff(m1, m2, diff1);

            diff2 = new Mat(m1.size(), m1.depth());
            Core.absdiff(m2, m3, diff2);

            result = new Mat(m1.size(), m1.depth());
            Core.bitwise_and(diff1, diff2, result);

            if (countNonZero(result) > 0) {
                Log.i(TAG, "There was movement with " + countNonZero(result) + " elements.");
            }

            m1 = m2;
            m2 = m3;
            m3 = null;

            return result;
        }

        return inputFrame.rgba();
    }
}