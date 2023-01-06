package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AndroidQ_CameraAlbum_EX extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_ALBUM = 101;

    //서버에 이미지 업로드를 파일로 하는 경우 해당 cacheFile 로 업로드 요청을 합니다.
    // 흔히 RealPath 라 불리는 경로로 보내면 퍼미션 에러가 나서 업로드 진행이 안됩니다.
    private String cacheFilePath = null;

    ImageView imageView;
    Button btnCamera;
    Button btnAlbum;
    Button btnCacheClear;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        imageView = findViewById( R.id.iamgeView );
        btnCamera = findViewById( R.id.btnCamera );
        btnAlbum = findViewById( R.id.btnAlbum );
        btnCacheClear = findViewById( R.id.btnCacheClear );

        btnCamera.setOnClickListener( this );
        btnAlbum.setOnClickListener( this );
        btnCacheClear.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {
        if ( v == btnCamera ) {
            //권한요청
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{ Manifest.permission.CAMERA }, REQUEST_CAMERA );
            }
        } else if ( v == btnAlbum ) {
            onAlbum( REQUEST_ALBUM );
        } else if ( v == btnCacheClear ) {
            //캐시파일에 존재하는 카메라 및 앨범 캐시이미지 제거
            cacheDirFileClear( );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if ( requestCode == REQUEST_CAMERA ) {
            for ( int g : grantResults ) {
                if ( g == PackageManager.PERMISSION_DENIED ) {
                    //권한거부
                    return;
                }
            }
            //임시파일 생성
            File file = createImgCacheFile( );
            cacheFilePath = file.getAbsolutePath( );
            //카메라 호출
            onCamera( REQUEST_CAMERA, file );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_OK ) {

            AlbumAdd( cacheFilePath );
            imageView.setImageBitmap( getBitmapCamera( imageView, cacheFilePath ) );

        } else if ( requestCode == REQUEST_ALBUM && resultCode == RESULT_OK ) {

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                File cacheFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( cacheFile );
                IOUtils.copy( inputStream, outputStream );

                cacheFilePath = cacheFile.getAbsolutePath( );

                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }

        } else if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_CANCELED ) {
            fileDelete( cacheFilePath );
            cacheFilePath = null;
        }
    }

    /**
     * 카메라 및 앨범관련 작업함수
     */
    //캐시파일 생성
    public File createImgCacheFile( ) {
        File cacheFile = new File( getCacheDir( ), new SimpleDateFormat( "yyyyMMdd_HHmmss", Locale.US ).format( new Date( ) ) + ".jpg" );
        return cacheFile;
    }

    //카메라 호출
    public void onCamera( int requestCode, File createTempFile ) {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        if ( intent.resolveActivity( getPackageManager( ) ) != null ) {
            if ( createTempFile != null ) {
                Uri photoURI = FileProvider.getUriForFile( this, BuildConfig.APPLICATION_ID, createTempFile );
                intent.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );
                startActivityForResult( intent, requestCode );
            }
        }
    }

    //앨범 호출
    public void onAlbum( int requestCode ) {
        Intent intent = new Intent( Intent.ACTION_PICK );
        intent.setType( MediaStore.Images.Media.CONTENT_TYPE );
        startActivityForResult( intent, requestCode );
    }

    //앨범 저장
    public void AlbumAdd( String cacheFilePath ) {
        if ( cacheFilePath == null ) return;
        BitmapFactory.Options options = new BitmapFactory.Options( );
        ExifInterface exifInterface = null;

        try {
            exifInterface = new ExifInterface( cacheFilePath );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }

        int exifOrientation;
        int exifDegree = 0;

        //사진 회전값 구하기
        if ( exifInterface != null ) {
            exifOrientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL );

            if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 ) {
                exifDegree = 90;
            } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_180 ) {
                exifDegree = 180;
            } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_270 ) {
                exifDegree = 270;
            }
        }

        Bitmap bitmap = BitmapFactory.decodeFile( cacheFilePath, options );
        Matrix matrix = new Matrix( );
        matrix.postRotate( exifDegree );

        Bitmap exifBit = Bitmap.createBitmap( bitmap, 0, 0, bitmap.getWidth( ), bitmap.getHeight( ), matrix, true );

        ContentValues values = new ContentValues( );
        //실제 앨범에 저장될 이미지이름
        values.put( MediaStore.Images.Media.DISPLAY_NAME, new SimpleDateFormat( "yyyyMMdd_HHmmss", Locale.US ).format( new Date( ) ) + ".jpg" );
        values.put( MediaStore.Images.Media.MIME_TYPE, "image/*" );
        //저장될 경로
        values.put( MediaStore.Images.Media.RELATIVE_PATH, "DCIM/AndroidQ" );
        values.put( MediaStore.Images.Media.ORIENTATION, exifDegree );
        values.put( MediaStore.Images.Media.IS_PENDING, 1 );

        Uri u = MediaStore.Images.Media.getContentUri( MediaStore.VOLUME_EXTERNAL );
        Uri uri = getContentResolver( ).insert( u, values );

        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( uri, "w", null );
            if ( parcelFileDescriptor == null ) return;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( );
            exifBit.compress( Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream );
            byte[] b = byteArrayOutputStream.toByteArray( );
            InputStream inputStream = new ByteArrayInputStream( b );

            ByteArrayOutputStream buffer = new ByteArrayOutputStream( );
            int bufferSize = 1024;
            byte[] buffers = new byte[ bufferSize ];

            int len = 0;
            while ( ( len = inputStream.read( buffers ) ) != -1 ) {
                buffer.write( buffers, 0, len );
            }

            byte[] bs = buffer.toByteArray( );
            FileOutputStream fileOutputStream = new FileOutputStream( parcelFileDescriptor.getFileDescriptor( ) );
            fileOutputStream.write( bs );
            fileOutputStream.close( );
            inputStream.close( );
            parcelFileDescriptor.close( );

            getContentResolver( ).update( uri, values, null, null );

        } catch ( Exception e ) {
            e.printStackTrace( );
        }

        values.clear( );
        values.put( MediaStore.Images.Media.IS_PENDING, 0 );
        getContentResolver( ).update( uri, values, null, null );
    }

    //이미지뷰에 뿌려질 앨범 비트맵 반환
    public Bitmap getBitmapAlbum( View targetView, Uri uri ) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( uri, "r" );
            if ( parcelFileDescriptor == null ) return null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor( );
            if ( fileDescriptor == null ) return null;

            int targetW = targetView.getWidth( );
            int targetH = targetView.getHeight( );

            BitmapFactory.Options options = new BitmapFactory.Options( );
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            int photoW = options.outWidth;
            int photoH = options.outHeight;

            int scaleFactor = Math.min( photoW / targetW, photoH / targetH );
            if ( scaleFactor >= 8 ) {
                options.inSampleSize = 8;
            } else if ( scaleFactor >= 4 ) {
                options.inSampleSize = 4;
            } else {
                options.inSampleSize = 2;
            }
            options.inJustDecodeBounds = false;

            Bitmap reSizeBit = BitmapFactory.decodeFileDescriptor( fileDescriptor, null, options );

            ExifInterface exifInterface = null;
            try {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                    exifInterface = new ExifInterface( fileDescriptor );
                }
            } catch ( IOException e ) {
                e.printStackTrace( );
            }

            int exifOrientation;
            int exifDegree = 0;

            //사진 회전값 구하기
            if ( exifInterface != null ) {
                exifOrientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL );

                if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 ) {
                    exifDegree = 90;
                } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_180 ) {
                    exifDegree = 180;
                } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_270 ) {
                    exifDegree = 270;
                }
            }

            parcelFileDescriptor.close( );
            Matrix matrix = new Matrix( );
            matrix.postRotate( exifDegree );

            Bitmap reSizeExifBitmap = Bitmap.createBitmap( reSizeBit, 0, 0, reSizeBit.getWidth( ), reSizeBit.getHeight( ), matrix, true );
            return reSizeExifBitmap;

        } catch ( Exception e ) {
            e.printStackTrace( );
            return null;
        }
    }

    //이미지뷰에 뿌려질 카메라 비트맵 반환
    public Bitmap getBitmapCamera( View targetView, String filePath ) {
        int targetW = targetView.getWidth( );
        int targetH = targetView.getHeight( );

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options( );
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile( filePath, bmOptions );

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        double scaleFactor = Math.min( photoW / targetW, photoH / targetH );
        if ( scaleFactor >= 8 ) {
            bmOptions.inSampleSize = 8;
        } else if ( scaleFactor >= 4 ) {
            bmOptions.inSampleSize = 4;
        } else {
            bmOptions.inSampleSize = 2;
        }


        bmOptions.inJustDecodeBounds = false;

        Bitmap originalBitmap = BitmapFactory.decodeFile( filePath, bmOptions );

        ExifInterface exifInterface = null;
        try {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
                exifInterface = new ExifInterface( filePath );
            }
        } catch ( IOException e ) {
            e.printStackTrace( );
        }

        int exifOrientation;
        int exifDegree = 0;

        //사진 회전값 구하기
        if ( exifInterface != null ) {
            exifOrientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL );

            if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_90 ) {
                exifDegree = 90;
            } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_180 ) {
                exifDegree = 180;
            } else if ( exifOrientation == ExifInterface.ORIENTATION_ROTATE_270 ) {
                exifDegree = 270;
            }
        }

        Matrix matrix = new Matrix( );
        matrix.postRotate( exifDegree );

        Bitmap reSizeExifBitmap = Bitmap.createBitmap( originalBitmap, 0, 0, originalBitmap.getWidth( ), originalBitmap.getHeight( ), matrix, true );
        return reSizeExifBitmap;

    }

    //앨범에서 선택한 사진이름 가져오기
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver( ).query( uri, null, null, null, null );
        try {
            if ( cursor == null ) return null;
            cursor.moveToFirst( );
            @SuppressLint("Range") String fileName = cursor.getString( cursor.getColumnIndex( OpenableColumns.DISPLAY_NAME ) );
            cursor.close( );
            return fileName;

        } catch ( Exception e ) {
            e.printStackTrace( );
            cursor.close( );
            return null;
        }
    }

    //파일삭제
    public void fileDelete( String filePath ) {
        if ( filePath == null ) return;
        try {
            File f = new File( filePath );
            if ( f.exists( ) ) {
                f.delete( );
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    //실제 앨범경로가 아닌 앱 내에 캐시디렉토리에 존재하는 이미지 캐시파일삭제
    //확장자 .jpg 필터링해서 제거
    public void cacheDirFileClear( ) {
        File cacheDir = new File( getCacheDir( ).getAbsolutePath( ) );
        File[] cacheFiles = cacheDir.listFiles( new FileFilter( ) {
            @Override
            public boolean accept( File pathname ) {
                return pathname.getName( ).endsWith( "jpg" );
            }
        } );
        if ( cacheFiles == null ) return;
        for ( File c : cacheFiles ) {
            fileDelete( c.getAbsolutePath( ) );
        }
    }
}