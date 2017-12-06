package net.cleonet.cleo.photofeed_galileo.main.ui;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedApp;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.PhotoListFragment;
import net.cleonet.cleo.photofeed_galileo.PhotoMapFragment;
import net.cleonet.cleo.photofeed_galileo.R;
import net.cleonet.cleo.photofeed_galileo.login.ui.LoginActivity;
import net.cleonet.cleo.photofeed_galileo.main.MainPresenter;
import net.cleonet.cleo.photofeed_galileo.main.ui.adapters.MainSectionsPagerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @Inject
    MainPresenter presenter;
    @Inject
    MainSectionsPagerAdapter adapter;
    @Inject
    SharedPreferences sharedPreferences;

    private String photoPath;
    private PhotoFeedApp app;
    private Location lastKnownLocation;
    private GoogleApiClient apiClient;

    private boolean resolvingError = false;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PICTURE = 1;
    private static final int REQUEST_RESOLVE_ERROR = 0;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        app = (PhotoFeedApp) getApplication();
        setupInjection();
        setupNavigation();
        setupGoogleAPIClient();

        presenter.onCreate();
    }

    private void setupGoogleAPIClient() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();

    }

    @Override
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult. requestCode=" + requestCode + " resultCode=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK) {
                if (!apiClient.isConnecting() && !apiClient.isConnected()) {
                    apiClient.connect();
                }
            }
        } else if (requestCode == REQUEST_PICTURE) {
            if (resultCode == RESULT_OK) {
                boolean fromCamera = (data == null || data.getData() == null);
                Log.d(TAG, "fromCamera: " + fromCamera);
                if (fromCamera) {
                    addToGallery();
                } else {
                    photoPath = getRealPathFromURI(data.getData());
                }
                presenter.uploadPhoto(lastKnownLocation, photoPath);
            } else {
                Log.d(TAG, "REQUEST_CANCELLED");
            }
        }
    }

    private void setupNavigation() {
        String email = sharedPreferences.getString(app.getEmailKey(), getString(R.string.app_name));
        toolbar.setTitle(email);
        setSupportActionBar(toolbar);

        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

    }

    private void setupInjection() {
        String[] titles = new String[]{ getString(R.string.main_title_list), getString(R.string.main_title_map)};
        Fragment[] fragments = new Fragment[] { new PhotoListFragment(), new PhotoMapFragment()};
        app.getMainComponent(this,titles,fragments,getSupportFragmentManager()).inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        presenter.logout();
        sharedPreferences.edit().clear().commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }

        if (LocationServices.FusedLocationApi.getLocationAvailability(apiClient).isLocationAvailable()){
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        } else {
            showSnackBar(R.string.main_error_location_notavailable);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (LocationServices.FusedLocationApi.getLocationAvailability(apiClient).isLocationAvailable()){
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                    } else {
                        showSnackBar(R.string.main_error_location_notavailable);
                    }
                }
                return;
            }
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    Log.d(TAG,"Camera permission granted");
                } else {
                    Log.d(TAG,"Camera permission NOT granted");
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (resolvingError){
            return;
        } else if (connectionResult.hasResolution()) {
            resolvingError = true;
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            resolvingError = true;
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), REQUEST_RESOLVE_ERROR).show();
        }
    }

    @Override
    public void onUploadInit() {
        showSnackBar(R.string.main_notice_upload_init);
    }

    @Override
    public void onUploadComplete() {
        showSnackBar(R.string.main_notice_upload_complete);
    }

    @Override
    public void onUploadError(String error) {
        showSnackBar(error);
    }

    @OnClick(R.id.fab)
    public void takePicture() {
        Log.d(TAG,"takePicture");
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("return-data",true);

        File photoFile = getFile();

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            if (cameraIntent.resolveActivity(getPackageManager()) != null){
                intentList = addIntentsToList(intentList,cameraIntent);
            }
        }

        if (pickIntent.resolveActivity(getPackageManager()) != null){
            intentList = addIntentsToList(intentList,pickIntent);
        }

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    getString(R.string.main_message_picture_source));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));

        }

        checkPermissionCamera();

        if (chooserIntent != null) {
            startActivityForResult(chooserIntent,REQUEST_PICTURE);
        }

    }

    private void checkPermissionCamera() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG,"Explanation!");

            } else {

                // No explanation needed, we can request the permission.

                //ActivityCompat.requestPermissions(this,
                //        new String[]{Manifest.permission.CAMERA},
                //        MY_PER);
                Log.d(TAG,"No explanation!");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private File getFile() {
        Log.d(TAG,"getFile");
        File photoFile = null;
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File storageDir = getBaseContext().getExternalFilesDir(null);
        //File storageDir = new File(this.getExternalFilesDir(null), imageFileName);
        Log.d(TAG,"getExternalStorage");
//        try {
            //photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            //photoFile = File.createTempFile(imageFileName, ".jpg", this.getCacheDir());
            //photoFile = new File(getActivity().getExternalFilesDir(null), fileName);
            photoFile = new File(this.getExternalFilesDir(null), imageFileName);
            Log.d(TAG,"createdTempFile");
            photoPath = photoFile.getAbsolutePath();
//        } catch (IOException e) {
//            Log.d(TAG,e.getMessage());Log.d(TAG,e.getLocalizedMessage());
//            showSnackBar(R.string.main_error_dispatch_camera);
//        }

        if (photoPath != null) {
            Log.d(TAG,"photoPath: " + photoPath);
        }
        else {
            Log.d(TAG,"photoPath is null");
        }
        return photoFile;
    }

    private List<Intent> addIntentsToList(List<Intent> list, Intent intent){
        Log.d(TAG,"addIntentsToList");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo){
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetIntent = new Intent(intent);
            targetIntent.setPackage(packageName);
            list.add(targetIntent);
        }
        return list;
    }

    private void addToGallery() {
        Log.d(TAG,"addToGallery");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(photoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private String getRealPathFromURI(Uri contentURI) {
        Log.d(TAG,"getRealPathFromURI");
        String result = null;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            if (contentURI.toString().contains("mediaKey")) {
                cursor.close();
                try {
                    File file = File.createTempFile("tempImg", ".jpg", getCacheDir());
                    InputStream input = getContentResolver().openInputStream(contentURI);
                    OutputStream output = new FileOutputStream(file);
                    try {
                        byte[] buffer = new byte[4 * 1024];
                        int read;

                        while ( (read = input.read(buffer)) != -1 ) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        result = file.getAbsolutePath();
                    } finally {
                        output.close();
                        input.close();
                    }
                } catch (Exception e) {
                }
            } else {
                cursor.moveToFirst();
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(dataColumn);
                cursor.close();
            }
        }
        return result;
    }

    private void showSnackBar(String msg) {
        Log.d(TAG, "showSnackBar " + msg);
        Snackbar.make(viewPager, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void showSnackBar(int strResource) {
        Log.d(TAG, "showSnackBarStrResource " + strResource);
        Snackbar.make(viewPager, strResource, Snackbar.LENGTH_SHORT).show();
    }
}
