package com.meeting.binary.android.binarymeeting.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.LoginServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.NetworkHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //check for internet connection on the device
    boolean mIsConnected;

    private EditText mUserLoginEditText;
    private EditText mUserLoginPasswordEditText;
    private Button mLoginButton;
    private TextView mSignUpTextVew;
    private TextView mForgotPasswordTextVew;

    private static final String TAG = "LoginActivity";



    /**
     * intent method to lunch the activity
     * @param context
     * @return
     */
    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * get the reference ids
         */
        mUserLoginEditText = (EditText)findViewById(R.id.username);
        mUserLoginPasswordEditText = (EditText)findViewById(R.id.userpassword);
        mLoginButton = (Button)findViewById(R.id.login);
        mSignUpTextVew = (TextView)findViewById(R.id.register);
        mForgotPasswordTextVew = (TextView)findViewById(R.id.forgot);

        /**
         * remove the add cookie sharedpreferences
         */
//        SharedPreferences sharedPreferences = getApplicationContext()
//                .getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
//        sharedPreferences.edit().remove(CookiePreferences.getPrefCookies()).apply();
//        String preferences = PreferenceManager.getDefaultSharedPreferences(this)
//                .getString(AddCookiesInterceptor.PREF_COOKIES, null);
//        if (preferences != null){
//            Toast.makeText(this, "preferences not deleted : " + preferences, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "preferences deleted", Toast.LENGTH_SHORT).show();
//        }


        //check network connection
        mIsConnected = NetworkHelper.hasNetworkAccess(this);

        //sample check of internet connection
        if (!mIsConnected){
            Toast.makeText(this, "no internet connection", Toast.LENGTH_SHORT).show();
        }

        mLoginButton.setOnClickListener(this);
        mSignUpTextVew.setOnClickListener(this);
        mForgotPasswordTextVew.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.login: {
//                Intent intent = HomeActivity.newIntent(getApplicationContext());
//                startActivity(intent);
                String email = mUserLoginEditText.getText().toString();
                String password = mUserLoginPasswordEditText.getText().toString();
                if (mIsConnected){
                    Toast.makeText(this, "onClick: network available", Toast.LENGTH_LONG).show();
                    if (checkUserField(email, password)){
                        requestLogin(email, password);
                    }
                    else {
                        mUserLoginEditText.setError("bad credential");
                        mUserLoginEditText.setTextColor(getResources().getColor(R.color.green));
                    }
                } else {
                    Toast.makeText(this, "Check your network connection: network not available", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onClick: network not available");
                }
                break;
            }

            case R.id.register: {
                Intent intent = SignupActivity.newIntent(this);
                startActivity(intent);
                break;
            }

            case R.id.forgot: {

                break;
            }
        }
    }



    private boolean checkUserField(String email, String password){
        if (email!=null && password!=null && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password) || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }



    /**
     * check the credentials provided by the user and
     * grant or not the access to the application
     */
    private void requestLogin(String email, String password){

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Login...");
        progressDialog.show();


        RequestWebServiceInterface requestWebServiceInterface = LoginServiceGenerator.createService(RequestWebServiceInterface.class, email, password, getApplicationContext());
        Call<Void> call = requestWebServiceInterface.login();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "onResponse isSuccessful: " +
                            response.message() + " response from login", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    CookiePreferences.setStoredName(LoginActivity.this, email);/**store the name*/
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = HomeActivity.newIntent(getApplicationContext());
                            startActivity(intent);
                        }
                    }, 1);
                    Log.i(TAG, "onResponse isSuccessful: " + response.message() + " response from login");
                } else {
                    Toast.makeText(LoginActivity.this, "onResponse is not successful: " +
                        response.message() + " response from login", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                Log.i(TAG, "onResponse is not successful: " + response.message() + " response from login");
            }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Toast.makeText(LoginActivity.this, "onFailure: " +
                    t.getMessage() + " response from failure", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


}


