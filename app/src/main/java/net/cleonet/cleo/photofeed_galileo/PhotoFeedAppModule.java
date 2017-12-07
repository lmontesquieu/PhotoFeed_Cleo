package net.cleonet.cleo.photofeed_galileo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 9/8/17.
 */

@Module
public class PhotoFeedAppModule {
    PhotoFeedApp app;

    public PhotoFeedAppModule(PhotoFeedApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context provideApplicationContext(){
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application){
        return application.getSharedPreferences(app.getSharedPreferencesName(), Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Application providesApplication(){
        return this.app;
    }
}
