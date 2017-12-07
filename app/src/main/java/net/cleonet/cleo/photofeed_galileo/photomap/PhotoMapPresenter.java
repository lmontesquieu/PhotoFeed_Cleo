package net.cleonet.cleo.photofeed_galileo.photomap;

import net.cleonet.cleo.photofeed_galileo.photomap.events.PhotoMapEvent;

/**
 * Created by Pepe on 10/27/17.
 */

public interface PhotoMapPresenter {
    void onCreate();
    void onDestroy();

    void subscribe();
    void unsubscribe();

    void onEventMainThread(PhotoMapEvent event);
}
