package net.cleonet.cleo.photofeed_galileo.lib.base;

/**
 * Created by Pepe on 9/11/17.
 */

public interface ImageStorageFinishedListener {
    void onSuccess();
    void onError(String error);
}
