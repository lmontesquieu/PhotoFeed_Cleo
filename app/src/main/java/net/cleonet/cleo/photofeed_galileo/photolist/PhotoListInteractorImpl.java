package net.cleonet.cleo.photofeed_galileo.photolist;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;

/**
 * Created by Pepe on 10/28/17.
 */

public class PhotoListInteractorImpl implements PhotoListInteractor {

    private PhotoListRepository repository;

    public PhotoListInteractorImpl(PhotoListRepository repository) {
        this.repository = repository;
    }

    @Override
    public void subscribe() {
        repository.subscribe();
    }

    @Override
    public void unsubscribe() {
        repository.unsubscribe();
    }

    @Override
    public void removePhoto(Photo photo) {
        repository.removePhoto(photo);
    }
}
