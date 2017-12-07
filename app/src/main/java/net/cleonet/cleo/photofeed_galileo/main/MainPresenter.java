package net.cleonet.cleo.photofeed_galileo.main;

import android.location.Location;

import net.cleonet.cleo.photofeed_galileo.main.events.MainEvent;

/**
 * Created by Pepe on 10/3/17.
 */

public interface MainPresenter {
    void onCreate();
    void onDestroy();

    void logout();
    void uploadPhoto(Location location, String path);
    void onEventMainThread(MainEvent event);

}
