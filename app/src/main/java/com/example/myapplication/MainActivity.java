package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
//import android.support.v4.app.Fragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.SeekBar;
import android.widget.TextView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    //    static {
//        System.loadLibrary("opencv_java4");
//        System.loadLibrary("native-lib");
//    }
//
//
//    ImageView imageVIewInput;
//    ImageView imageVIewOuput;
//    private Mat img_input;
//    private Mat img_output;
//    private int threshold1=50;
//    private int threshold2=150;
//
//    private static final String TAG = "opencv";
    private final int GET_GALLERY_IMAGE = 200;


//    boolean isReady = false;

    final private static String TAG = "tag";
    Button circle_btn;
    final static int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        imageVIewInput = (ImageView)findViewById(R.id.imageViewInput);
//        imageVIewOuput = (ImageView)findViewById(R.id.imageViewOutput);

//        iv_photo = findViewById(R.id.iv_photo);
        circle_btn = findViewById(R.id.circle_button);

        // HTTPFunc.java 실행 부분
        HttpFunc httpFunc = new HttpFunc("url");
        httpFunc.execute();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        circle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.circle_button:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PICTURE);
                        break;
                }


            }
        });


        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment1 = new Fragment1();
                transaction.replace(R.id.frame, fragment1);
                transaction.commit();
                circle_btn.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, Record.class));
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                Fragment fragment2 = new Fragment2();
//                transaction.replace(R.id.frame, fragment2);
//                transaction.commit();
                circle_btn.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, Record.class));
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                Fragment fragment3 = new Fragment3();
//                transaction.replace(R.id.frame, fragment3);
//                transaction.commit();
//                circle_btn.setVisibility(View.GONE);
            }
        });
    }
}

//        Button Button = (Button)findViewById(R.id.button);
//        Button.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
//
//                imageprocess_and_showResult(threshold1, threshold2);
//            }
//        });


//        imageVIewInput.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("IntentReset")
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                startActivityForResult(intent, GET_GALLERY_IMAGE);
//            }
//        });
//
//
//        final TextView textView1 = (TextView)findViewById(R.id.textView_threshold1);
//        SeekBar seekBar1=(SeekBar)findViewById(R.id.seekBar_threshold1);
//        seekBar1.setProgress(threshold1);
//        seekBar1.setMax(200);
//        seekBar1.setMin(0);
//        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                threshold1 = progress;
//                textView1.setText(threshold1+"");
//                imageprocess_and_showResult(threshold1, threshold2);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        final TextView textView2 = (TextView)findViewById(R.id.textView_threshold2);
//        SeekBar seekBar2=(SeekBar)findViewById(R.id.seekBar_threshold2);
//        seekBar2.setProgress(threshold2);
//        seekBar2.setMax(200);
//        seekBar2.setMin(0);
//        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                threshold2 = progress;
//                textView2.setText(threshold2+"");
//                imageprocess_and_showResult(threshold1, threshold2);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//
//        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
//            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        isReady = true;
//    }
//
//    public native void imageprocessing(long inputImage, long outputImage, int th1, int th2);
//
//    private void imageprocess_and_showResult(int th1, int th2) {
//
//        if (isReady==false) return;
//
//        if (img_output == null)
//            img_output = new Mat();
//
//        imageprocessing(img_input.getNativeObjAddr(), img_output.getNativeObjAddr(), th1, th2);
//
//
//        Bitmap bitmapOutput = Bitmap.createBitmap(img_output.cols(), img_output.rows(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(img_output, bitmapOutput);
//        imageVIewOuput.setImageBitmap(bitmapOutput);
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == GET_GALLERY_IMAGE) {
//
//
//            if (data.getData() != null) {
//                Uri uri = data.getData();
//
//                try {
//                    String path = getRealPathFromURI(uri);
//                    int orientation = getOrientationOfImage(path); // 런타임 퍼미션 필요
//                    Bitmap temp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                    Bitmap bitmap = getRotatedBitmap(temp, orientation);
//                    imageVIewInput.setImageBitmap(bitmap);
//
//                    img_input = new Mat();
//                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                    Utils.bitmapToMat(bmp32, img_input);
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//    }
//
//
//    private String getRealPathFromURI(Uri contentUri) {
//
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
//        cursor.moveToFirst();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//        return cursor.getString(column_index);
//    }
//
//    // 출처 - http://snowdeer.github.io/android/2016/02/02/android-image-rotation/
//    public int getOrientationOfImage(String filepath) {
//        ExifInterface exif = null;
//
//        try {
//            exif = new ExifInterface(filepath);
//        } catch (IOException e) {
//            Log.d("@@@", e.toString());
//            return -1;
//        }
//
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//
//        if (orientation != -1) {
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    return 90;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    return 180;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    return 270;
//            }
//        }
//
//        return 0;
//    }
//
//    public Bitmap getRotatedBitmap(Bitmap bitmap, int degrees) throws Exception {
//        if(bitmap == null) return null;
//        if (degrees == 0) return bitmap;
//
//        Matrix m = new Matrix();
//        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
//
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//    }
//
//
//
//    // 퍼미션 코드
//    static final int PERMISSION_REQUEST_CODE = 1;
//    String[] PERMISSIONS  = {"android.permission.WRITE_EXTERNAL_STORAGE"};
//
//    private boolean hasPermissions(String[] permissions) {
//        int ret = 0;
//        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
//        for (String perms : permissions){
//            ret = checkCallingOrSelfPermission(perms);
//            if (!(ret == PackageManager.PERMISSION_GRANTED)){
//                //퍼미션 허가 안된 경우
//                return false;
//            }
//
//        }
//        //모든 퍼미션이 허가된 경우
//        return true;
//    }
//
//    private void requestNecessaryPermissions(String[] permissions) {
//        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//        }
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
//        switch (permsRequestCode) {
//
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                        if (!writeAccepted) {
//                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
//                            return;
//                        }
//                    }
//                }
//                break;
//        }
//    }
//
//    private void showDialogforPermission(String msg) {
//
//        final AlertDialog.Builder myDialog = new AlertDialog.Builder(  MainActivity.this);
//        myDialog.setTitle("알림");
//        myDialog.setMessage(msg);
//        myDialog.setCancelable(false);
//        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
//                }
//
//            }
//        });
//        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                finish();
//            }
//        });
//        myDialog.show();
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGECAPTURE && resultCode == RESULT_OK) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
//            ExifInterface exif = null;
//
//            try {
//                exif = new ExifInterface(imageFilePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int exifOrientation;
//            int exifDegree;
//
//            if (exif != null) {
//                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                exifDegree = exifOrientationToDegress(exifOrientation);
//            } else {
//                exifDegree = 0;
//            }
//
//            ((ImageView) findViewById(R.id.imageViewOutput)).setImageBitmap(rotate(bitmap.exifDegree));
//        }
//    }