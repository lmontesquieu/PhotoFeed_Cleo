package net.cleonet.cleo.photofeed_galileo.photomap.di;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapInteractor;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapInteractorImpl;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapPresenter;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapPresenterImpl;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapRepository;
import net.cleonet.cleo.photofeed_galileo.photomap.PhotoMapRepositoryImpl;
import net.cleonet.cleo.photofeed_galileo.photomap.ui.PhotoMapView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 10/30/17.
 */

@Module
public class PhotoMapModule {
    private PhotoMapView view;

    public PhotoMapModule(PhotoMapView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    PhotoMapView providesPhotoMapView() {
        return this.view;
    }

    @Provides
    @Singleton
    PhotoMapPresenter providesPhotoMapPresenter(EventBus eventBus, PhotoMapView view,
                                                 PhotoMapInteractor mapInteractor) {
        return new PhotoMapPresenterImpl(eventBus, view, mapInteractor);
    }

    @Provides
    @Singleton
    PhotoMapInteractor providesPhotoMapInteractor(PhotoMapRepository repository) {
        return new PhotoMapInteractorImpl(repository);
    }

    @Provides
    @Singleton
    PhotoMapRepository providesPhotoMapRepository(EventBus eventBus, FirebaseAPI firebaseAPI) {
        return new PhotoMapRepositoryImpl(eventBus, firebaseAPI);
    }

}
