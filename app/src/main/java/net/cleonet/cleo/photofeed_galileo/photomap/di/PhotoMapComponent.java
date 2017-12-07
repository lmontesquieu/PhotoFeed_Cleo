package net.cleonet.cleo.photofeed_galileo.photomap.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;
import net.cleonet.cleo.photofeed_galileo.domain.di.DomainModule;
import net.cleonet.cleo.photofeed_galileo.lib.di.LibsModule;
import net.cleonet.cleo.photofeed_galileo.photomap.ui.PhotoMapFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules= {PhotoMapModule.class, DomainModule.class, LibsModule.class, PhotoFeedAppModule.class})
public interface PhotoMapComponent {
    void inject(PhotoMapFragment activity);
}