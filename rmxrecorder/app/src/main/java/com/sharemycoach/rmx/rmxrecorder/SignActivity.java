package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class SignActivity extends AppCompatActivity {
    private String filePath;
    private EditText signEditText;
    private ImageButton checkBtn;
    private String target;
    private SignDrawingView signDrawingView;
    private LinearLayout signDrawLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        signEditText = findViewById(R.id.signEditText);
        checkBtn = findViewById(R.id.checkButton);
        signDrawingView = new SignDrawingView(this);
        signDrawLayout = findViewById(R.id.signDrawLinearLayout);
        signDrawLayout.addView(signDrawingView);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        target = intent.getStringExtra("target");

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getBitmap(signDrawLayout);
                boolean success = SaveImage(bitmap);
                if (success)
                    goBack();
            }
        });
    }

    private void goBack() {
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

    private Bitmap getBitmap(LinearLayout layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private boolean SaveImage(Bitmap bitmap) {
        boolean allowed = createDirectory();
        if (allowed){
            String text = signEditText.getText().toString();
            if (text.isEmpty()){
                Toasty.error(getApplication(), "Please input your sign!", Toast.LENGTH_LONG, true).show();
                return false;
            }
            String name = target + "_Signature_" + text + ".jpg";
            FileOutputStream outputStream;
            File file = new File(filePath, name);
            try {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Toasty.success(this, name + " has been saved in successfully !", Toast.LENGTH_LONG, true).show();
                return true;
            } catch (IOException e) {
                Toasty.error(this, "Can't generate Photo File !!!", Toast.LENGTH_LONG, true).show();
                return false;
            }
        }
        return false;
    }

    private boolean createDirectory() {
        boolean isCreated;
        File dir = new File(filePath);
        if (!dir.exists())
        {
            isCreated = dir.mkdirs();
            if (!isCreated){
                Toasty.error(getApplicationContext(), "Can't Create Directory", Toast.LENGTH_LONG, true).show();
                return false;
            }
        }
        return true;
    }
}
