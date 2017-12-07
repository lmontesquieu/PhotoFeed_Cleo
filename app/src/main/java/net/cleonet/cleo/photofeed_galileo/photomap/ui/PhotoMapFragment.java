package net.cleonet.cleo.photofeed_galileo.photomap.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedApp;
import net.cleonet.cleo.photofeed_galileo.R;
import net.cleonet.cleo.photofeed_galileo.domain.Util;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageLoader;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapPresenter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoMapFragment extends Fragment implements PhotoMapView, OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    @BindView(R.id.container)
    FrameLayout container;

    @Inject
    ImageLoader imageLoader;

    @Inject
    Util util;

    @Inject
    PhotoMapPresenter presenter;

    private GoogleMap map;
    private HashMap<Marker, Photo> markers;
    private final static int PERMISSIONS_REQUEST_LOCATION = 1;
    private final static String TAG = "PhotoMapFragment";

    public PhotoMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"OnCreate");
        super.onCreate(savedInstanceState);
        setupInjection();
        markers = new HashMap<Marker, Photo>();
        presenter.onCreate();
    }

    private void setupInjection() {
        PhotoFeedApp app = (PhotoFeedApp) getActivity().getApplication();
        app.getPhotoMapComponent(this, this).inject(this);
    }

    @Override
    public void onDestroy() {
        presenter.unsubscribe();
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG,"onActivityCreated");
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void addPhoto(Photo photo) {
        Log.d(TAG,"ADD PHOTO AND MARKER");
        LatLng location = new LatLng(photo.getLatitude(), photo.getLongitude());

        Marker marker = map.addMarker(new MarkerOptions().position(location));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location,6));
        markers.put(marker, photo);
    }

    @Override
    public void removePhoto(Photo photo) {
        Log.d(TAG,"REMOVE PHOTO AND MARKER");
        for (Map.Entry<Marker, Photo> entry : markers.entrySet()) {
            Photo currentPhoto = entry.getValue();
            Marker currentMarker = entry.getKey();
            if (currentPhoto.getId().equals(photo.getId())) {
                currentMarker.remove();
                markers.remove(currentMarker);
                break;
            }
        }
    }

    @Override
    public void onPhotosError(String error) {
        Snackbar.make(container, error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        presenter.subscribe();

        map.setInfoWindowAdapter(this);

        Log.d(TAG, "ON MAP READY");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }

        map.setMyLocationEnabled(true);
        return;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (map != null) {
                        map.setMyLocationEnabled(true);
                    }
                }
                return;
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.d(TAG,"GetInfoWindow");
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.d(TAG, "getInfoContents");
        View view = getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        Photo photo = markers.get(marker);

        CircleImageView imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        TextView txtUser = (TextView) view.findViewById(R.id.txtUser);
        ImageView imgMain = (ImageView) view.findViewById(R.id.imgMain);

        imageLoader.load(imgMain, photo.getUrl());
        imageLoader.load(imgAvatar, util.getAvatarUrl(photo.getEmail()));
        txtUser.setText(photo.getEmail());

        return view;
    }
}
