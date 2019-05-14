package com.github.lizlop.galleryApplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;

import java.util.List;

public class VerticalGalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<MediaStoreData>> {

    private RecyclerView recyclerView;
    static private Parcelable mListState;
    static private RecyclerView.LayoutManager mLayoutManager;
    static String LIST_STATE_KEY = "list.KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(R.id.loader_id_media_store_data, null, this);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = result.findViewById(R.id.recycler_view);
        int spanCount = (getActivity().getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)?6:4;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        return result;
    }

    @Override
    public Loader<List<MediaStoreData>> onCreateLoader(int i, Bundle bundle) {
        return new MediaStoreDataLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<MediaStoreData>> loader,
                               List<MediaStoreData> mediaStoreData) {
        RequestManager glideRequests = Glide.with(this);
        RecyclerAdapter adapter =
                new RecyclerAdapter(getActivity(), mediaStoreData, glideRequests);
        RecyclerViewPreloader<MediaStoreData> preloader =
                new RecyclerViewPreloader<>(glideRequests, adapter, adapter, 3);
        recyclerView.addOnScrollListener(preloader);
        recyclerView.setAdapter(adapter);
        mLayoutManager=recyclerView.getLayoutManager();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MediaStoreData>> loader) {
        // Do nothing.
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        mLayoutManager=recyclerView.getLayoutManager();
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Retrieve list state and list/item positions
        if(savedInstanceState != null)
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    }
}
