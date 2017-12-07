package net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cleonet.cleo.photofeed_galileo.R;
import net.cleonet.cleo.photofeed_galileo.domain.Util;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pepe on 10/27/17.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {

    private Util utils;
    private List<Photo> photoList;
    private ImageLoader imageLoader;
    private OnItemClickListener onItemClickListener;
    private static final String TAG = "PhotoListAdapter";

    public PhotoListAdapter(Util utils, List<Photo> photoList, ImageLoader imageLoader, OnItemClickListener onItemClickListener) {
        this.utils = utils;
        this.photoList = photoList;
        this.imageLoader = imageLoader;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo currentPhoto = photoList.get(position);
        holder.setOnItemClickListener(currentPhoto, onItemClickListener);

        imageLoader.load(holder.imgMain, currentPhoto.getUrl());
        imageLoader.load(holder.imgAvatar, utils.getAvatarUrl(currentPhoto.getEmail()));
        holder.txtUser.setText(currentPhoto.getEmail());

        double lat = currentPhoto.getLatitude();
        double lon = currentPhoto.getLongitude();

        if (lat != 0.0 && lon != 0.0) {
            Log.d(TAG,"latitude and longitude != 0.0, lat: " + lat + " long: " + lon);
            holder.txtPlace.setText(utils.getFromLocation(lat,lon));
            holder.txtPlace.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG,"latitude and longitude == 0.0");
            holder.txtPlace.setVisibility(View.GONE);
        }

        if (currentPhoto.isPublishedByMe()) {
            Log.d(TAG,"PublishedByMe!");
            holder.imgDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imgDelete.setVisibility(View.GONE);
        }

    }

    public void addPhoto(Photo photo) {
        photoList.add(photo);
        notifyDataSetChanged();
    }

    public void removePhoto(Photo photo) {
        photoList.remove(photo);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgAvatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txtUser)
        TextView txtUser;
        @BindView(R.id.layoutHeader)
        LinearLayout layoutHeader;
        @BindView(R.id.imgMain)
        ImageView imgMain;
        @BindView(R.id.txtPlace)
        TextView txtPlace;
        @BindView(R.id.imgShare)
        ImageButton imgShare;
        @BindView(R.id.imgDelete)
        ImageButton imgDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setOnItemClickListener(final Photo photo, final OnItemClickListener listener) {
            txtPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlaceClick(photo);
                }
            });

            imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShareClick(photo, imgMain);
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteClick(photo);
                }
            });
        }
    }
}
