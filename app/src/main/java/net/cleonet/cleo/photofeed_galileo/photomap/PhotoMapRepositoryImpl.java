package net.cleonet.cleo.photofeed_galileo.photomap;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.domain.FirebaseEventListenerCallback;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.photomap.events.PhotoMapEvent;

/**
 * Created by Pepe on 10/28/17.
 */

public class PhotoMapRepositoryImpl implements PhotoMapRepository {

    private EventBus eventBus;
    private FirebaseAPI firebaseAPI;

    private static String TAG = "PhotoMapRepositoryImpl";

    public PhotoMapRepositoryImpl(EventBus eventBus, FirebaseAPI firebaseAPI) {
        this.eventBus = eventBus;
        this.firebaseAPI = firebaseAPI;
    }

    @Override
    public void subscribe() {
        Log.d(TAG,"subscribe");
        firebaseAPI.subscribe(new FirebaseEventListenerCallback() {
            @Override
            public void onChildAdded(DataSnapshot snapshot) {
                Log.d(TAG,"onChildAdded");
                Photo photo = snapshot.getValue(Photo.class);
                photo.setId(snapshot.getKey());

                String email = firebaseAPI.getAuthUserEmail();
                boolean publishedByMe = photo.getEmail().equals(email);
                photo.setPublishedByMe(publishedByMe);
                post(PhotoMapEvent.READ_EVENT, photo);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Log.d(TAG,"onChildRemoved");
                Photo photo = snapshot.getValue(Photo.class);
                photo.setId(snapshot.getKey());
                post(PhotoMapEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,"onError");
                post(PhotoMapEvent.READ_EVENT, error.getMessage());
            }
        });
    }

    @Override
    public void unsubscribe() {
        firebaseAPI.unsubscribe();
    }

    private void post(int type, Photo photo) {
        post(type, null, photo);
    }

    private void post(int type, String error) {
        post(type, error, null);
    }

    private void post(int type, String error, Photo photo) {
        Log.d(TAG,"POST");
        PhotoMapEvent event = new PhotoMapEvent();
        event.setType(type);
        event.setError(error);
        event.setPhoto(photo);
        eventBus.post(event);
    }
}
