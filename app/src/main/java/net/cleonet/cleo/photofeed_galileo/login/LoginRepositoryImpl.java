package net.cleonet.cleo.photofeed_galileo.login;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import net.cleonet.cleo.photofeed_galileo.domain.FirebaseAPI;
import net.cleonet.cleo.photofeed_galileo.domain.FirebaseActionListenerCallback;
import net.cleonet.cleo.photofeed_galileo.lib.base.EventBus;
import net.cleonet.cleo.photofeed_galileo.login.events.LoginEvent;

/**
 * Created by Pepe on 10/13/2016.
 */

public class LoginRepositoryImpl implements LoginRepository {

    private static final String TAG = "LoginRepositoryImpl";
    private EventBus eventBus;
    private FirebaseAPI firebaseAPI;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public LoginRepositoryImpl(EventBus eventBus, FirebaseAPI firebaseAPI) {
        this.eventBus = eventBus;
        this.firebaseAPI = firebaseAPI;
        //this.firebaseAPI = FirebaseAPI.getInstance();
        this.mDatabase = firebaseAPI.getDataReference();
        this.mAuth = firebaseAPI.getMyAuth();
        this.mAuthListener = firebaseAPI.getMyAuthListener();
        this.mAuth.addAuthStateListener(this.mAuthListener);
    }

    @Override
    public void signUp(final String email, final String password) {
        firebaseAPI.signup(email, password, new FirebaseActionListenerCallback() {
            @Override
            public void onSuccess() {
                postEvent(LoginEvent.onSignUpSuccess);
                signIn(email,password);
            }

            @Override
            public void onError(Exception exception) {
                postEvent(LoginEvent.onSignUpError, exception.getMessage(), null);
            }

            @Override
            public void onDatabaseError(DatabaseError error) {
                postEvent(LoginEvent.onSignUpError, error.getMessage(), null);
            }
        });
    }

    @Override
    public void signIn(final String email, final String password) {
        if (email != null && password != null) {
            firebaseAPI.login(email, password, new FirebaseActionListenerCallback() {
                @Override
                public void onSuccess() {
                    String email = firebaseAPI.getAuthUserEmail();
                    postEvent(LoginEvent.onSignInSuccess, email);
                }

                @Override
                public void onError(Exception exception) {
                    postEvent(LoginEvent.onSignInError, exception.getMessage(), null);
                }

                @Override
                public void onDatabaseError(DatabaseError error) {
                    postEvent(LoginEvent.onSignInError, error.getMessage(), null);
                }
            });
        } else {
            firebaseAPI.checkSession(new FirebaseActionListenerCallback() {
                @Override
                public void onSuccess() {
                    String email = firebaseAPI.getAuthUserEmail();
                    postEvent(LoginEvent.onSignInSuccess, email);
                }

                @Override
                public void onError(Exception exception) {
                    postEvent(LoginEvent.onFailedToRecoverSession);
                }

                @Override
                public void onDatabaseError(DatabaseError error) {
                    postEvent(LoginEvent.onFailedToRecoverSession);
                }
            });
        }
    }

    private void postEvent(int type, String errorMessage, String currentUserEmail) {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.setEventType(type);
        //Log.d(TAG, errorMessage);
        Log.d(TAG,"errorPostEvent");
        loginEvent.setErrorMessage(errorMessage);
        loginEvent.setCurrentUserEmail(currentUserEmail);
        eventBus.post(loginEvent);
    }

    private void postEvent(int type) {
        Log.d(TAG, String.valueOf(type));
        postEvent(type, null, null);
    }

    private void postEvent(int type, String currentUserEmail) {
        Log.d(TAG, String.valueOf(type));
        postEvent(type, null, currentUserEmail);
    }
}
