package net.cleonet.cleo.photofeed_galileo.main;

import android.location.Location;

import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.main.events.MainEvent;
import net.cleonet.cleo.photofeed_galileo.main.ui.MainView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Pepe on 10/6/17.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    private EventBus eventBus;
    private UploadInteractor uploadInteractor;
    private SessionInteractor sessionInteractor;

    public MainPresenterImpl(MainView view, EventBus eventBus, UploadInteractor uploadInteractor, SessionInteractor sessionInteractor) {
        this.view = view;
        this.eventBus = eventBus;
        this.uploadInteractor = uploadInteractor;
        this.sessionInteractor = sessionInteractor;
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void logout() {
        sessionInteractor.logout();
    }

    @Override
    public void uploadPhoto(Location location, String path) {
        uploadInteractor.execute(location, path);
    }

    @Override
    @Subscribe
    public void onEventMainThread(MainEvent event) {
        if (this.view != null) {
            switch(event.getType()) {
                case MainEvent.UPLOAD_INIT:
                    view.onUploadInit();
                    break;
                case MainEvent.UPLOAD_COMPLETE:
                    view.onUploadComplete();
                    break;
                case MainEvent.UPLOAD_ERROR:
                    view.onUploadError(event.getError());
                    break;
            }
        }
    }
}
