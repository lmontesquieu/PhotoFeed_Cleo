package net.cleonet.cleo.photofeed_galileo.photomap.ui;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;

/**
 * Created by Pepe on 10/27/17.
 */

public interface PhotoMapView {
    void addPhoto(Photo photo);
    void removePhoto(Photo photo);
    void onPhotosError(String error);
}
