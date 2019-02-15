package com.github.lizlop.galleryApplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageViewActivity extends Activity {
    public static final String EXTRA_IMAGE = "ImageViewActivity.IMAGE";
    private ImageView mImageView;
    private RecyclerView recyclerView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        mImageView = (ImageView) findViewById(R.id.image);
        final MediaStoreData current = getIntent().getParcelableExtra(EXTRA_IMAGE);

        Glide.with(this)
                .load(current.getUri())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(mImageView);

        /*final float[] mx = new float[1];
        final float[] my = new float[1];
        final float[] xval = new float[1];
        final View switcherView = this.findViewById(R.id.scroll_view);

        switcherView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent event) {
                float curX, curY;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mx[0] = event.getX();
                        my[0] = event.getY();
                        xval[0] = mx[0];
                        break;
                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        curY = event.getY();
                        mImageView.scrollBy((int) (mx[0] - curX), 0);
                        mx[0]=curX;
                        break;
                    case MotionEvent.ACTION_UP:
                        curX = event.getX();
                        mImageView.scrollBy((int)-(xval[0] - curX), 0);
                        break;
                }
                return true;
            }
        });*/
    }

}
