package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ReadMeActivity extends AppCompatActivity {
    private ImageButton acceptBtn;
    private String filePath;
    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);

        acceptBtn = findViewById(R.id.acceptButton);

        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        target = intent.getStringExtra("target");

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                intent.putExtra("filePath", filePath);
                intent.putExtra("target", target);
                startActivity(intent);
            }
        });
    }
}
