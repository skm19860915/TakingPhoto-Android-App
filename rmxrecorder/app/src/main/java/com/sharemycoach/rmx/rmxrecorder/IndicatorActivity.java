package com.sharemycoach.rmx.rmxrecorder;

import android.Manifest;
import android.content.ContentResolver;
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
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextPaint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class IndicatorActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
    private String longitude;
    private String latitude;
    private ImageButton backBtn;
    private String filePath;
    private String fileName;
    private ImageView odometerImageView;
    private EditText odometerEditText;
    private Bitmap bitmap;
    private String param;
    private String target;
    private TextView keyTextView;
    private TextView labelTextView;
    private Uri imageUri;
    private boolean isCaptured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getLocationInformation();

        backBtn = findViewById(R.id.backButton);
        odometerImageView = findViewById(R.id.odometerImageView);
        odometerEditText = findViewById(R.id.odometerEditText);
        keyTextView = findViewById(R.id.keyTextView);
        labelTextView = findViewById(R.id.labelTextView);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        param = intent.getStringExtra("param");
        target = intent.getStringExtra("target");
        keyTextView.setText(param);
        labelTextView.setText("Enter " + param + ":");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean allowed = checkPermissionAndCreateDirectory();
        if (allowed){
            File tempPhoto;
            fileName = target + "_" + param + ".jpg";
            tempPhoto = new File(filePath, fileName);
            tempPhoto.delete();

            File dir = new File(filePath);
            File[] files = dir.listFiles();
            if (files != null && files.length > 0){
                for (File file : files){
                    String selectedFileName = FilenameUtils.removeExtension(file.getName());
                    String targetFileName = target + "_" + param;
                    if (selectedFileName.contains(targetFileName)){
                        file.delete();
                    }
                }
            }

            imageUri = FileProvider.getUriForFile(IndicatorActivity.this, BuildConfig.APPLICATION_ID + ".provider", tempPhoto);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, HomeActivity.REQUEST_CODE_PERMISSION);
        }

        odometerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (isCaptured){
                        String odometerText = odometerEditText.getText().toString();
                        if (odometerText.isEmpty()){
                            Toasty.error(getApplicationContext(), "Please input value!", Toast.LENGTH_LONG, true).show();
                            return false;
                        }
                        else{
                            SaveCaptureImage(param, odometerText);
                        }
                    }

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
                return false;
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean checkPermissionAndCreateDirectory() {
        if (ContextCompat.checkSelfPermission(IndicatorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(IndicatorActivity.this, new String[]{Manifest.permission.CAMERA}, HomeActivity.REQUEST_CODE_PERMISSION);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HomeActivity.REQUEST_CODE_PERMISSION && resultCode == RESULT_OK){
            this.getContentResolver().notifyChange(imageUri, null);
            ContentResolver resolver = this.getContentResolver();
            Bitmap lawBitmap = null;
            try {
                lawBitmap = MediaStore.Images.Media.getBitmap(resolver, imageUri);
            } catch (IOException e) {
                Toasty.error(this, "Bitmap create error!", Toast.LENGTH_LONG, true).show();
            }
            bitmap = customBitmapWithInformation(lawBitmap);
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            odometerImageView.setImageBitmap(scaledBitmap);
            isCaptured = true;
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        String pattern = "MM/dd/yyyy h:mm";
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        str = format.format(currentTime);
        return str;
    }

    private void SaveCaptureImage(String param, String text) {
        File originalFile = new File(filePath, fileName);
        String name = target + "_" + param + "_" + text + ".jpg";
        File updateFile = new File(filePath, name);
        boolean success = originalFile.renameTo(updateFile);
        if (success){
            Toasty.success(getApplication(), name + " has been saved in successfully !", Toast.LENGTH_LONG, true).show();
        }
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

    @Override
    public void onBackPressed() {
        if (isCaptured){
            String odometerText = odometerEditText.getText().toString();
            if (odometerText.isEmpty()){
                Toasty.error(getApplicationContext(), "Please input value!", Toast.LENGTH_LONG, true).show();
                return;
            }
            else{
                SaveCaptureImage(param, odometerText);
            }
        }

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
}
