package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityMainBinding;

public class Record extends AppCompatActivity {

    //UI
    ImageView image1, image2;

    //constant
    final int PICTURE_REQUEST_CODE = 200;
    Button circle_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment2);

        //UI
        image1 = (ImageView)findViewById(R.id.imageView1);
        image2 = (ImageView)findViewById(R.id.imageView2);

        Button btnImage = (Button)findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                //사진을 여러개 선택할수 있도록 한다
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),  PICTURE_REQUEST_CODE);
            }
        });

        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Record.this, MainActivity.class));

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
                startActivity(new Intent(Record.this, Record.class));
                circle_btn.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment fragment3 = new Fragment3();
                transaction.replace(R.id.frame, fragment3);
                transaction.commit();
                circle_btn.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //기존 이미지 지우기
                image1.setImageResource(0);
                image2.setImageResource(0);

                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                ClipData clipData = data.getClipData();

                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if (clipData != null) {

                    for (int i = 0; i < 3; i++) {
                        if (i < clipData.getItemCount()) {
                            Uri urione = clipData.getItemAt(i).getUri();
                            switch (i) {
                                case 0:
                                    image1.setImageURI(urione);
                                    break;
                                case 1:
                                    image2.setImageURI(urione);
                                    break;
                            }
                        }
                    }
                } else if (uri != null) {
                    image1.setImageURI(uri);
                }
            }
        }
    }
}