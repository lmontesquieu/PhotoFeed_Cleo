package net.cleonet.cleo.photofeed_galileo.login.di;

import net.cleonet.cleo.photofeed_galileo.PhotoFeedAppModule;
import net.cleonet.cleo.photofeed_galileo.domain.di.DomainModule;
import net.cleonet.cleo.photofeed_galileo.lib.di.LibsModule;
import net.cleonet.cleo.photofeed_galileo.login.ui.LoginActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pepe on 9/28/17.
 */
@Singleton
@Component(modules = {LoginModule.class, DomainModule.class, LibsModule.class, PhotoFeedAppModule.class})
public interface LoginComponent {
    void inject(LoginActivity activity);
}
