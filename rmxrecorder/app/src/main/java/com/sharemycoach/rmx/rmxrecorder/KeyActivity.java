package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class KeyActivity extends AppCompatActivity {
    private EditText vehicleIdEditText;
    private EditText rentalIdEditText;
    private ImageButton outBtn;
    private ImageButton inBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        ImageButton homeBtn = findViewById(R.id.largehomeButton);
        outBtn = findViewById(R.id.largeoutButton);
        inBtn = findViewById(R.id.largeinButton);
        vehicleIdEditText = findViewById(R.id.vehicleIdEditText);
        rentalIdEditText = findViewById(R.id.rentalIdEditText);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicleText = vehicleIdEditText.getText().toString();
                String rentalText = rentalIdEditText.getText().toString();
                if (vehicleText.isEmpty()){
                    Toasty.error(getApplication(), "Please input vehicle Id!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (rentalText.isEmpty()){
                    Toasty.error(getApplication(), "Please input rental Id!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                String vehicleId = vehicleIdEditText.getText().toString();
                String rentalId = rentalIdEditText.getText().toString();
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("rentalId", rentalId);
                intent.putExtra("target", "Out");
                startActivity(intent);
                finish();
            }
        });

        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehicleText = vehicleIdEditText.getText().toString();
                String rentalText = rentalIdEditText.getText().toString();
                if (vehicleText.isEmpty()){
                    Toasty.error(getApplication(), "Please input vehicle Id!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (rentalText.isEmpty()){
                    Toasty.error(getApplication(), "Please input rental Id!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                String vehicleId = vehicleIdEditText.getText().toString();
                String rentalId = rentalIdEditText.getText().toString();
                intent.putExtra("vehicleId", vehicleId);
                intent.putExtra("rentalId", rentalId);
                intent.putExtra("target", "In");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
