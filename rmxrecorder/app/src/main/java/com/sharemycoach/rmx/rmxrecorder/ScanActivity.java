package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import es.dmoral.toasty.Toasty;

public class ScanActivity extends AppCompatActivity {
    private TextView dataTextView;
    private ImageButton inBtn;
    private ImageButton outBtn;
    private ImageButton homeBtn;
    private String vehicleId;
    private String rentalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        homeBtn = findViewById(R.id.largehomeButton);
        outBtn = findViewById(R.id.largeoutButton);
        inBtn = findViewById(R.id.largeinButton);
        dataTextView = findViewById(R.id.dataTextView);
        outBtn.setEnabled(false);
        outBtn.setEnabled(false);

        IntentIntegrator integrator = new IntentIntegrator(ScanActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                onBackPressed();
            }
        });

        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("rentalId", rentalId);
                intent.putExtra("target", "Out");
                startActivity(intent);
            }
        });

        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("rentalId", rentalId);
                intent.putExtra("target", "In");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Toasty.error(getApplication(), "No Result !", Toast.LENGTH_LONG, true).show();
                outBtn.setEnabled(false);
                inBtn.setEnabled(false);
            }
            else{
                String value = parseScanContent(result.getContents().toString());
                if (value == null){
                    Toasty.error(getApplication(), "The QR Code you scanned does not contain RMX Check Out/In data !", Toast.LENGTH_LONG, true).show();
                    outBtn.setEnabled(false);
                    inBtn.setEnabled(false);

                }
                else {
                    dataTextView.setText(value);
                    String[] tokens = value.split(",");
                    if (tokens.length > 0){
                        rentalId = tokens[1];
                        vehicleId = tokens[2];
                        Toasty.success(getApplication(), "Scanning Success ! ", Toast.LENGTH_LONG, true).show();
                        outBtn.setEnabled(true);
                        inBtn.setEnabled(true);
                    }
                    else{
                        outBtn.setEnabled(false);
                        inBtn.setEnabled(false);
                        Toasty.error(getApplication(), "QR Code parse error !", Toast.LENGTH_LONG, true).show();
                    }
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String parseScanContent(String result) {
        String id = result.substring(0, 4);
        if (id.equals("RMXR"))
            return result;
        else
            return null;
    }
}
