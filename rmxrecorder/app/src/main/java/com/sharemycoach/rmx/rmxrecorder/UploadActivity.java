package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class UploadActivity extends AppCompatActivity {
    private ArrayList<String> list = new ArrayList<>();
    private int selectedNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ImageButton homeBtn = findViewById(R.id.largehomeButton);
        ListView dataListView = findViewById(R.id.dataListView);

        String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        //String rootDir = "/storage/sdcard1/DCIM/";
        //String rootDir = "/mnt/sdcard/DCIM/";
        String mainPath = rootDir + "/OutInPhotos/";

        File mainDir = new File(mainPath);
        if (mainDir.isDirectory()){
            String[] parentNames = mainDir.list();
            for (int i = 0; i < parentNames.length; i++){
                String parentPath = mainPath + parentNames[i];
                File parentDir = new File(parentPath);
                String[] childNames = parentDir.list();
                for (int x = 0; x < childNames.length; x++){
                    Log.e("subNames", parentNames[i] + " - " + childNames[x]);
                    list.add(parentNames[i] + " - " + childNames[x]);
                }
            }
        }

        UploadListAdapter adapter = new UploadListAdapter(list, UploadActivity.this);
        dataListView.setAdapter(adapter);
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                selectedNumber = position;
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                onBackPressed();
            }
        });
    }
}
