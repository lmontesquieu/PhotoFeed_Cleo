package net.cleonet.cleo.photofeed_galileo.lib;

import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;

/**
 * Created by Pepe on 10/13/2016.
 */

public class GreenRobotEventBus implements EventBus {

    org.greenrobot.eventbus.EventBus eventBus;

    public GreenRobotEventBus(org.greenrobot.eventbus.EventBus eventBus) {
        //Log.d(TAG, "GreenRobotEventBus");
        this.eventBus = eventBus;
    }

    private static class SingletonHolder {
        private static final GreenRobotEventBus INSTANCE = new GreenRobotEventBus();
    }

    public GreenRobotEventBus() {
        //Log.d(TAG, "GreenRobotEventBus");
        this.eventBus = org.greenrobot.eventbus.EventBus.getDefault();
    }

    public static GreenRobotEventBus getInstance() {
        //Log.d(TAG, "getInstance");
        return SingletonHolder.INSTANCE;
    }
    @Override
    public void register(Object suscriber) {
        //Log.d(TAG, "register");
        eventBus.register(suscriber);
    }

    @Override
    public void unregister(Object suscriber) {
        eventBus.unregister(suscriber);
    }

    @Override
    public void post(Object event) {
        eventBus.post(event);
    }
}
