package com.sharemycoach.rmx.rmxrecorder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sharemycoach.rmx.rmxrecorder.Model.RentalVehicleModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity {
    private RequestQueue queue;
    private ImageButton scanBtn;
    private ImageButton keyBtn;
    private ImageButton uploadBtn;
    private ImageButton settingsBtn;
    private ImageButton outBtn;
    private ImageButton inBtn;
    private ListView vehicleListView;
    ArrayList<RentalVehicleModel> arrayList = new ArrayList<>();
    private RentalVehicleModel selectedModel;
    private String authFilePath;
    public static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scanBtn = findViewById(R.id.scanButton);
        keyBtn = findViewById(R.id.keyButton);
        uploadBtn = findViewById(R.id.uploadButton);
        settingsBtn = findViewById(R.id.settingsButton);
        outBtn = findViewById(R.id.outButton);
        inBtn = findViewById(R.id.inButton);
        vehicleListView = findViewById(R.id.vehicleListView);
        vehicleListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        vehicleListView.setSelector(android.R.color.darker_gray);
        outBtn.setEnabled(false);
        inBtn.setEnabled(false);

        checkStoragePermission();
        checkAzureWebServiceAuthentication();
        //loadRentalVehicleListFromWebApi();

        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                intent.putExtra("vehicleId", selectedModel.getQuickFindKeyWord());
                intent.putExtra("rentalId", selectedModel.getReferenceEstimateSequenceId());
                intent.putExtra("target", "Out");
                startActivity(intent);
            }
        });

        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IOActivity.class);
                intent.putExtra("vehicleId", selectedModel.getQuickFindKeyWord());
                intent.putExtra("rentalId", selectedModel.getReferenceEstimateSequenceId());
                intent.putExtra("target", "In");
                startActivity(intent);
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(intent);
            }
        });

        keyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KeyActivity.class);
                startActivity(intent);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, HomeActivity.REQUEST_CODE_PERMISSION);
            return;
        }
    }

    private void checkAzureWebServiceAuthentication() {
        authFilePath = getFilesDir() + "/Settings/";
        File dir = new File(authFilePath);
        if (dir.isDirectory() && dir.exists()){
            File file = new File(authFilePath, "azure.txt");
            if (file.isFile() && file.exists()){
                loadRentalVehicleListFromWebApi();
            }
            else
                Toasty.error(getApplication(), "Not Found Authentication File!", Toast.LENGTH_LONG, true).show();
        }
        else
            Toasty.error(getApplication(), "Not Found Authentication File!", Toast.LENGTH_LONG, true).show();
    }

    private void loadRentalVehicleListFromWebApi() {
        queue = Volley.newRequestQueue(this);
        String url = "http://rmxwebapi.azurewebsites.net/RMXRecorder/RentalVehicle/1";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject record;
                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < response.length(); i++)
                {
                    try {
                        record = response.getJSONObject(i);
                        String referenceEstimateSequenceId = record.getString("ReferenceEstimateSequenceId");
                        String quickFindKeyWord = record.getString("QuickFindKeyWord");
                        String locationSequenceId = record.getString("LocationSequenceId");
                        String licensePlate = record.getString("LicensePlate");
                        RentalVehicleModel model = new RentalVehicleModel();
                        model.setReferenceEstimateSequenceId(referenceEstimateSequenceId);
                        model.setQuickFindKeyWord(quickFindKeyWord);
                        model.setLocationSequenceId(locationSequenceId);
                        model.setLicensePlate(licensePlate);
                        String pattern = referenceEstimateSequenceId + " - " + quickFindKeyWord + "    " +  licensePlate;
                        list.add(pattern);
                        arrayList.add(model);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ListAdapter adapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, list);

                vehicleListView.setAdapter(adapter);
                vehicleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RentalVehicleModel model = arrayList.get(position);
                        outBtn.setEnabled(true);
                        inBtn.setEnabled(true);
                        selectedModel = model;
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Do you want to exit this application ?");
        builder.setTitle("Note")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDestroy();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Alert Dialog");
        alertDialog.show();
    }
}
