package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView detailImageView = findViewById(R.id.detailImageView);
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        String targetFile = intent.getStringExtra("targetFile");
        File file = new File(path, targetFile);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            detailImageView.setImageBitmap(scaledBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
