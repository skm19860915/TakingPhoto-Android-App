package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class OtherActivity extends AppCompatActivity {
    private String filePath;
    private String fileName;
    private String param;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        final EditText otherEditText = findViewById(R.id.otherEditText);
        ImageButton checkBtn = findViewById(R.id.checkImageButton);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        fileName = intent.getStringExtra("fileName");
        param = intent.getStringExtra("param");
        target = intent.getStringExtra("target");

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = otherEditText.getText().toString();
                if (string.isEmpty()){
                    Toasty.error(getApplication(), "Please input a value!", Toast.LENGTH_LONG, true).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), AnnotateActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("fileName", fileName);
                intent.putExtra("param", param);
                intent.putExtra("target", target);
                intent.putExtra("otherMsg", string);
                startActivity(intent);
                finish();
            }
        });
    }
}
