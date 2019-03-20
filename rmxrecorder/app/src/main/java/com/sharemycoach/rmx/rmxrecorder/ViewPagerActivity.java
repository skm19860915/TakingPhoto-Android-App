package com.sharemycoach.rmx.rmxrecorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class ViewPagerActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private String longitude;
    private String latitude;
    private ImageButton backBtn;
    private ViewPager photoViewPager;
    private ImageButton cameraBtn;
    private ImageButton pencilBtn;
    private ImageButton characterBtn;
    private ViewPagerAdapter adapter;
    private TextView keyTextView;
    private String filePath;
    private String fileName;
    private String param;
    private String target;

    private String draw;
    private String startTime;
    private String endTime;
    private String targetTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getLocationInformation();

        backBtn = findViewById(R.id.backButton);
        photoViewPager = findViewById(R.id.photoViewPager);
        cameraBtn = findViewById(R.id.cameraButton);
        pencilBtn = findViewById(R.id.pencilButton);
        characterBtn = findViewById(R.id.characterButton);
        keyTextView = findViewById(R.id.keyTextView);
        pencilBtn.setEnabled(false);
        characterBtn.setEnabled(false);

        final Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        param = intent.getStringExtra("param");
        target = intent.getStringExtra("target");
        draw = intent.getStringExtra("draw");
        if (draw != null)
            characterBtn.setEnabled(true);

        ShowTitle(param);
        File dir = new File(filePath);
        if (dir.exists())
        {
            File[] files = dir.listFiles();
            if (files.length > 0)
            {
                File[] filterFiles = getFilterFiles(dir);
                if (filterFiles.length > 0)
                {
                    adapter = new ViewPagerAdapter(ViewPagerActivity.this, filterFiles.length, filterFiles);
                    photoViewPager.setAdapter(adapter);
                }
            }
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings  = filePath.split("/");
                String vehicleId = strings[strings.length - 2];
                String rentalId = strings[strings.length - 1];
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("rentalId", rentalId);
                intent.putExtra("target", target);
                startActivity(intent);
                finish();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                boolean allowed = checkPermissionAndCreateDirectory();
                if (allowed){
                    startTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    startActivityForResult(cameraIntent, HomeActivity.REQUEST_CODE_PERMISSION);
                }
            }
        });

        pencilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = adapter.getTargetFile();
                fileName = file.getName();
                Intent drawIntent = new Intent(getApplicationContext(), DrawActivity.class);
                drawIntent.putExtra("filePath", filePath);
                drawIntent.putExtra("fileName", fileName);
                drawIntent.putExtra("param", param);
                drawIntent.putExtra("target", target);
                startActivity(drawIntent);
                finish();
            }
        });

        characterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = adapter.getTargetFile();
                fileName = file.getName();
                Intent annotateIntent = new Intent(getApplicationContext(), AnnotateActivity.class);
                annotateIntent.putExtra("filePath", filePath);
                annotateIntent.putExtra("fileName", fileName);
                annotateIntent.putExtra("param", param);
                annotateIntent.putExtra("target", target);
                startActivity(annotateIntent);
                finish();
            }
        });
    }

    private boolean checkPermissionAndCreateDirectory() {
        if (ContextCompat.checkSelfPermission(ViewPagerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ViewPagerActivity.this, new String[]{Manifest.permission.CAMERA}, HomeActivity.REQUEST_CODE_PERMISSION);
            return false;
        }
        boolean isCreated;
        File dir = new File(filePath);
        if (!dir.exists()){
            isCreated = dir.mkdirs();
            if (!isCreated){
                Toasty.error(getApplicationContext(), "Can't Create Directory", Toast.LENGTH_LONG, true).show();
                return false;
            }
        }
        return true;
    }

    private void ShowTitle(String param) {
        String index = null;
        switch (param){
            case "LicensePlate":
                index = "License Plate Photo";
                break;
            case "OutSide":
                index = "OutSide Photos";
                break;
            case "InSide":
                index = "InSide Photos";
                break;
            case "Package":
                index = "Packages Photos";
                break;
        }
        keyTextView.setText(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        endTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String originalPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/";
        //String originalPath = "/storage/sdcard1/DCIM/Camera/";
        //String originalPath = "/mnt/sdcard/DCIM/Camera/";
        File originalDir = new File(originalPath);
        File[] files = originalDir.listFiles();
        if (files.length > 0)
        {
            for (int i = 0; i < files.length; i++)
            {
                if (files[i].isFile()){
                    String availableFileName =  getAvailableFileName(startTime, endTime, files[i].getName());
                    if (availableFileName != null){
                        String sourcePath = originalPath + files[i].getName();
                        File source = new File(sourcePath);
                        String destinationPath = filePath + availableFileName;
                        File destination = new File(destinationPath);
                        boolean success = source.renameTo(destination);
                        if (success)
                            generateCustomImageFile(destination);
                    }
                }
            }
        }
        else
            Toasty.error(getApplicationContext(), "No having Files or Directory", Toast.LENGTH_LONG, true).show();

        File targetDir = new File(filePath);
        File[] filterFiles = getFilterFiles(targetDir);
        if (filterFiles.length > 0){
            adapter = new ViewPagerAdapter(ViewPagerActivity.this, filterFiles.length, filterFiles);
            photoViewPager.setAdapter(adapter);
        }
        pencilBtn.setEnabled(true);
    }

    private void generateCustomImageFile(File file) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            Bitmap customBitmap = customBitmapWithInformation(bitmap);
            saveBitmap(customBitmap, file.getName());
        } catch (FileNotFoundException e) {
            Toasty.error(this, "Bitmap create error !!!", Toast.LENGTH_LONG, true).show();
        }
    }

    private void saveBitmap(Bitmap bitmap, String fileName) {
        FileOutputStream outputStream;
        File file = new File(filePath, fileName);
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Toasty.error(this, "Can't generate Photo File !!!", Toast.LENGTH_LONG, true).show();
        }
    }

    private String getAvailableFileName(String startTime, String endTime, String name) {
        if (name.length() < 5){
            Toasty.error(this, "Found Bad File !!!", Toast.LENGTH_SHORT, true).show();
            return null;
        }
        String fileName = name.substring(0, name.length() - 4); //  remove .jpg from full file name
        String tokens[] = fileName.split("_");
        if (tokens != null)
        {
            targetTime = null;
            if (tokens.length == 3) // common camera raw image format : IMG_20180705_074237.jpg
                targetTime = tokens[1] + "_" + tokens[2];
            else if (tokens.length == 2) // samsung camera raw image format : 20180705_074237.jpg
                targetTime = tokens[0] + "_" + tokens[1];

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            try {
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                if (targetTime == null){
                    Toasty.error(this, "Found UnKnown File !!!", Toast.LENGTH_SHORT, true).show();
                    return null;
                }
                Date targetDate = sdf.parse(targetTime);
                if (startDate.compareTo(targetDate) * targetDate.compareTo(endDate) > 0)
                    return target + "_" + param + "_" + targetTime + ".jpg";
            } catch (ParseException e) {
                Toasty.error(this, "Can't Recreate File !!!", Toast.LENGTH_SHORT, true).show();
                return null;
            }
        }
        else
            Toasty.error(this, "Not Found Target File !!!", Toast.LENGTH_SHORT, true).show();
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HomeActivity.REQUEST_CODE_PERMISSION){
            int grantResultLength = grantResults.length;
            if (grantResultLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toasty.success(getApplicationContext(), "Allowed Permission !!", Toast.LENGTH_LONG, true).show();
            }
            else{
                Toasty.error(getApplicationContext(), "Denied Permission!!", Toast.LENGTH_LONG, true).show();
            }
        }
    }

    private Bitmap customBitmapWithInformation(Bitmap bitmap) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        Paint textPaint = new TextPaint();
        if (bitmap.getWidth() > bitmap.getHeight()){
            textPaint.setTextSize(bitmap.getHeight() / 25);
            textPaint.setStrokeWidth(bitmap.getHeight() / 80);
        }
        else{
            textPaint.setTextSize(bitmap.getWidth() / 25);
            textPaint.setStrokeWidth(bitmap.getWidth() / 80);
        }

        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setColor(Color.BLACK);

        String datetime = GetDateTimeOfPhoto();
        String location = longitude + ", " + latitude;
        String information = location + "  " + datetime;
        canvas.drawText(information, bitmap.getWidth() / 2 - textPaint.measureText(information) / 2, bitmap.getHeight() - 50 , textPaint);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.WHITE);
        canvas.drawText(information, bitmap.getWidth() / 2 - textPaint.measureText(information) / 2, bitmap.getHeight() - 50 , textPaint);

        return bitmap;
    }

    private String GetDateTimeOfPhoto() {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String newFormat = "MM/dd/yyyy h:mm:ss";
        try {
            Date time = sdf.parse(targetTime);
            str = new SimpleDateFormat(newFormat).format(time);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File[] getFilterFiles(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(target + "_" + param)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (files.length > 0){
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified());
                }
            });
        }
        return files;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Toasty.error(getApplication(), "Can't get Location Information!", Toast.LENGTH_LONG, true).show();
            return;
        }
        longitude = String.valueOf(location.getLongitude()) ;
        latitude = String.valueOf(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getLocationInformation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRuntimePermission();
        }
        else{
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }
    }

    private void requestRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, HomeActivity.REQUEST_CODE_PERMISSION);
    }
}
