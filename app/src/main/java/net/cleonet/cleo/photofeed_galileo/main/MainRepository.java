package net.cleonet.cleo.photofeed_galileo.main;

import android.location.Location;

/**
 * Created by Pepe on 10/3/17.
 */

public interface MainRepository {
    void logout();
    void uploadPhoto(Location location, String path);
}
