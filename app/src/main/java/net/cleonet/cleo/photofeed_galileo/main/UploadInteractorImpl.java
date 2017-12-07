package net.cleonet.cleo.photofeed_galileo.main;

import android.location.Location;

/**
 * Created by Pepe on 10/6/17.
 */

public class UploadInteractorImpl implements UploadInteractor {
    MainRepository repository;

    public UploadInteractorImpl(MainRepository repository) {
        this.repository = repository;
    }


    @Override
    public void execute(Location location, String path) {
        repository.uploadPhoto(location, path);
    }
}
