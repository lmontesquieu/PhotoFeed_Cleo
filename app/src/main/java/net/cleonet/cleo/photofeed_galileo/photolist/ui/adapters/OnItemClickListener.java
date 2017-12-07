package net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters;

import android.widget.ImageView;

import net.cleonet.cleo.photofeed_galileo.entities.Photo;

/**
 * Created by Pepe on 10/27/17.
 */

public interface OnItemClickListener {
    void onPlaceClick(Photo photo);
    void onShareClick(Photo photo, ImageView img);
    void onDeleteClick(Photo photo);

}
