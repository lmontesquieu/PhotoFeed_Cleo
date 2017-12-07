package net.cleonet.cleo.photofeed_galileo.main;

import android.location.Location;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageStorage;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageStorageFinishedListener;
import net.cleonet.cleo.photofeed_galileo.main.events.MainEvent;

import java.io.File;

/**
 * Created by Pepe on 10/6/17.
 */

public class MainRepositoryImpl implements MainRepository {
    private EventBus eventBus;
    private FirebaseAPI firebaseAPI;
    private ImageStorage imageStorage;

    public MainRepositoryImpl(EventBus eventBus, FirebaseAPI firebaseAPI, ImageStorage imageStorage) {
        this.eventBus = eventBus;
        this.firebaseAPI = firebaseAPI;
        this.imageStorage = imageStorage;
    }

    @Override
    public void logout() {
        firebaseAPI.logout();
    }

    @Override
    public void uploadPhoto(Location location, String path) {
        final String newPhotoId = firebaseAPI.create();
        final Photo photo = new Photo();
        photo.setId(newPhotoId);
        photo.setEmail(firebaseAPI.getAuthUserEmail());
        if (location != null) {
            photo.setLatitude(location.getLatitude());
            photo.setLongitude(location.getLongitude());
        }
        post(MainEvent.UPLOAD_INIT);
        ImageStorageFinishedListener listener = new ImageStorageFinishedListener() {
            @Override
            public void onSuccess() {
                String url = imageStorage.getImageURL(newPhotoId);
                photo.setUrl(url);
                firebaseAPI.update(photo);
                post(MainEvent.UPLOAD_COMPLETE);
            }

            @Override
            public void onError(String error) {
                post(MainEvent.UPLOAD_ERROR, error);
            }
        };
        imageStorage.upload(new File(path), newPhotoId, listener);
    }

    private void post(int type) {
        post(type,null);
    }

    private void post(int type, String error){
        MainEvent event = new MainEvent();
        event.setType(type);
        event.setError(error);
        eventBus.post(event);
    }
}
