package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class OpenCV extends Activity {
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is not loaded!");
        } else { Log.d(TAG, "OpenCV is loaded successfully!"); }
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }
    private Mat mRgba;
    private Mat mGray;
    private Mat mHSV;

    private Mat mIntermediateMat;
    private Bitmap mBitmap;
    private int thresh = 100;

    public static final int      VIEW_MODE_A     = 0;
    public static final int      VIEW_MODE_B     = 1;
    public static final int      VIEW_MODE_C     = 2;
    public static final int      VIEW_MODE_D     = 3;
    public static final int      VIEW_MODE_E     = 4;
    public static final int      VIEW_MODE_F     = 5;
    public static final int      VIEW_MODE_G     = 6;
    public static final int      VIEW_MODE_H     = 7;
    public static final int      VIEW_MODE_I     = 8;
    public static final int      VIEW_MODE_J     = 9;

    public static int viewMode = VIEW_MODE_A;

    public OpenCV() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        Mat time_1_file = Imgcodecs.imread("@drawable/image1.jpg", 1);
        Mat time_2_file = Imgcodecs.imread("@drawable/image2.jpg", 1);

        Mat imgRGB = new Mat();

        // Imgproc.cvtColor(time_2_file, imgRGB, Imgproc.COLOR_BGR2RGB);
    }
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC3);
        mHSV = new Mat();

        mIntermediateMat = new Mat(height, width, CvType.CV_8UC3);
        mGray = new Mat(height, width, CvType.CV_8UC1);

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();
        mIntermediateMat = null;

        if(mHSV!= null)
            mHSV.release();
        mHSV = null;

        if(mGray!= null)
            mGray.release();
        mGray = null;

        if (mIntermediateMat != null)
            mIntermediateMat.release();
        mIntermediateMat = null;

        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }


    private void findContoursTutorial(){
        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);

        Imgproc.blur(mGray, mGray, new Size(3, 3));

        Mat canny_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        /// Detect edges using canny
        Imgproc.Canny( mGray, canny_output, thresh, thresh*2, 3, false);

        /// Find contours
        Imgproc.findContours( canny_output, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0) );

        /// Draw contours
        mIntermediateMat = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
        for(int i = 0; i<contours.size(); i++) {
            Scalar color = new Scalar( getRandomUniformInt(0, 255), getRandomUniformInt(0,255), getRandomUniformInt(0,255) );
            Imgproc.drawContours( mIntermediateMat, contours, i, color, 2, 8, hierarchy, 0, new Point() );
        }
        mRgba = mIntermediateMat;
    }

    private int getRandomUniformInt(int min, int max) {
        Random r1 = new Random();
        return r1.nextInt() * (max - min) + min;
    }
}
