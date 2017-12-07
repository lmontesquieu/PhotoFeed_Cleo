package net.cleonet.cleo.photofeed_galileo.photolist.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;
import net.cleonet.cleo.photofeed_galileo.domain.di.DomainModule;
import net.cleonet.cleo.photofeed_galileo.lib.di.LibsModule;
import net.cleonet.cleo.photofeed_galileo.photolist.ui.PhotoListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules= {PhotoListModule.class, DomainModule.class, LibsModule.class, PhotoFeedAppModule.class})
public interface PhotoListComponent {
    void inject(PhotoListFragment activity);
}