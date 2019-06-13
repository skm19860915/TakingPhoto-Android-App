package com.sharemycoach.rmx.rmxrecorder;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.renderscript.Script;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {
    private EditText locationText;
    private EditText nasServerText;
    private EditText nasUserNameText;
    private EditText nasPasswordText;
    private EditText azureServerText;
    private EditText azureUserNameText;
    private EditText azurePasswordText;
    private Button nasBtn;
    private Button nasSaveBtn;
    private Button azureBtn;
    private Button azureSaveBtn;
    private ProgressBar nasProgressBar;
    private ProgressBar azureProgressBar;
    private String path;
    private RequestQueue queue;
    private String location;
    private String azureServer;
    private String azureUserName;
    private String azurePassword;
    private String nasSever;
    private String nasUserName;
    private String nasPassword;
    private Sardine sardine;

    private boolean successOfWebDAVConnection;
    private boolean successOfRMXWebAPIConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nasBtn = findViewById(R.id.nasButton);
        nasSaveBtn = findViewById(R.id.nasSavebutton);
        azureBtn = findViewById(R.id.azureButton);
        azureSaveBtn = findViewById(R.id.azureSaveButton);
        nasProgressBar = findViewById(R.id.nasProgressBar);
        azureProgressBar = findViewById(R.id.azureProgressBar);

        locationText = findViewById(R.id.locationEditText);
        nasServerText = findViewById(R.id.serverEditText1);
        nasUserNameText = findViewById(R.id.nameEditText1);
        nasPasswordText = findViewById(R.id.passwordEditText1);
        azureServerText = findViewById(R.id.serverEditText2);
        azureUserNameText = findViewById(R.id.nameEditText2);
        azurePasswordText = findViewById(R.id.passwordEditText2);

        nasProgressBar.setVisibility(View.GONE);
        azureProgressBar.setVisibility(View.GONE);
        path = getFilesDir() + "/Settings/";

        loadSettingsInformation(path);

        nasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString();
                if (location.isEmpty()){
                    Toasty.error(getApplication(), "Please input Location Id !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasSever = nasServerText.getText().toString();
                if (nasSever.isEmpty()){
                    Toasty.error(getApplication(), "Please input NAS Server URL !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasUserName = nasUserNameText.getText().toString();
                if (nasUserName.isEmpty()){
                    Toasty.error(getApplication(), "Please input UserName !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasPassword = nasPasswordText.getText().toString();
                if (nasPassword.isEmpty()){
                    Toasty.error(getApplication(), "Please input Password !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasProgressBar.setVisibility(View.VISIBLE);
                connectWebDAVServer();
            }
        });

        nasSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString();
                if (location.isEmpty()){
                    Toasty.error(getApplication(), "Location Id is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasSever = nasServerText.getText().toString();
                if (nasSever.isEmpty()){
                    Toasty.error(getApplication(), "NAS Server Address is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasUserName = nasUserNameText.getText().toString();
                if (nasUserName.isEmpty()){
                    Toasty.error(getApplication(), "UserName is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                nasPassword = nasPasswordText.getText().toString();
                if (nasPassword.isEmpty()){
                    Toasty.error(getApplication(), "Password is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (!successOfWebDAVConnection){
                    Toasty.error(getApplication(), "Config information is incorrect ! Please try to login again !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                saveWebDAVServerConfiguration();
            }
        });

        azureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString();
                if (location.isEmpty()){
                    Toasty.error(getApplication(), "Please input Location Id !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azureServer = azureServerText.getText().toString();
                if (azureServer.isEmpty()){
                    Toasty.error(getApplication(), "Please input Azure Server URL !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azureUserName = azureUserNameText.getText().toString();
                if (azureUserName.isEmpty()){
                    Toasty.error(getApplication(), "Please input UserName !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azurePassword = azurePasswordText.getText().toString();
                if (azurePassword.isEmpty()){
                    Toasty.error(getApplication(), "Please input Password !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azureProgressBar.setVisibility(View.VISIBLE);
                connectWebService();
            }
        });

        azureSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString();
                if (location.isEmpty()){
                    Toasty.error(getApplication(), "Location Id is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azureServer = azureServerText.getText().toString();
                if (azureServer.isEmpty()){
                    Toasty.error(getApplication(), "Azure Server Address is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azureUserName = azureUserNameText.getText().toString();
                if (azureUserName.isEmpty()){
                    Toasty.error(getApplication(), "UserName is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                azurePassword = azurePasswordText.getText().toString();
                if (azurePassword.isEmpty()){
                    Toasty.error(getApplication(), "Password is empty !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (!successOfRMXWebAPIConnection){
                    Toasty.error(getApplication(), "Config information is incorrect ! Please try to login again !", Toast.LENGTH_LONG, true).show();
                    return;
                }
                saveWebServiceConfiguration();
            }
        });
    }

    private void saveWebDAVServerConfiguration() {
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(path + "nas.txt");
        if (file.exists() && file.isFile())
            file.delete();

        String info = location + " " + nasSever + " " + nasUserName + " " + nasPassword;
        boolean successOfSaving = saveSettingsFile(file, info);
        if (successOfSaving)
            Toasty.success(getApplicationContext(), "WebDAV Configuration successfully saved!", Toast.LENGTH_LONG, true).show();
    }

    private void connectWebDAVServer() {
        sardine = new OkHttpSardine();
        sardine.setCredentials(nasUserName, nasPassword);
        new AsyncConnectivity().execute();
    }

    private class AsyncConnectivity extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                List<DavResource> resources = sardine.list("http://" + nasSever);
                if (resources != null && resources.size()> 0)
                    successOfWebDAVConnection = true;
            }
            catch (IOException e){
                successOfWebDAVConnection = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (successOfWebDAVConnection)
                Toasty.success(getApplication(), "Successfully Connected to WebDAV Server !", Toast.LENGTH_LONG, true).show();
            else
                Toasty.error(getApplication(), "Failed to Connect to WebDAV Server !", Toast.LENGTH_LONG, true).show();
            nasProgressBar.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }

    private void connectWebService() {
        queue = Volley.newRequestQueue(this);
        String url = "http://" + azureServer + "/RMXRecorder/Auth?user=" + azureUserName + "&pass=" + azurePassword;
        if (!azureServer.equals("rmxwebapi.azurewebsites.net")){
            Toasty.error(getApplication(), "Server Address is incorrect! Please Try Again !", Toast.LENGTH_LONG, true).show();
            azureProgressBar.setVisibility(View.GONE);
            return;
        }
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String result = response;

                if (result.contains("allow_rmx")){
                    successOfRMXWebAPIConnection = true;
                    Toasty.success(getApplicationContext(), "Successfully Connected to Azure WebService !", Toast.LENGTH_LONG, true).show();
                }
                else{
                    successOfRMXWebAPIConnection = false;
                    Toasty.error(getApplicationContext(), "Fail to Connect to Azure WebService !", Toast.LENGTH_LONG, true).show();
                }

                azureProgressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                successOfRMXWebAPIConnection = false;
                azureProgressBar.setVisibility(View.GONE);
                Toasty.error(getApplication(), "Fail to Authentication to the WebService !", Toast.LENGTH_LONG, true).show();
            }
        });
        queue.add(request);
    }

    private void saveWebServiceConfiguration(){
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(path + "/azure.txt");
        if (file.exists() && file.isFile())
            file.delete();

        String info = location + " " + azureServer + " " + azureUserName + " " + azurePassword;
        boolean successOfSaving = saveSettingsFile(file, info);
        if (successOfSaving)
            Toasty.success(getApplicationContext(), "Successfully saved WebService Configuration !", Toast.LENGTH_LONG, true).show();
    }

    private void loadSettingsInformation(String path) {
        String nasInfo = getSettingsInformation(path, "nas.txt");
        if (nasInfo != null){
            String[] values = nasInfo.split(" ");
            if (values.length > 3){
                locationText.setText(values[0]);
                nasServerText.setText(values[1]);
                nasUserNameText.setText(values[2]);
                nasPasswordText.setText(values[3]);
            }
        }
        String azureInfo = getSettingsInformation(path, "azure.txt");
        if (azureInfo != null){
            String[] values = azureInfo.split(" ");
            if (values.length > 3){
                locationText.setText(values[0]);
                azureServerText.setText(values[1]);
                azureUserNameText.setText(values[2]);
                azurePasswordText.setText(values[3]);
            }
        }
    }

    private String getSettingsInformation(String path, String fileName) {
        File file = new File(path + "/" + fileName);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            try {
                String info = convertStreamToString(inputStream);
                inputStream.close();
                return info;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertStreamToString(FileInputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null){
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private boolean saveSettingsFile(File file, String info) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Toasty.error(getApplication(), "File Not found!", Toast.LENGTH_LONG, true).show();
            e.printStackTrace();
            return false;
        }
        try
        {
            outputStream.write(info.getBytes());
            return true;
        }
        catch (IOException e)
        {
            try
            {
                outputStream.close();
            } catch (IOException e1)
            {
                Toasty.error(getApplication(), "Close Error !", Toast.LENGTH_LONG, true).show();
                e1.printStackTrace();
                return false;
            }

            Toasty.error(getApplication(), "Write Error !", Toast.LENGTH_LONG, true).show();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
