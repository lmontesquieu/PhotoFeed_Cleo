package net.cleonet.cleo.photofeed_galileo.login.di;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.login.LoginInteractor;
import net.cleonet.cleo.photofeed_galileo.login.LoginInteractorImpl;
import net.cleonet.cleo.photofeed_galileo.login.LoginPresenter;
import net.cleonet.cleo.photofeed_galileo.login.LoginPresenterImpl;
import net.cleonet.cleo.photofeed_galileo.login.LoginRepository;
import net.cleonet.cleo.photofeed_galileo.login.LoginRepositoryImpl;
import net.cleonet.cleo.photofeed_galileo.login.ui.LoginView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Pepe on 9/12/17.
 */
@Module
public class LoginModule {
    LoginView view;

    public LoginModule(LoginView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    LoginView providesLoginView() {
        return this.view;
    }

    @Provides
    @Singleton
    LoginPresenter providesLoginPresenter(EventBus eventBus, LoginView loginView, LoginInteractor loginInteractor) {
        return new LoginPresenterImpl(eventBus, loginView, loginInteractor);
    }

    @Provides
    @Singleton
    LoginInteractor providesLoginInteractor(LoginRepository loginRepository){
        return new LoginInteractorImpl(loginRepository);
    }

    @Provides
    @Singleton
    LoginRepository providesLoginRepository(EventBus eventBus, FirebaseAPI firebaseAPI){
        return new LoginRepositoryImpl(eventBus, firebaseAPI);
    }
}
