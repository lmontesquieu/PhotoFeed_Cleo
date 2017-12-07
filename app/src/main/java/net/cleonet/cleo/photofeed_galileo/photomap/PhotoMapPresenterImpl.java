package net.cleonet.cleo.photofeed_galileo.photomap;

import android.util.Log;

import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.photomap.events.PhotoMapEvent;
import net.cleonet.cleo.photofeed_galileo.photomap.ui.PhotoMapView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Pepe on 10/28/17.
 */

public class PhotoMapPresenterImpl implements PhotoMapPresenter {

    private EventBus eventBus;
    private PhotoMapView view;
    private PhotoMapInteractor interactor;

    private static final String EMPTY_LIST = "Listado vacío";
    private static final String TAG = "PhotoMapPresenterImpl";

    public PhotoMapPresenterImpl(EventBus eventBus, PhotoMapView view, PhotoMapInteractor interactor) {
        this.eventBus = eventBus;
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        eventBus.register(this);

    }

    @Override
    public void onDestroy() {
        this.view = null;
        eventBus.unregister(this);
    }

    @Override
    public void subscribe() {
        Log.d(TAG,"subscribe");
        interactor.subscribe();
    }

    @Override
    public void unsubscribe() {
        interactor.unsubscribe();
    }

    @Override
    @Subscribe
    public void onEventMainThread(PhotoMapEvent event) {

        Log.d(TAG,"onEventMainThread");
        if (view != null) {
            Log.d(TAG,"view != null");
            if (event.getError() != null) {
                Log.d(TAG,"event.getError() != null");
                view.onPhotosError(event.getError());
            } else {
                if (event.getType() == PhotoMapEvent.READ_EVENT) {
                    Log.d(TAG,"READ_EVENT");
                    view.addPhoto(event.getPhoto());
                } else if (event.getType() == PhotoMapEvent.DELETE_EVENT) {
                    Log.d(TAG,"DELETE_EVENT");
                    view.removePhoto(event.getPhoto());
                }
            }
        }
    }
}
