package net.cleonet.cleo.photofeed_galileo.login.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.cleonet.cleo.photofeed_galileo.main.ui.MainActivity;
import net.cleonet.cleo.photofeed_galileo.PhotoFeedApp;
import net.cleonet.cleo.photofeed_galileo.R;
import net.cleonet.cleo.photofeed_galileo.login.LoginPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;
    @BindView(R.id.editTxtEmail)
    EditText inputEmail;
    @BindView(R.id.editTxtPassword)
    EditText inputPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.layoutMainContainer)
    RelativeLayout container;

    private PhotoFeedApp app;
    @Inject
    LoginPresenter loginPresenter;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Log.d(TAG,"onCreate");

        app = (PhotoFeedApp)getApplication();
        setupInjection();
        loginPresenter.onCreate();
        loginPresenter.validateLogin(null, null);
    }

    private void setupInjection() {
        app.getLoginComponent(this).inject(this);
    }

    @Override
    protected void onDestroy() {
        loginPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void enableInputs() {
        setInputs(true);
    }

    @Override
    public void disableInputs() {
        setInputs(false);

    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnSignIn)
    @Override
    public void handleSignIn(){
        loginPresenter.validateLogin(inputEmail.getText().toString(),
                inputPassword.getText().toString());
        Log.d("AndroidChat",inputEmail.getText().toString());
    }

    @OnClick(R.id.btnSignUp)
    @Override
    public void handleSignUp(){
        loginPresenter.registerNewUser(inputEmail.getText().toString(),
                                            inputPassword.getText().toString());
    }

    @Override
    public void navigateToMainScreen() {
        Log.d(TAG,"navigateToMainScreen");
        //**Aquí debería ser, según tutorial, MainActivity.class
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void loginError(String error) {
        inputPassword.setText("");
        String msgError = String.format(getString(R.string.login_error_message_signin),error);
        inputPassword.setError(msgError);
    }

    @Override
    public void newUserSuccess() {
        Snackbar.make(container,R.string.login_notice_message_signup, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void newUserError(String error) {
        inputPassword.setText("");
        String msgError = String.format(getString(R.string.login_error_message_signup),error);
        inputPassword.setError(msgError);
    }

    @Override
    public void setUserEmail(String email) {
        if (email != null){
            sharedPreferences.edit().putString(app.getEmailKey(), email).commit();
        }

    }

    private void setInputs(boolean enabled) {
        inputEmail.setEnabled(enabled);
        inputPassword.setEnabled(enabled);
        btnSignIn.setEnabled(enabled);
        btnSignUp.setEnabled(enabled);
    }
}
