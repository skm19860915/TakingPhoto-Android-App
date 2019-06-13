package com.sharemycoach.rmx.rmxrecorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class AnnotateActivity extends AppCompatActivity {
    private ImageView annotateImageView;
    private String filePath;
    private String fileName;
    private String param;
    private String target;
    private ImageButton backBtn;
    private String eventName;
    private String otherMsg;
    private Bitmap bitmap;
    private Bitmap scaledBitmap;
    private CheckBox serviceCheckBox;

    private String annotateFileName;
    private String serviceFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);

        Button otherBtn = findViewById(R.id.otherButton);
        Button brokenBtn = findViewById(R.id.brokenButton);
        Button crackBtn = findViewById(R.id.crackButton);
        Button bentBtn = findViewById(R.id.bentButton);
        Button missingBtn = findViewById(R.id.missingButton);
        Button holeBtn = findViewById(R.id.holeButton);
        Button dentBtn = findViewById(R.id.dentButton);
        Button scratchBtn = findViewById(R.id.scratchButton);
        Button chipBtn = findViewById(R.id.chipButton);
        annotateImageView = findViewById(R.id.annotateImageView);
        backBtn = findViewById(R.id.backButton);
        serviceCheckBox = findViewById(R.id.serviceCheckBox);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        fileName = intent.getStringExtra("fileName");
        param = intent.getStringExtra("param");
        target = intent.getStringExtra("target");
        otherMsg = intent.getStringExtra("otherMsg");
        backBtn.setEnabled(false);

        final File file = new File(filePath, fileName);
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            annotateImageView.setImageBitmap(scaledBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (otherMsg != null){
            String format = GetFormat(otherMsg);
            CaptureTargetImage(format);
        }

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OtherActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("fileName", fileName);
                intent.putExtra("param", param);
                intent.putExtra("target", target);

                intent.putExtra("msg", otherMsg);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", param);
                intent.putExtra("target", target);

                intent.putExtra("msg", otherMsg);
                startActivity(intent);
                finish();
            }
        });

        brokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Broken");
            }
        });

        crackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Crack");
            }
        });

        bentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Bent");
            }
        });

        missingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Missing");
            }
        });

        holeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Hole");
            }
        });

        dentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Dent");
            }
        });

        scratchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Scratch");
            }
        });

        chipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureTargetImage("Chip");
            }
        });

        serviceCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceCheckBox.isChecked()){
                    if (annotateFileName == null){
                        serviceCheckBox.setChecked(false);
                        Toasty.error(getApplication(), "Please select annotate name!", Toast.LENGTH_LONG, true).show();
                        return;
                    }
                    SaveAsServiceName();
                }
                else{
                    SaveAsNoneServiceName();
                }
            }
        });
    }

    private String GetFormat(String string) {
        String otherMsgFormat = "";
        String[] token = string.split(" ");
        for (int i = 0; i < token.length; i++){
            otherMsgFormat += token[i] + "-";
        }
        otherMsgFormat = otherMsgFormat.substring(0, otherMsgFormat.length() - 1);
        return otherMsgFormat;
    }

    private void CaptureTargetImage(String str) {
        android.graphics.Bitmap.Config bitmapConfig = scaledBitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        scaledBitmap = scaledBitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(scaledBitmap);
        int padding;
        Paint textPaint = new TextPaint();
        if (scaledBitmap.getWidth() > scaledBitmap.getHeight()){
            textPaint.setTextSize(scaledBitmap.getHeight() / 25);
            textPaint.setStrokeWidth(scaledBitmap.getHeight() / 80);
            padding = 15;
        }
        else{
            textPaint.setTextSize(scaledBitmap.getWidth() / 25);
            textPaint.setStrokeWidth(scaledBitmap.getWidth() / 80);
            padding = 30;
        }

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setColor(Color.BLACK);

        Rect rect = new Rect();
        String sampleString = "eee";
        textPaint.getTextBounds(sampleString, 0, sampleString.length(), rect);

        canvas.drawText(str, scaledBitmap.getWidth() / 2 - textPaint.measureText(str) / 2, rect.height() + padding, textPaint);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        canvas.drawText(str, scaledBitmap.getWidth() / 2 - textPaint.measureText(str) / 2, rect.height() + padding, textPaint);

        annotateImageView.setImageBitmap(scaledBitmap);
        backBtn.setEnabled(true);
        eventName = str;
        SaveImage();
    }

    private void SaveImage() {
        String name = fileName.substring(0, fileName.length() - 4); //  remove .jpg from full file name
        String key = name.substring(name.length() - 2, name.length()); // get '-2' string from file name
        String customName;
        if (key.equals("-2")){
            File tempFile = new File(filePath, fileName);
            tempFile.delete();
            customName = name.substring(0, name.length() - 2);
        }
        else
            customName = name;

        FileOutputStream outputStream;
        annotateFileName = customName + "_" + eventName + "-A.jpg";
        try {
            File outFile = new File(filePath, annotateFileName);
            outputStream = new FileOutputStream(outFile);
            Bitmap fullBitmap = Bitmap.createScaledBitmap(scaledBitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            fullBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toasty.success(getApplication(), annotateFileName + " has been saved in successfully !", Toast.LENGTH_LONG, true).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toasty.error(getApplication(), "File is not created", Toast.LENGTH_LONG, true).show();
        }
    }

    private void SaveAsServiceName(){
        String name = annotateFileName.substring(0, annotateFileName.length() - 6); // remove "-A.jpg" from full file name
        serviceFileName = name + "_(SERVICE).jpg";
        File annotateFile = new File(filePath, annotateFileName);
        File serviceFile = new File(filePath, serviceFileName);
        if (annotateFile.exists()){
            annotateFile.renameTo(serviceFile);
            Toasty.success(getApplication(), serviceFileName + " has been saved in successfully !", Toast.LENGTH_LONG, true).show();
        }
    }

    private void SaveAsNoneServiceName(){
        File serviceFile = new File(filePath, serviceFileName);
        File annotateFile = new File(filePath, annotateFileName);
        if (serviceFile.exists()){
            serviceFile.renameTo(annotateFile);
            Toasty.success(getApplication(), annotateFileName + " has been saved in successfully !", Toast.LENGTH_LONG, true).show();
        }
    }
}
