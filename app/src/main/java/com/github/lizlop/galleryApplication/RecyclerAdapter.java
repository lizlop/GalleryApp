package com.github.lizlop.galleryApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.support.v4.util.Preconditions;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ImageView;

import com.bumptech.glide.*;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;

import java.util.*;

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ListViewHolder>
        implements ListPreloader.PreloadSizeProvider<MediaStoreData>,
        ListPreloader.PreloadModelProvider<MediaStoreData> {

    private final List<MediaStoreData> data;
    private final int screenWidth;
    private final RequestBuilder<Drawable> requestBuilder;

    private int[] actualDimensions;

    RecyclerAdapter(Context context, List<MediaStoreData> data, RequestManager glideRequests) {
        this.data = data;
        RequestOptions options = new RequestOptions().fitCenter();
        requestBuilder = glideRequests.asDrawable().apply(options);

        setHasStableIds(true);

        screenWidth = getScreenWidth(context);
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.recycler_item, viewGroup, false);
        view.getLayoutParams().width = screenWidth;

        if (actualDimensions == null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (actualDimensions == null) {
                        actualDimensions = new int[] { view.getWidth(), view.getHeight() };
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position) {
        MediaStoreData current = data.get(position);

        Key signature =
                new MediaStoreSignature(current.mimeType, current.dateModified, current.orientation);

        requestBuilder
                .clone()
                .apply(new RequestOptions().signature(signature))
                .load(current.uri)
                .into(viewHolder.image);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).rowId;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public List<MediaStoreData> getPreloadItems(int position) {
        return data.isEmpty()
                ? Collections.<MediaStoreData>emptyList()
                : Collections.singletonList(data.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull MediaStoreData item) {
        MediaStoreSignature signature =
                new MediaStoreSignature(item.mimeType, item.dateModified, item.orientation);
        return requestBuilder
                .clone()
                .apply(new RequestOptions().signature(signature))
                .load(item.uri);
    }

    @Nullable
    @Override
    public int[] getPreloadSize(@NonNull MediaStoreData item, int adapterPosition,
                                int perItemPosition) {
        return actualDimensions;
    }

    // Display#getSize(Point)
    @SuppressWarnings("deprecation")
    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        @SuppressLint("RestrictedApi")
        Display display = Preconditions.checkNotNull(wm).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * ViewHolder containing views to display individual MediaStoreData.
     */
    static final class ListViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        ListViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
