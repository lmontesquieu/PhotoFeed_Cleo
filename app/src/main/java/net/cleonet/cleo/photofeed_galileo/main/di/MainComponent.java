package net.cleonet.cleo.photofeed_galileo.main.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;
import net.cleonet.cleo.photofeed_galileo.domain.di.DomainModule;
import net.cleonet.cleo.photofeed_galileo.lib.di.LibsModule;
import net.cleonet.cleo.photofeed_galileo.main.di.MainModule;
import net.cleonet.cleo.photofeed_galileo.main.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pepe on 10/6/17.
 */
@Singleton
@Component(modules= {MainModule.class, DomainModule.class, LibsModule.class, PhotoFeedAppModule.class})
public interface MainComponent {
    void inject(MainActivity activity);
}
