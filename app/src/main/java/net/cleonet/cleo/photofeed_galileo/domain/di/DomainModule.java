package net.cleonet.cleo.photofeed_galileo.domain.di;

import android.content.Context;
import android.location.Geocoder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.domain.Util;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 9/8/17.
 */

@Module
public class DomainModule {
    String firebaseURL;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    public DomainModule(String firebaseURL, DatabaseReference mDatabase, FirebaseAuth mAuth) {
        this.firebaseURL = firebaseURL;
        this.mDatabase = mDatabase;
        this.mAuth = mAuth;
    }

    @Provides
    @Singleton
//    FirebaseAPI providesFirebaseAPI(Firebase firebase){
    FirebaseAPI providesFirebaseAPI(){
        //return new FirebaseAPI(firebase);
        //return new FirebaseAPI();
        //return FirebaseAPI.getInstance();
        return new FirebaseAPI(mAuth,mDatabase);
    }

//    @Provides
//    @Singleton
//    DatabaseReference providesFirebaseDatabase(FirebaseAPI firebaseAPI){
//    //DatabaseReference providesFirebase(String firebaseURL){
//        //return new FirebaseDatabase();
//        //return FirebaseAPI.getInstance().getDataReference();
//        //return new DatabaseReference();
//        return firebaseAPI.getDataReference();
//    }

//    @Provides
//    @Singleton
//    FirebaseAuth providesFirebaseAuth(FirebaseAPI firebaseAPI){
//        //DatabaseReference providesFirebase(String firebaseURL){
//        //return new FirebaseDatabase();
//        //return FirebaseAPI.getInstance().getMyAuth();
//        return firebaseAPI.getMyAuth();
//    }

    @Provides
    @Singleton
    String providesFirebaseURL(){
        return this.firebaseURL;
    }

    @Provides
    @Singleton
    Util providesUtil(Geocoder geocoder){
        return new Util(geocoder);
    }

    @Provides
    @Singleton
    Geocoder providesGeocoder(Context context){
        return new Geocoder(context);
    }
}
