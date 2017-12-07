package net.cleonet.cleo.photofeed_galileo;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cleonet.cleo.photofeed_galileo.domain.di.DomainModule;
import net.cleonet.cleo.photofeed_galileo.lib.di.LibsModule;
import net.cleonet.cleo.photofeed_galileo.login.di.DaggerLoginComponent;
import net.cleonet.cleo.photofeed_galileo.login.di.LoginComponent;
import net.cleonet.cleo.photofeed_galileo.login.di.LoginModule;
import net.cleonet.cleo.photofeed_galileo.login.ui.LoginView;
import net.cleonet.cleo.photofeed_galileo.main.di.DaggerMainComponent;
import net.cleonet.cleo.photofeed_galileo.main.di.MainComponent;
import net.cleonet.cleo.photofeed_galileo.main.di.MainModule;
import net.cleonet.cleo.photofeed_galileo.main.ui.MainView;
import net.cleonet.cleo.photofeed_galileo.photolist.di.DaggerPhotoListComponent;
import net.cleonet.cleo.photofeed_galileo.photolist.di.PhotoListComponent;
import net.cleonet.cleo.photofeed_galileo.photolist.di.PhotoListModule;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.PhotoListFragment;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.PhotoListView;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters.OnItemClickListener;
import net.cleonet.cleo.photofeed_galileo.photomap.di.DaggerPhotoMapComponent;
import net.cleonet.cleo.photofeed_galileo.photomap.di.PhotoMapComponent;
import net.cleonet.cleo.photofeed_galileo.photomap.di.PhotoMapModule;
import net.cleonet.cleo.photofeed_galileo.photomap.ui.*;
import net.cleonet.cleo.photofeed_galileo.photomap.ui.PhotoMapFragment;

/**
 * Created by Pepe on 9/8/17.
 */

public class PhotoFeedApp extends Application {

    private final static String EMAIL_KEY = "email";
    private final static String SHARED_PREFERENCES_NAME = "UserPrefs";
    private final static String FIREBASE_URL = "https://cleoPhotoFeed.firebaseio.com/";
    private final static String TAG = "PhotoFeedApp";

    private PhotoFeedAppModule photoFeedAppModule;
    private DomainModule domainModule;
    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
        initFirebase();
        initModules();
    }

    private void initModules() {
        Log.d(TAG,"initModules");
        photoFeedAppModule = new PhotoFeedAppModule(this);
        domainModule = new DomainModule(FIREBASE_URL, mDatabase, mAuth);
    }

    private void initFirebase() {
        //Firebase.setAndroidContext(this);
        Log.d(TAG,"initFirebase");
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public String getEmailKey() {
        return EMAIL_KEY;
    }

    public String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    public LoginComponent getLoginComponent(LoginView view) {
        return DaggerLoginComponent
                .builder()
                .photoFeedAppModule(photoFeedAppModule)
                .domainModule(domainModule)
                .libsModule(new LibsModule(null))
                .loginModule(new LoginModule(view))
                .build();
    }

    public MainComponent getMainComponent(MainView view, String[] titles, Fragment[] fragments, FragmentManager fragmentManager) {
        return DaggerMainComponent
                .builder()
                .photoFeedAppModule(photoFeedAppModule)
                .domainModule(domainModule)
                .libsModule(new LibsModule(null))
                .mainModule(new MainModule(view, titles, fragments, fragmentManager))
                .build();
    }

    public PhotoListComponent getPhotoListComponent(PhotoListFragment fragment, PhotoListView photoListView, OnItemClickListener onItemClickListener){
        Log.d(TAG,"getPhotoMapComponent");
        return DaggerPhotoListComponent
                .builder()
                .photoFeedAppModule(photoFeedAppModule)
                .domainModule(domainModule)
                // Requiero el fragmento, para mostrar contenido (?)
                .libsModule(new LibsModule(fragment))
                .photoListModule(new PhotoListModule(photoListView, onItemClickListener))
                .build();
    }

    public PhotoMapComponent getPhotoMapComponent(PhotoMapFragment photoMapFragment, PhotoMapView photoMapView) {
        Log.d(TAG,"getPhotoMapComponent");
        return DaggerPhotoMapComponent
                .builder()
                .photoFeedAppModule(photoFeedAppModule)
                .domainModule(domainModule)
                // Requiero el fragmento, para mostrar contenido (?)
                .libsModule(new LibsModule(photoMapFragment))
                .photoMapModule(new PhotoMapModule(photoMapView))
                .build();
    }
}
