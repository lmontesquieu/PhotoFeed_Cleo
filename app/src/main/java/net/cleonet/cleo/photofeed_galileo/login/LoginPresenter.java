package net.cleonet.cleo.photofeed_galileo.login;


import net.cleonet.cleo.photofeed_galileo.login.events.LoginEvent;

/**
 * Created by Pepe on 10/13/2016.
 */

public interface LoginPresenter {
    void onCreate();
    void onDestroy();
    void validateLogin(String email, String password);
    void registerNewUser(String email, String password);
    void onEventMainThread(LoginEvent event);
}
