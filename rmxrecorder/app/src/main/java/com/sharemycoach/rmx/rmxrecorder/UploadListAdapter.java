package com.sharemycoach.rmx.rmxrecorder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.sharemycoach.rmx.rmxrecorder.R.drawable.sent;

public class UploadListAdapter extends BaseAdapter implements ListAdapter{
    private ArrayList<String> list;
    private Context context;
    private String path;
    private LayoutInflater inflater;
    private ProgressDialog progressDialog;
    private Sardine sardine;
    private String nasServerName;
    private String nasUserName;
    private String nasPassword;
    private File[] files;
    private String vehicleId;
    private String rentalId;
    private ViewHolder currentHolder;

    public UploadListAdapter(ArrayList<String> list, Context context){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (view == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.upload_list_item, null);
            holder = new ViewHolder();
            holder.removeBtn = view.findViewById(R.id.removeButton);
            holder.itemTextView = view.findViewById(R.id.itemTextView);
            holder.uploadBtn = view.findViewById(R.id.uploadButton);
            holder.sentBtn = view.findViewById(R.id.sentButton);
            holder.eyeBtn = view.findViewById(R.id.eyeButton);
            holder.view = view;
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.itemTextView.setText(list.get(position));

        holder.sentBtn.setTag(position);

        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete all photos for the selected vehicle ?");
                builder.setTitle("Warning")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                boolean isDeleted = deleteAllInfos(list.get(position), context);
                                if (isDeleted){
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        holder.uploadBtn.setTag(position);
        holder.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentHolder = holder;
                int pos = (int) v.getTag();
                String record = list.get(pos);
                String info = record.replaceAll("\\s", "");
                String[] node = info.split("-");
                uploadPhotos(node[0], node[1], context);
            }
        });

        holder.eyeBtn.setTag(position);
        holder.eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                String record = list.get(pos);
                String info = record.replaceAll("\\s", "");
                String[] node = info.split("-");
                Log.e("Value", node[0] + "*********" + node[1]);
                Intent intent = new Intent(context, PhotoListActivity.class);
                intent.putExtra("parentNode", node[0]);
                intent.putExtra("childNode", node[1]);
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    static class ViewHolder {
        ImageButton removeBtn, uploadBtn, eyeBtn, sentBtn;
        View view;
        TextView itemTextView;
    }

    private void uploadPhotos(String parent, String child, Context context) {
        String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        vehicleId = parent;
        rentalId = child;
        String sourcePath = rootDir + "/OutInPhotos/" + vehicleId + "/" + rentalId + "/";
        File dir = new File(sourcePath);
        if (!dir.isDirectory()){
            Toasty.error(context, "No exist such directory !", Toast.LENGTH_LONG, true).show();
            return;
        }
        files = dir.listFiles();
        if (files == null || files.length < 1){
            Toasty.error(context, "No such files in this directory !", Toast.LENGTH_LONG, true).show();
            return;
        }
        String[] list = getWebDavConfigFile(context);
        if (list == null || list.length < 3){
            Toasty.error(context, "Not found WebDAV Config Information !", Toast.LENGTH_LONG, true).show();
            return;
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait......");
        progressDialog.show();

        nasServerName = list[1];
        nasUserName = list[2];
        nasPassword = list[3];
        sardine = new OkHttpSardine();
        sardine.setCredentials(nasUserName, nasPassword);
        new AsyncUploadTask().execute();
    }

    private class AsyncUploadTask extends AsyncTask<Void, Void, Void>{
        private int status;
        private String targetUrl;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String rootUrl = "http://" + nasServerName + "/RMXRecorder/OutInPhotos/";
                if (!sardine.exists(rootUrl))
                    sardine.createDirectory(rootUrl);
                String parentUrl = rootUrl + vehicleId + "/";
                if (!sardine.exists(parentUrl))
                    sardine.createDirectory(parentUrl);
                targetUrl = parentUrl + rentalId + "/";
                if (!sardine.exists(targetUrl))
                    sardine.createDirectory(targetUrl);
            } catch (IOException e) {
                e.printStackTrace();
                status = -1;
            }
            for (int i = 0; i < files.length; i++){
                try {
                    byte[] data = FileUtils.readFileToByteArray(files[i]);
                    sardine.put(targetUrl + files[i].getName(), data);
                    status = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    status = 0;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (status == 1){
                currentHolder.sentBtn.setEnabled(false);
                notifyDataSetChanged();
                Toasty.success(context, "All files uploaded successfully !", Toast.LENGTH_LONG, true).show();
            }
            else if(status == 0){
                Toasty.error(context, "File upload failed !", Toast.LENGTH_LONG, true).show();
            }
            else{
                Toasty.error(context, "Can't create directory to server !", Toast.LENGTH_LONG, true).show();
            }

            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private String[] getWebDavConfigFile(Context context) {
        String path = context.getFilesDir() + "/Settings/";
        String nasInfo = getSettingsInformation(path, "nas.txt");
        if (nasInfo != null){
            String[] values = nasInfo.split(" ");
            return values;
        }
        return null;
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

    private boolean deleteAllInfos(String string, Context context) {
        boolean isDeleted = false;
        String info = string.replaceAll("\\s", "");
        String[] node = info.split("-");
        String parentDirName = node[0];
        String childDirName = node[1];

        String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        //String rootDir = "/storage/sdcard1/DCIM/";
        //String rootDir = "/mnt/sdcard/DCIM/";
        path = rootDir + "/OutInPhotos/" + parentDirName + "/";

        File dir = new File(path + childDirName + "/");
        boolean deletedAllFiles = false;
        if (dir.exists()){
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++){
                File file = files[i];
                deletedAllFiles = file.delete();
            }
        }
        if (deletedAllFiles){
            try {
                File mainDir = new File(path);
                File subDir = new File(path + childDirName + "/");
                FileUtils.deleteDirectory(mainDir);
                FileUtils.deleteDirectory(subDir);
                isDeleted = true;
            } catch (IOException e) {
                e.printStackTrace();
                isDeleted = false;
            }
        }
        return isDeleted;
    }
}
