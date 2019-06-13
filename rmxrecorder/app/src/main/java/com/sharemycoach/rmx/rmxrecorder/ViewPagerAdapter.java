package com.sharemycoach.rmx.rmxrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private int count;
    private File[] files;
    ViewPager viewPager;

    ViewPagerAdapter(Context context, int count, File[] files){
        this.context = context;
        this.files = files;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        File file = files[position];
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.photo_layout, null);
        ImageView imageView = view.findViewById(R.id.photoImageView);

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            int nh = (int) (bitmap.getHeight() * (512.0/bitmap.getWidth()));
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            imageView.setImageBitmap(scaledBitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    public File getTargetFile(){
        int index = viewPager.getCurrentItem();
        File file = files[index];
        return file;
    }
}

