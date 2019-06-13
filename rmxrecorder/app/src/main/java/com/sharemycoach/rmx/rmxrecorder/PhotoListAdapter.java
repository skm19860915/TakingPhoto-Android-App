package com.sharemycoach.rmx.rmxrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoListAdapter extends ArrayAdapter<String> {
    private int count;
    private Context context;
    private File[] files;

    public PhotoListAdapter(@NonNull Context context, int count, File[] files) {
        super(context, R.layout.photo_list_item);
        this.context = context;
        this.files = files;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.photo_list_item, parent, false);
            viewHolder.targetImageView = convertView.findViewById(R.id.targetImageView);
            viewHolder.descTextView = convertView.findViewById(R.id.descTextView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        File file = files[position];
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            viewHolder.targetImageView.setImageBitmap(scaledBitmap);
            viewHolder.descTextView.setText(file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    static class ViewHolder{
        ImageView targetImageView;
        TextView descTextView;
    }
}
