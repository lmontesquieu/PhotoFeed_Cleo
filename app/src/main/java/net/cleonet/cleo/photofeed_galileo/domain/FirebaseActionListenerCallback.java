package net.cleonet.cleo.photofeed_galileo.domain;


import com.google.firebase.database.DatabaseError;

/**
 * Created by Pepe on 9/7/17.
 */

public interface FirebaseActionListenerCallback {
    public void onSuccess();
    public void onError(Exception exception);
    public void onDatabaseError(DatabaseError error);
}
