package com.github.lizlop.testpalette;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import static com.github.lizlop.testpalette.ColorHelper.*;

public class MainActivity extends AppCompatActivity {
    SparseIntArray pixelsCounter = new SparseIntArray();
    Map<Integer, Integer> rgbDif = new HashMap();
    int[] colors = new int[5];

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
        double[] whitePoint = new double[]{0.9505,1.0000,1.0890};
        for (int i=0; i<pixels.length; i++){
            double[] pixel = getLab(getXYZ(getLinearRGB(pixels[i])));
            int dif = (int)Math.floor(getDifference(whitePoint,pixel));
            rgbDif.put(dif, pixels[i]);
            pixelsCounter.put(dif,pixelsCounter.get(dif,0)+1);
        }
        return;
    }

    int getCommonColor(int index) {
        Integer color = 0;
        boolean isNotUnique = true;
        int currentPosition = index-1;
        while(isNotUnique){
            isNotUnique = false;
            currentPosition++;
            color = pixelsCounter.keyAt(pixelsCounter.size()-currentPosition);
            for (int i=index-1; i>0; i--) {
                if (Math.abs(color-colors[i])<=3)
                {isNotUnique=true; break;}
            }
        }
        colors[index-1] = rgbDif.get(color);
        return colors[index-1];
    }
}
