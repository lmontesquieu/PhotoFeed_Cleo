package net.cleonet.cleo.photofeed_galileo.lib.base;

import java.io.File;

/**
 * Created by Pepe on 9/11/17.
 */

public interface ImageStorage {
    String getImageURL(String id);
    void upload(File file, String id, ImageStorageFinishedListener listener);
}
