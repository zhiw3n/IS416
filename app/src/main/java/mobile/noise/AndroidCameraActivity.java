package mobile.noise;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import static org.opencv.core.Core.countNonZero;

public class AndroidCameraActivity extends AppCompatActivity {

    private static final String TAG = "Android Camera Activity";
    private Camera mCamera;

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "Picture taken with address: " + data.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera);

        Camera mCamera = null;
        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            Log.i(TAG, "Camera has address: " + mCamera.toString());
        }
        catch (Exception e){
            Log.e(TAG, "Could not load the camera!");
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){
            Log.i(TAG, "No preview before.");
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mCamera.setPreviewTexture(new SurfaceTexture(10));
            } else {
                SurfaceView view = new SurfaceView(this);
                mCamera.setPreviewDisplay(view.getHolder());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to set preview.");
        }

        mCamera.startPreview();
        mCamera.takePicture(null, null, mPicture);
    }
}
