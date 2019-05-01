package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;

public class PhotoListActivity extends AppCompatActivity {
    private ListView photoListView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        photoListView = findViewById(R.id.photoListView);

        Intent intent = getIntent();
        String parentNode = intent.getStringExtra("parentNode");
        String childNode = intent.getStringExtra("childNode");

        String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        //String rootDir = "/storage/sdcard1/DCIM/";
        //String rootDir = "/mnt/sdcard/DCIM/";
        path = rootDir + "/OutInPhotos/" + parentNode + "/" + childNode;
        File dir = new File(path);
        final File[] files = dir.listFiles();

        PhotoListAdapter adapter = new PhotoListAdapter(this, files.length, files);
        photoListView.setAdapter(adapter);
        photoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PhotoDetailActivity.class);
                String targetFile = files[position].getName();
                intent.putExtra("path", path);
                intent.putExtra("targetFile", targetFile);
                startActivity(intent);
            }
        });
    }
}
