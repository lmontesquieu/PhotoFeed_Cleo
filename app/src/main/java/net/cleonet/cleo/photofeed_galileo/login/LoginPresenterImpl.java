package net.cleonet.cleo.photofeed_galileo.login;

import android.util.Log;

import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.login.events.LoginEvent;
import net.cleonet.cleo.photofeed_galileo.login.ui.LoginView;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Pepe on 10/13/2016.
 */

public class LoginPresenterImpl implements LoginPresenter {

    private static final String TAG = "LoginPresenterImpl";
    private EventBus eventBus;
    private LoginView loginView;
    private LoginInteractor loginInteractor;

    public LoginPresenterImpl(EventBus eventBus, LoginView loginView, LoginInteractor loginInteractor) {
        this.eventBus = eventBus;
        this.loginView = loginView;
        this.loginInteractor = loginInteractor;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        loginView = null;
        eventBus.unregister(this);
    }

    @Override
    public void validateLogin(String email, String password) {
        if (loginView != null) {
            loginView.disableInputs();
            loginView.showProgress();
        }
        loginInteractor.doSignIn(email, password);
    }

    @Override
    public void registerNewUser(String email, String password) {
        if (loginView != null) {
            loginView.disableInputs();
            loginView.showProgress();
        }
        loginInteractor.doSignUp(email, password);
    }

    @Override
    @Subscribe
    public void onEventMainThread(LoginEvent event) {
        Log.d(TAG,"onEventMainThread");
        switch (event.getEventType()) {
            case LoginEvent.onSignInSuccess:
                onSignInSuccess(event.getCurrentUserEmail());
                break;
            case LoginEvent.onSignUpSuccess:
                onSignUpSuccess();
                break;
            case LoginEvent.onSignInError:
                onSignInError(event.getErrorMessage());
                break;
            case LoginEvent.onSignUpError:
                onSignUpError(event.getErrorMessage());
                break;
            case LoginEvent.onFailedToRecoverSession:
                onFailedToRecoverSession();
                break;
        }
    }

    private void onFailedToRecoverSession() {
        if (loginView != null) {
            loginView.hideProgress();
            loginView.enableInputs();
        }
    }

    private void onSignInSuccess(String email) {
        Log.d(TAG,"onSignInSuccess!");
        if (loginView != null) {
            loginView.setUserEmail(email);
            loginView.navigateToMainScreen();
        }
    }
    private void onSignInError(String error) {
        Log.d(TAG, error);
        if (loginView != null) {
            loginView.hideProgress();
            loginView.enableInputs();
            loginView.loginError(error);
        }
    }
    private void onSignUpSuccess() {
        Log.d(TAG,"onSignUpSuccess");
        if (loginView != null) {
            loginView.newUserSuccess();
        }
    }
    private void onSignUpError(String error) {
        Log.d(TAG,"onSignUpError");
        if (loginView != null) {
            loginView.hideProgress();
            loginView.enableInputs();
            loginView.newUserError(error);
        }
    }
}
