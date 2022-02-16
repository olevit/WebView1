package com.example.webview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private ImageView imageView;
    private final int IMG_GALLERY = 1;
    Bitmap galleryBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        /*mWebView = (WebView)findViewById(R.id.webView);
        mWebView.setBackgroundColor(0);*/
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery_button:
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                // мы будем обрабатывать возвращенное значение в onActivityResult
                startActivityForResult(
                        Intent.createChooser(pickIntent, "Выберите картинку"),
                        IMG_GALLERY);

                break;
            case R.id.web_button:
                writeImage();
        }
    }
    public File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (file.mkdirs()) {
            //if directory not exist
            Toast.makeText(getApplicationContext(),
                    file.getAbsolutePath() + " created",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Directory not created", Toast.LENGTH_LONG).show();
        }
        return file;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMG_GALLERY) {
                Uri selectedImageUri = data.getData();
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(galleryBitmap);
                writeImage();
            }
        }
    }
    private void writeImage() {
        //generate a unique file name from timestamp
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyMMdd-hhmmss-SSS");
        String fileName = "img" + simpleDateFormat.format(new Date()) + ".png";

        File dir = getPublicAlbumStorageDir("Cats"); // Album name
        File file = new File(dir, fileName);

        FileOutputStream fileOutputStream;
        try{
            fileOutputStream = new FileOutputStream(file);

            imageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = imageView.getDrawingCache();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    fileOutputStream);


        }catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    "FileNotFoundException: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    /*public void onStop() {
        super.onStop();
        if (galleryBitmap != null) {
            galleryBitmap.recycle();
            galleryBitmap = null;
            System.gc();
        }
    }*/
    
}