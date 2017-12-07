package net.cleonet.cleo.photofeed_galileo.photolist;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.photolist.events.PhotoListEvent;

/**
 * Created by Pepe on 10/27/17.
 */

public interface PhotoListPresenter {
    void onCreate();
    void onDestroy();

    void subscribe();
    void unsubscribe();

    void removePhoto(Photo photo);
    void onEventMainThread(PhotoListEvent event);
}
