package net.cleonet.cleo.photofeed_galileo.main.di;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.lib.base.ImageStorage;
import net.cleonet.cleo.photofeed_galileo.main.MainPresenter;
import net.cleonet.cleo.photofeed_galileo.main.MainPresenterImpl;
import net.cleonet.cleo.photofeed_galileo.main.MainRepository;
import net.cleonet.cleo.photofeed_galileo.main.MainRepositoryImpl;
import net.cleonet.cleo.photofeed_galileo.main.SessionInteractor;
import net.cleonet.cleo.photofeed_galileo.main.SessionInteractorImpl;
import net.cleonet.cleo.photofeed_galileo.main.UploadInteractor;
import net.cleonet.cleo.photofeed_galileo.main.UploadInteractorImpl;
import net.cleonet.cleo.photofeed_galileo.main.ui.MainView;
import net.cleonet.cleo.photofeed_galileo.main.ui.adapters.MainSectionsPagerAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 10/6/17.
 */
@Module
public class MainModule {
    private MainView view;
    private String[] titles;
    private Fragment[] fragments;
    private FragmentManager fragmentManager;

    public MainModule(MainView view, String[] titles, Fragment[] fragments, FragmentManager fragmentManager) {
        this.view = view;
        this.titles = titles;
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
    }

    @Provides @Singleton
    MainView providesMainView(){
        return this.view;
    }

    @Provides @Singleton
    MainPresenter providesMainPresenter(MainView view, EventBus eventBus, UploadInteractor uploadInteractor, SessionInteractor sessionInteractor){
        return new MainPresenterImpl(view, eventBus, uploadInteractor, sessionInteractor);
    }

    @Provides @Singleton
    MainRepository providesMainRepository(EventBus eventBus, FirebaseAPI firebaseAPI, ImageStorage imageStorage){
        return new MainRepositoryImpl(eventBus, firebaseAPI, imageStorage);
    }

    @Provides @Singleton
    SessionInteractor providesSessionInteractor(MainRepository repository){
        return new SessionInteractorImpl(repository);
    }

    @Provides @Singleton
    UploadInteractor providesUploadInteractor(MainRepository repository){
        return new UploadInteractorImpl(repository);
    }

    @Provides @Singleton
    MainSectionsPagerAdapter providesAdapter(FragmentManager fm, String[] titles, Fragment[] fragments){
        return new MainSectionsPagerAdapter(fm, titles, fragments);
    }

    @Provides @Singleton
    FragmentManager providesAdapterFragmentManager(){
        return this.fragmentManager;
    }

    @Provides @Singleton
    Fragment[] providesFragmentArrayForAdapter(){
        return this.fragments;
    }

    @Provides @Singleton
    String[] providesStringArrayForAdapter(){
        return this.titles;
    }
}
