package net.cleonet.cleo.photofeed_galileo.photomap;

import android.util.Log;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;

/**
 * Created by Pepe on 10/28/17.
 */

public class PhotoMapInteractorImpl implements PhotoMapInteractor {

    private PhotoMapRepository repository;
    private final static String TAG = "PhotoMapInteractorImpl";

    public PhotoMapInteractorImpl(PhotoMapRepository repository) {
        this.repository = repository;
    }

    @Override
    public void subscribe() {
        Log.d(TAG,"subscribe");
        repository.subscribe();
    }

    @Override
    public void unsubscribe() {
        repository.unsubscribe();
    }

}
