package com.github.lizlop.testpalette;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SparseIntArray pixelsCounter = new SparseIntArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palette_view);
        ImageView imageView = findViewById(R.id.picture);
        Bitmap resource = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        int width = resource.getWidth();
        int height = resource.getHeight();
        int[] pixels = new int[width*height];
        resource.getPixels(pixels,0, width, 0, 0, width, height);
        setPixelsCounter(pixels);
        findViewById(R.id.color1).setBackgroundColor(getCommonColor(1));
        findViewById(R.id.color2).setBackgroundColor(getCommonColor(2));
        findViewById(R.id.color3).setBackgroundColor(getCommonColor(3));
        findViewById(R.id.color4).setBackgroundColor(getCommonColor(4));
        findViewById(R.id.color5).setBackgroundColor(getCommonColor(5));
    }

    void setPixelsCounter(int[] pixels){
        for (int i=0; i<pixels.length; i++){
            pixelsCounter.put(pixels[i],pixelsCounter.get(pixels[i],0)+1);
        }
    }

    int getCommonColor(int index) {
        int color = pixelsCounter.keyAt(pixelsCounter.size()-index);
        return color;
    }
}
