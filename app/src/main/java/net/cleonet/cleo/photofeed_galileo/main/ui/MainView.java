package net.cleonet.cleo.photofeed_galileo.main.ui;

/**
 * Created by Pepe on 10/3/17.
 */

public interface MainView {
    void onUploadInit();
    void onUploadComplete();
    void onUploadError(String error);
}
