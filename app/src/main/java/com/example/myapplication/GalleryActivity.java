package com.example.myapplication;

import static android.media.MediaRecorder.VideoSource.CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GalleryActivity extends AppCompatActivity {
    public Intent intent;
    String imageDate;
    String imagePath;
    ImageView imageView;
    final int GALLERY = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // 권한 체크
        boolean hasCamPerm = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        boolean hasWritePerm = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        if (!hasCamPerm || !hasWritePerm)  // 권한 없을 시  권한설정 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        //	카메라 버튼 이벤트
        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File imageFile = null;
                    try {
                        imageFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (imageFile != null) {
                        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.myapplication.fileprovider",
                                imageFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, CAMERA); // final int CAMERA = 100;
                    }
                }
            }
        });

        // 갤러리 버튼 이벤트
        findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY); // final int GALLERY = 101;
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    File createImageFile() throws IOException {
    //	이미지 파일 생성
    	SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");

        String timeStamp = imageDate.format(String.valueOf(new Date())); // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
        String fileName = "IMAGE_" + timeStamp; // 이미지 파일 명
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(fileName, ".jpg", storageDir); // 이미지 파일 생성
        imagePath = file.getAbsolutePath(); // 파일 절대경로 저장하기, String
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) { // 결과가 있을 경우
            Bitmap bitmap = null;

            switch (requestCode) {
                case GALLERY: // 갤러리에서 이미지로 선택한 경우
//				1) 이미지 절대경로로 이미지 세팅하기
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        imagePath = cursor.getString(index);
                        cursor.close();
                    }

//				2) InputStream 으로 이미지 세팅하기
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA: // 카메라로 이미지 가져온 경우
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2; // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨

                    bitmap = BitmapFactory.decodeFile(imagePath, options);
            }
            imageView.setImageBitmap(bitmap);
        }
    }
}
