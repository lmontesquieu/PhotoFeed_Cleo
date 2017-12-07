package net.cleonet.cleo.photofeed_galileo.photolist;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;

/**
 * Created by Pepe on 10/27/17.
 */

public interface PhotoListRepository {
    void subscribe();
    void unsubscribe();

    void removePhoto(Photo photo);
}
