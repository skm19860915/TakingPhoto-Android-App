package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class DrawActivity extends AppCompatActivity {
    private ImageButton checkBtn;
    private ImageView drawImageView;
    private Bitmap updateBitmap;
    private String filePath;
    private String fileName;
    private String param;
    private String target;
    private Bitmap scaledBitmap;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        checkBtn = findViewById(R.id.checkImageButton);
        drawImageView = findViewById(R.id.drawImageView);

        final Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        fileName = intent.getStringExtra("fileName");
        param = intent.getStringExtra("param");
        target = intent.getStringExtra("target");
        File file = new File(filePath, fileName);

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            BitmapFactory.Options myOptions = new BitmapFactory.Options();
            myOptions.inDither = true;
            myOptions.inScaled = false;
            myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            myOptions.inPurgeable = true;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);

            updateBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(updateBitmap);
            if (updateBitmap.getWidth() > updateBitmap.getHeight()){
                canvas.drawCircle( updateBitmap.getWidth() / 2, updateBitmap.getHeight() / 2, updateBitmap.getHeight() / 4, paint);
            }
            else{
                canvas.drawCircle( updateBitmap.getWidth() / 2, updateBitmap.getHeight() / 2, updateBitmap.getWidth() / 4, paint);
            }
            drawImageView.setAdjustViewBounds(true);
            drawImageView.setImageBitmap(updateBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream outputStream;
                try {
                    String name = fileName.substring(0, fileName.length() - 4); //  remove .jpg from full file name
                    File tempFile = new File(filePath, name + "-2.jpg");
                    outputStream = new FileOutputStream(tempFile);
                    Bitmap fullBitmap = Bitmap.createScaledBitmap(updateBitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    fullBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", param);
                intent.putExtra("target", target);
                intent.putExtra("draw", "circle");
                startActivity(intent);
            }
        });

        drawImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                BitmapFactory.Options myOptions = new BitmapFactory.Options();
                myOptions.inDither = true;
                myOptions.inScaled = false;
                myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                myOptions.inPurgeable = true;

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.YELLOW);
                paint.setStrokeWidth(5);
                paint.setStyle(Paint.Style.STROKE);

                updateBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);

                Canvas canvas = new Canvas(updateBitmap);
                float xValue = updateBitmap.getWidth() * event.getX() / drawImageView.getWidth();
                float yValue = updateBitmap.getHeight() * event.getY() / drawImageView.getHeight();
                if (updateBitmap.getWidth() > updateBitmap.getHeight()){
                    canvas.drawCircle(xValue, yValue, updateBitmap.getHeight() / 4, paint);
                }
                else{
                    canvas.drawCircle(xValue, yValue, updateBitmap.getWidth() / 4, paint);
                }
                drawImageView.setAdjustViewBounds(true);
                drawImageView.setImageBitmap(updateBitmap);
                return true;
            }
        });
    }
}
