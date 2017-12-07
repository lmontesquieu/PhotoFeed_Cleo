package net.cleonet.cleo.photofeed_galileo.lib.base;

import android.widget.ImageView;

/**
 * Created by Pepe on 10/20/16.
 */
public interface ImageLoader {
    void load(ImageView imgAvatar, String url);
    void setOnFinishedImageLoadingListener(Object listener);
}
