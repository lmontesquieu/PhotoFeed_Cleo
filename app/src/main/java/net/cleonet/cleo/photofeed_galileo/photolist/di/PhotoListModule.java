package net.cleonet.cleo.photofeed_galileo.photolist.di;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.domain.Util;
import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageLoader;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListInteractor;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListInteractorImpl;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListPresenter;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListPresenterImpl;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListRepository;
import net.cleonet.cleo.photofeed_galileo.photolist.PhotoListRepositoryImpl;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.PhotoListView;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters.OnItemClickListener;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.adapters.PhotoListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 10/30/17.
 */

@Module
public class PhotoListModule {
    private PhotoListView view;
    OnItemClickListener onItemClickListener;

    public PhotoListModule(PhotoListView view, OnItemClickListener onItemClickListener) {
        this.view = view;
        this.onItemClickListener = onItemClickListener;
    }

    @Provides
    @Singleton
    PhotoListView providesPhotoListView() {
        return this.view;
    }

    @Provides
    @Singleton
    PhotoListPresenter providesPhotoListPresenter(EventBus eventBus, PhotoListView view,
                                                  PhotoListInteractor listInteractor) {
        return new PhotoListPresenterImpl(eventBus, view, listInteractor);
    }

    @Provides
    @Singleton
    PhotoListInteractor providesPhotoListInteractor(PhotoListRepository repository) {
        return new PhotoListInteractorImpl(repository);
    }

    @Provides
    @Singleton
    PhotoListRepository providesPhotoListRepository(EventBus eventBus, FirebaseAPI firebaseAPI) {
        return new PhotoListRepositoryImpl(eventBus, firebaseAPI);
    }

    @Provides
    @Singleton
    PhotoListAdapter providesPhotoListAdapter(Util utils, List<Photo> photoList, ImageLoader imageLoader,
                                              OnItemClickListener onItemClickListener) {
        return new PhotoListAdapter(utils, photoList, imageLoader, onItemClickListener);
    }

    @Provides
    @Singleton
    OnItemClickListener  providesOnItemClickListener(){
        return this.onItemClickListener;
    }

    @Provides
    @Singleton
    List<Photo> providesPhotosList(){
        return new ArrayList<Photo>();
    }
}
