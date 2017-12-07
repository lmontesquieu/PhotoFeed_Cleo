package net.cleonet.cleo.photofeed_galileo.photolist;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.domain.FirebaseActionListenerCallback;
import net.cleonet.cleo.photofeed_galileo.domain.FirebaseEventListenerCallback;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.photolist.events.PhotoListEvent;

/**
 * Created by Pepe on 10/28/17.
 */

public class PhotoListRepositoryImpl implements PhotoListRepository {

    private EventBus eventBus;
    private FirebaseAPI firebaseAPI;

    private static String TAG = "PhotoListRepositoryImpl";

    public PhotoListRepositoryImpl(EventBus eventBus, FirebaseAPI firebaseAPI) {
        this.eventBus = eventBus;
        this.firebaseAPI = firebaseAPI;
    }

    @Override
    public void subscribe() {
        firebaseAPI.checkForData(new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception exception) {
                Log.d(TAG,"onError: " + exception.getMessage());
            }

            @Override
            public void onDatabaseError(DatabaseError error) {
                if (error != null) {
                    post(PhotoListEvent.READ_EVENT, error.getMessage());
                } else {
                    post(PhotoListEvent.READ_EVENT, "");
                }
            }
        });

        firebaseAPI.subscribe(new FirebaseEventListenerCallback() {
            @Override
            public void onChildAdded(DataSnapshot snapshot) {
                Photo photo = snapshot.getValue(Photo.class);
                photo.setId(snapshot.getKey());
                post(PhotoListEvent.READ_EVENT, photo);

                String email = firebaseAPI.getAuthUserEmail();
                boolean publishedByMe = photo.getEmail().equals(email);
                photo.setPublishedByMe(publishedByMe);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Photo photo = snapshot.getValue(Photo.class);
                photo.setId(snapshot.getKey());
                post(PhotoListEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                post(PhotoListEvent.READ_EVENT, error.getMessage());
            }
        });
    }

    @Override
    public void unsubscribe() {
        firebaseAPI.unsubscribe();
    }

    @Override
    public void removePhoto(final Photo photo) {
        firebaseAPI.remove(photo, new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {
                post(PhotoListEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onError(Exception exception) {
                Log.d(TAG, "Error: " + exception.getMessage());
                post(PhotoListEvent.DELETE_EVENT, photo);
            }

            @Override
            public void onDatabaseError(DatabaseError error) {
                post(PhotoListEvent.DELETE_EVENT, error.getMessage());
            }
        });
    }

    private void post(int type, Photo photo) {
        post(type, null, photo);
    }

    private void post(int type, String error) {
        post(type, error, null);
    }

    private void post(int type, String error, Photo photo) {
        PhotoListEvent event = new PhotoListEvent();
        event.setType(type);
        event.setError(error);
        event.setPhoto(photo);
        eventBus.post(event);
    }
}
