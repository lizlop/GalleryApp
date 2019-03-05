package com.github.lizlop.galleryApplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.SparseIntArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.github.lizlop.galleryApplication.ColorHelper.*;

public class ImageViewActivity extends Activity {
    public static final String EXTRA_IMAGE = "ImageViewActivity.IMAGE";
    private ImageView mImageView;
    SparseIntArray pixelsCounter = new SparseIntArray();
    Map<LABColor, Integer> RgbLab = new HashMap();
    ArrayList<LABColor> colors = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        mImageView = (ImageView) findViewById(R.id.image);
        final MediaStoreData current = getIntent().getParcelableExtra(EXTRA_IMAGE);

        Glide.with(this)
                .asBitmap()
                .load(current.getUri())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(new ImageViewTarget<Bitmap>(mImageView) {
                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        getView().setImageBitmap(resource);
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        int[] pixels = new int[width*height];
                        resource.getPixels(pixels,0, width, 0, 0, width, height);
                        setPalette(pixels);
                    }
                });
    }

    private void setPalette(int[] pixels){
        double[] whitePoint = new double[]{0.9505,1.0000,1.0890};
        for (int i=0; i<pixels.length; i++){
            LABColor color = getLab(getXYZ(getLinearRGB(pixels[i])));
            RgbLab.put(color,pixels[i]);
            if (colors.contains(color)){colors.get(colors.indexOf(color)).increaseCount(color.getCount());}
            else colors.add(color);

            /*int dif = (int)Math.floor(getDifference(whitePoint,pixel));
            rgbDif.put(dif, pixels[i]);
            pixelsCounter.put(dif,pixelsCounter.get(dif,0)+1);*/
        }
        Collections.sort(colors,new LABColor.SortColors());

        while (colors.size()>5){
            LABColor.setDif(2*LABColor.getDif());
            int i=0;
            while (i<colors.size()-1) {
                for (int j = i + 1; j < colors.size(); j++) {
                    if (colors.get(i).equals(colors.get(j))) {
                        if (colors.get(i).getCount() >= colors.get(j).getCount()) {
                            colors.get(i).increaseCount(colors.get(j).getCount());
                            colors.remove(j);
                            j--;
                            if (colors.size() == 5) break;
                        } else {
                            colors.get(j).increaseCount(colors.get(i).getCount());
                            colors.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                i++;
                if (colors.size() == 5) break;
            }
        }

        try
        {findViewById(R.id.color1).setBackgroundColor(RgbLab.get(colors.get(0)));
        findViewById(R.id.color2).setBackgroundColor(RgbLab.get(colors.get(1)));
        findViewById(R.id.color3).setBackgroundColor(RgbLab.get(colors.get(2)));
        findViewById(R.id.color4).setBackgroundColor(RgbLab.get(colors.get(3)));
        findViewById(R.id.color5).setBackgroundColor(RgbLab.get(colors.get(4)));}
        catch (IndexOutOfBoundsException ex){}
    }

    /*int getCommonColor(int index) {
        Integer color = 0;
        boolean isNotUnique = true;
        int currentPosition = index-1;
        while(isNotUnique&&(currentPosition<rgbDif.size()-1)){
            isNotUnique = false;
            currentPosition++;
            color = pixelsCounter.keyAt(pixelsCounter.size()-currentPosition);
            for (int i=index-2; i>=0; i--) {
                if (Math.abs(color-colors[i])<3)
                {isNotUnique=true; break;}
            }
        }
        colors[index-1] = color;
        return rgbDif.get(colors[index-1]);
    }*/

}
