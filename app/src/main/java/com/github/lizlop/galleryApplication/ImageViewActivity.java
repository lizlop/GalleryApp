package com.github.lizlop.galleryApplication;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class ImageViewActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    public static final String EXTRA_IMAGE = "ImageViewActivity.IMAGE";
    private ImageView mImageView;
    int width, height, nextSize;
    int[] pixels;
    LABColor[][] palette = new LABColor[3][];
    SeekBar seekBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        mImageView = (ImageView) findViewById(R.id.image);
        final MediaStoreData current = getIntent().getParcelableExtra(EXTRA_IMAGE);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        try {
            seekBar.setOnSeekBarChangeListener(this);
        } catch (NullPointerException e) {

        }

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
                        width = resource.getWidth();
                        height = resource.getHeight();
                        pixels = new int[width*height];
                        resource.getPixels(pixels,0, width, 0, 0, width, height);

                        setPalette();
                    }
                });
    }

    private void setPalette(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int xdpmm = (int)(metrics.xdpi/25.4);
        int ydpmm = (int)(metrics.ydpi/25.4);

        int index;
        double difference = 1.0;
        final SparseIntArray counter = new SparseIntArray();
        List<LABColor> colors = new ArrayList<>();

        Comparator<LABColor> comparator = new Comparator<LABColor>() {
            @Override
            public int compare(LABColor o1, LABColor o2) {
                int dif = counter.get(o2.getLab())-counter.get(o1.getLab());
                if (dif == 0) {
                    if (o2.getA()-o1.getA() == 0) {
                        if (o2.getB()-o1.getB() == 0) return 0;
                        return (o2.getB()-o1.getB())<0? -1 : 1;
                    }
                    return (o2.getA()-o1.getA())<0? -1 : 1;
                }
                return dif;
            }
        };

        for (int l=ydpmm/2; l<height ;l=l+ydpmm){
            for (int i=xdpmm/2; (i<width)&&(l*width+i<pixels.length); i=i+xdpmm) {
                index = l*width+i;
                LABColor pixel = ColorHelper.rgbToLab(pixels[index]);
                boolean isRepeated = false;
                Iterator<LABColor> iterator = colors.iterator();
                while (iterator.hasNext() && !isRepeated) {
                    LABColor color = iterator.next();
                    if (ColorHelper.getDifference(color,pixel) <= difference) {
                        if (ColorHelper.getDifferenceWithoutL(color,pixel) <= 2.3) color.setL(pixel.getL());
                        iterator.remove();
                        pixel = color;
                        isRepeated = true;
                    }
                }
                counter.put(pixel.getLab(), counter.get(pixel.getLab(),0)+1);
                colors.add(pixel);
                if (colors.size()==100) {
                    difference = remove(colors, counter, comparator, difference+0.5, 25);
                }
            }
        }

        Collections.sort(colors, comparator);
        int size = 5;
        List<LABColor> copy = new ArrayList<>(Arrays.asList(colors.toArray(new LABColor[0])));
        removeExtra(copy, size, remove(copy, counter, comparator, difference, size),comparator);
        palette[0] = new LABColor[]{copy.get(0), copy.get(1), copy.get(2), copy.get(3), copy.get(4)};

        size = nextSize<(5+colors.size())/2 ? nextSize : (5+colors.size())/2;
        copy = new ArrayList<>(Arrays.asList(colors.toArray(new LABColor[0])));
        removeExtra(copy, size, remove(copy, counter, comparator, difference, size),comparator);
        palette[1] = new LABColor[]{copy.get(0), copy.get(1), copy.get(2), copy.get(3), copy.get(4)};

        size = colors.size();
        copy = new ArrayList<>(Arrays.asList(colors.toArray(new LABColor[0])));
        removeExtra(copy, size, remove(copy, counter, comparator, difference, size),comparator);
        palette[2] = new LABColor[]{copy.get(0), copy.get(1), copy.get(2), copy.get(3), copy.get(4)};

        findViewById(R.id.color1).setBackgroundColor(ColorHelper.labToRgb(palette[0][0]));
        findViewById(R.id.color2).setBackgroundColor(ColorHelper.labToRgb(palette[0][1]));
        findViewById(R.id.color3).setBackgroundColor(ColorHelper.labToRgb(palette[0][2]));
        findViewById(R.id.color4).setBackgroundColor(ColorHelper.labToRgb(palette[0][3]));
        findViewById(R.id.color5).setBackgroundColor(ColorHelper.labToRgb(palette[0][4]));
    }

    double remove(List<LABColor> colors, SparseIntArray counter, Comparator<LABColor> comparator, double difference, int size){
        List<LABColor> copy = new ArrayList<>(Arrays.asList(colors.toArray(new LABColor[0])));
        while (copy.size()>=size) {
            colors.clear();
            colors.addAll(copy);
            for (int i=0; i<copy.size()-1; i++) {
                Iterator<LABColor> iterator = copy.listIterator(i+1);
                while (iterator.hasNext()){
                    LABColor color = iterator.next();
                    double d = ColorHelper.getDifference(copy.get(i), color);
                    if (d <= difference) {
                        if (ColorHelper.getDifferenceWithoutL(color,copy.get(i)) <= 2.3) copy.get(i).setL(color.getL());
                        counter.put(copy.get(i).getLab(), counter.get(copy.get(i).getLab()) + counter.get(color.getLab()));
                        iterator.remove();
                    }

                }
            }
            if (size==25) nextSize=copy.size();
            Collections.sort(copy, comparator);
            difference+=0.5;
        }
        return difference;
    }

    void removeExtra(List<LABColor> colors, int size, double difference, Comparator<LABColor> comparator){
        double dMin;
        int iMin=0;
        while (colors.size()>size) {
            dMin = difference;
            for (int i=0; i<colors.size()-1; i++) {
                for (int j=i+1; j<colors.size(); j++){
                    double d = ColorHelper.getDifference(colors.get(i), colors.get(j));
                    if (d<dMin){
                        dMin = d;
                        iMin = j;
                    }
                }
            }
            colors.remove(iMin);
            Collections.sort(colors, comparator);
        }
    }

    public void showToast(View v){
        String title = "#"+Integer.toHexString(((ColorDrawable)v.getBackground()).getColor()).substring(2);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("color", title);
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(this, R.string.notification, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        findViewById(R.id.color1).setBackgroundColor(0xffffffff);
        findViewById(R.id.color2).setBackgroundColor(0xffffffff);
        findViewById(R.id.color3).setBackgroundColor(0xffffffff);
        findViewById(R.id.color4).setBackgroundColor(0xffffffff);
        findViewById(R.id.color5).setBackgroundColor(0xffffffff);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int index = seekBar.getProgress();
        findViewById(R.id.color1).setBackgroundColor(ColorHelper.labToRgb(palette[index][0]));
        findViewById(R.id.color2).setBackgroundColor(ColorHelper.labToRgb(palette[index][1]));
        findViewById(R.id.color3).setBackgroundColor(ColorHelper.labToRgb(palette[index][2]));
        findViewById(R.id.color4).setBackgroundColor(ColorHelper.labToRgb(palette[index][3]));
        findViewById(R.id.color5).setBackgroundColor(ColorHelper.labToRgb(palette[index][4]));
    }
}
