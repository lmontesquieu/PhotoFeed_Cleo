package net.cleonet.cleo.photofeed_galileo.domain;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import net.cleonet.cleo.photofeed_galileo.entities.Photo;
import net.cleonet.cleo.photofeed_galileo.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pepe on 9/7/17.
 */

public class FirebaseAPI {
    //private Firebase firebase;
    private ChildEventListener photosEventListener;

    private static final String TAG = "FirebaseAPI";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final static String SEPARATOR = "___";
    private final static String CHATS_PATH = "chats";
    private final static String USERS_PATH = "users";
    private final static String CONTACTS_PATH = "contacts";

    //private static class SingletonHolder {
    //    private static final FirebaseAPI INSTANCE = new FirebaseAPI();
    //}

    //public static FirebaseAPI getInstance() {
    //    Log.d(TAG, "getInstance");
        //return SingletonHolder.INSTANCE;
    //}

    public FirebaseAPI(FirebaseAuth mAuth, DatabaseReference mDatabase) {
        Log.d(TAG,"Constructor");
        this.mAuth = mAuth;
        this.mDatabase = mDatabase;
        //this.mDatabase = FirebaseDatabase.getInstance().getReference();
        //this.mAuth = FirebaseAuth.getInstance();

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public DatabaseReference getDataReference() {
        return mDatabase;
    }

    public FirebaseAuth getMyAuth() {
        return mAuth;
    }

    public FirebaseAuth.AuthStateListener getMyAuthListener() {
        return mAuthListener;
    }

    public void checkForData(final FirebaseActionListenerCallback listenerCallback) {
        Log.d(TAG,"checkForData");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    listenerCallback.onSuccess();
                } else {
                    listenerCallback.onDatabaseError(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listenerCallback.onDatabaseError(databaseError);
                //postError();
            }
        });
    }

    public void subscribe(final FirebaseEventListenerCallback eventListenerCallback) {
        Log.d(TAG,"subscribe");
        if (photosEventListener == null) {
            Log.d(TAG,"photosEventListener == null");
            photosEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    eventListenerCallback.onChildAdded(dataSnapshot);
                    Log.d(TAG, "onChildAdded");
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged");
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    eventListenerCallback.onChildRemoved(dataSnapshot);
                    Log.d(TAG, "onChildRemoved");
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled");
                    eventListenerCallback.onCancelled(databaseError);
                    //postError();
                }
            };
            Log.d(TAG, "Adding ChildEventListener");
            mDatabase.addChildEventListener(photosEventListener);
        }
    }

    public void unsubscribe() {
        if(photosEventListener != null){
            mDatabase.removeEventListener(photosEventListener);
        }
    }

    public String create(){
        return mDatabase.push().getKey();
    }

    public void update(Photo photo){
        this.mDatabase.child(photo.getId()).setValue(photo);
    }

    public void remove(Photo photo, FirebaseActionListenerCallback listenerCallback) {
        this.mDatabase.child(photo.getId()).removeValue();
        listenerCallback.onSuccess();
    }

    public String getAuthUserEmail() {
        //Log.d(TAG, "getAuthUserEmail");
        String email = null;
        //FirebaseUser authUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser authUser = mAuth.getCurrentUser();
        if (authUser != null) {
            email = authUser.getEmail();
        }
        Log.d(TAG, email);
        return email;
    }

    public void logout(){
        mAuth.signOut();
    }

    public void login(String email, String password, final FirebaseActionListenerCallback listenerCallback) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            //initSignIn();
                            listenerCallback.onSuccess();
                        } else {
                            Exception exception = task.getException();
                            listenerCallback.onError(exception);
                        }
                    }
                });
    }

//    private void initSignIn() {
//        //mDatabase = helper.getDataReference();
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                /*User currentUser = dataSnapshot.getValue(User.class);*/
//                //if (currentUser == null) {
//                //    registerNewUser();
//                //}
//                /*helper.changeUserConnectionStatus(User.ONLINE);
//                postEvent(LoginEvent.onSignInSuccess);*/
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        });
//    }

    public void signup(String email, String password, final FirebaseActionListenerCallback listenerCallback) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signUp:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            listenerCallback.onSuccess();
                        } else {
                            Exception exception = task.getException();
                            listenerCallback.onError(exception);
                        }
                    }
                });
    }

    /*private void registerNewUser() {
        String email = helper.getAuthUserEmail();
        if (email != null) {
            User currentUser = new User();
            currentUser.setEmail(email);
            mDatabase.setValue(currentUser);
        }
    }*/

    public void checkSession(FirebaseActionListenerCallback listenerCallback) {
        if (mAuth.getCurrentUser() != null) {
            //initSignIn();
            listenerCallback.onSuccess();
        } else {
            //postEvent(LoginEvent.onFailedToRecoverSession);
            listenerCallback.onError(null);
        }
    }

    public DatabaseReference getUserReference(String email) {
        //Log.d(TAG, "getUserReference");
        DatabaseReference userReference = null;
        if (email != null) {
            String emailKey = email.replace(".","_");
            userReference = mDatabase.getRoot().child(USERS_PATH).child(emailKey);
        }
        return userReference;
    }

    public DatabaseReference getMyUserReference() {
        //Log.d(TAG, "getMyUserReference");
        return getUserReference(getAuthUserEmail());
    }

    public void changeUserConnectionStatus(boolean online) {
        //Log.d(TAG, "changeUserConnectionStatus");
        if (getMyUserReference() != null) {
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("online", online);
            getMyUserReference().updateChildren(updates);
            notifyContactsOfConnectionChange(online);
        }
    }
    public void notifyContactsOfConnectionChange(boolean online) {
        notifyContactsOfConnectionChange(online, false);
    }

    public void signOff() {
        //Log.d(TAG, "signOff");
        notifyContactsOfConnectionChange(User.OFFLINE, true);
    }

    private void notifyContactsOfConnectionChange(final boolean online, final boolean signOff) {
        //Log.d(TAG, "notifyContactsOfConnectionChange");
        final String myEmail = getAuthUserEmail();
        getMyContactsReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String email = child.getKey();
                    DatabaseReference reference = getOneContactReference(email, myEmail);
                    reference.setValue(online);
                    Log.d("onDataChange",email);
                    Log.d("status:",String.valueOf(online));
                }
                if (signOff) {
                    //Log.d(TAG, "signOut");
                    //FirebaseAuth.getInstance().signOut();
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.d(TAG, "firebaseError");
            }
        });
    }

    public DatabaseReference getContactsReference(String email) {
        //Log.d(TAG, "getContactsReference");
        return getUserReference(email).child(CONTACTS_PATH);
    }

    public DatabaseReference getMyContactsReference() {
        //Log.d(TAG, "getMyContactsReference");
        return getContactsReference(getAuthUserEmail());
    }

    public DatabaseReference getOneContactReference(String mainEmail, String childEmail) {
        //Log.d(TAG, "getOneContactReference");
        String childKey = childEmail.replace(".","_");
        return getUserReference(mainEmail).child(CONTACTS_PATH).child(childKey);
    }

    private void postSuccess() {
        post(false);
    }

    private void postError() {
        post(true);
    }

    private void post(boolean error) {
        //AddContactEvent event = new AddContactEvent();
        //event.setError(error);
        //eventBus.post(event);
    }
}
