package net.cleonet.cleo.photofeed_galileo.domain.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pepe on 9/8/17.
 */

@Singleton
@Component(modules = {DomainModule.class, PhotoFeedAppModule.class})
public interface DomainComponent {
}
