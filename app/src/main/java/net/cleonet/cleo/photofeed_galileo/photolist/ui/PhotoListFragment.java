package net.cleonet.cleo.photofeed_galileo.photolist.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedApp;
import net.cleonet.cleo.photofeed_galileo.R;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListPresenter;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters.OnItemClickListener;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters.PhotoListAdapter;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoListFragment extends Fragment implements PhotoListView, OnItemClickListener{


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.container)
    FrameLayout container;
    Unbinder unbinder;

    @Inject
    PhotoListAdapter adapter;
    @Inject
    PhotoListPresenter presenter;

    public final static String TAG = "PhotoListFragment";

    public PhotoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setupInjection();
        presenter.onCreate();
    }

    private void setupInjection() {
        PhotoFeedApp app = (PhotoFeedApp) getActivity().getApplication();
        app.getPhotoListComponent(this, this, this).inject(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        presenter.unsubscribe();
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView();
        presenter.subscribe();
        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG,"onDestroyView");
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideList() {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void addPhoto(Photo photo) {
        Log.d(TAG,"addPhoto");
        adapter.addPhoto(photo);
    }

    @Override
    public void removePhoto(Photo photo) {
        adapter.removePhoto(photo);
    }

    @Override
    public void onPhotosError(String error) {
        Snackbar.make(container, error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaceClick(Photo photo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:" + photo.getLatitude() + " ," + photo.getLongitude()));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onShareClick(Photo photo, ImageView img) {
        Bitmap bitmap = ((GlideBitmapDrawable)img.getDrawable()).getBitmap();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                bitmap, null, null);

        Uri imageUri = Uri.parse(path);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(intent, getString(R.string.photolist_message_share)));
    }

    @Override
    public void onDeleteClick(Photo photo) {
        presenter.removePhoto(photo);
    }
}
