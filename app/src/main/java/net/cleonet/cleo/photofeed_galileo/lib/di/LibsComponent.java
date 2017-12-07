package net.cleonet.cleo.photofeed_galileo.lib.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedApp;
import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pepe on 9/11/17.
 */

@Singleton
@Component(modules = {LibsModule.class, PhotoFeedAppModule.class})
public interface LibsComponent {
}
