package net.cleonet.cleo.photofeed_galileo.main;

/**
 * Created by Pepe on 10/6/17.
 */

public class SessionInteractorImpl implements SessionInteractor {
    MainRepository repository;

    public SessionInteractorImpl(MainRepository repository) {
        this.repository = repository;
    }

    @Override
    public void logout() {
        repository.logout();
    }
}
