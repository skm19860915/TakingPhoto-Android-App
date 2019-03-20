package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;

public class IOActivity extends AppCompatActivity {
    private String vehicleId;
    private String rentalId;
    private String filePath;
    private TextView licenseTextView;
    private TextView outsideTextView;
    private TextView insideTextView;
    private TextView odometerTextView;
    private TextView chronometerTextView;
    private TextView packagesTextView;
    private TextView signTextView;
    private String target;
    private TextView keyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_io);

        ImageButton homeBtn = findViewById(R.id.largehomeButton);
        licenseTextView = findViewById(R.id.licenseTextView);
        outsideTextView = findViewById(R.id.outsideTextView);
        insideTextView = findViewById(R.id.insideTextView);
        odometerTextView = findViewById(R.id.odometerTextView);
        chronometerTextView = findViewById(R.id.chronometerTextView);
        packagesTextView = findViewById(R.id.packagesTextView);
        signTextView = findViewById(R.id.signTextView);
        keyTextView = findViewById(R.id.keyTextView);

        //outsideTextView.setEnabled(false);
        //chronometerTextView.setEnabled(false);

        Intent intent = getIntent();
        vehicleId = intent.getStringExtra("vehicleId");
        rentalId = intent.getStringExtra("rentalId");
        target = intent.getStringExtra("target");
        keyTextView.setText(target.toUpperCase());

        GetCountOfPhotos(vehicleId, rentalId, target);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        licenseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "LicensePlate");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        outsideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "OutSide");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        insideTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "InSide");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        packagesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "Package");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        odometerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IndicatorActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "Odometer");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        chronometerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IndicatorActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("param", "Chronometer");
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });

        signTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReadMeActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });
    }

    private void GetCountOfPhotos(String vehicleId, String rentalId, String target) {
        licenseTextView.setText("License Plate Photo");
        outsideTextView.setText("Outside Photos");
        insideTextView.setText("Inside Photos");
        odometerTextView.setText("Odometer Photo");
        chronometerTextView.setText("Chronometer Photo");
        packagesTextView.setText("Packages Photos");
        signTextView.setText("Sign for Vehicle");

        String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        //String rootDir = "/storage/sdcard1/DCIM/";
        //String rootDir = "/mnt/sdcard/DCIM/";
        filePath = rootDir + "/OutInPhotos/" + vehicleId + "/" + rentalId + "/";

        File dir = new File(filePath);
        if (dir.isDirectory()){
            File[] files = dir.listFiles();
            if (files == null)
                return;
            int countOfLicensePhotos = 0;
            int countOfOutSidePhotos = 0;
            int countOfInSidePhotos = 0;
            int countOfPackages = 0;
            if (files.length > 0)
            {
                for (int i = 0; i < files.length; i++)
                {
                    String fileName = files[i].getName();
                    if (fileName.contains(target + "_LicensePlate")){
                        countOfLicensePhotos++;
                    }
                    else if (fileName.contains(target + "_OutSide")){
                        countOfOutSidePhotos++;
                    }
                    else if (fileName.contains(target + "_InSide")){
                        countOfInSidePhotos++;
                    }
                    else if (fileName.contains(target + "_Odometer")){
                        odometerTextView.setText("Odometer Photo (1)");
                    }
                    else if (fileName.contains(target + "_Chronometer")){
                        chronometerTextView.setText("Chronometer Photo (1)");
                    }
                    else if (fileName.contains(target + "_Package")){
                        countOfPackages++;
                    }
                    else if (fileName.contains(target + "_Signature")){
                        signTextView.setText("Sign for Vehicle (1)");
                    }
                }

                if (countOfLicensePhotos > 0){
                    String count = String.valueOf(countOfLicensePhotos);
                    licenseTextView.setText("License Plate Photos (" + count + ")");
                }

                if (countOfOutSidePhotos > 0){
                    String count = String.valueOf(countOfOutSidePhotos);
                    outsideTextView.setText("Outside Photos (" + count + ")");
                }

                if (countOfInSidePhotos > 0){
                    String count = String.valueOf(countOfInSidePhotos);
                    insideTextView.setText("Inside Photos (" + count + ")");
                }

                if (countOfPackages > 0){
                    String count = String.valueOf(countOfPackages);
                    packagesTextView.setText("Packages Photos (" + count + ")");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
