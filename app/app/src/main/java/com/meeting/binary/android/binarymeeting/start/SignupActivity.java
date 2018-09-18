package com.meeting.binary.android.binarymeeting.start;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.GenericResponse;
import com.meeting.binary.android.binarymeeting.model.UserDto;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name)
    EditText _nameText;
//    @BindView(R.id.input_address) EditText _addressText;
    @BindView(R.id.input_email) EditText _emailText;
//    @BindView(R.id.input_mobile) EditText _mobileText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
//                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }



    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        requestSignup(name,email,password,reEnterPassword);
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        //onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length()!=10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    /**
     * check the credentials provided by the user and
     * grant or not the access to the application
     */
    private void requestSignup(String username, String email, String password,String matchingPassword){
        RequestWebServiceInterface requestWebServiceInterface = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
//        Map<String,String> user = new HashMap<>();
        UserDto user  = new UserDto();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setMatchingPassword(matchingPassword);
        Call<GenericResponse> call = requestWebServiceInterface.register(user);
        Log.d(TAG, "requestSignup: " + "0000000000000000");
        call.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Log.i(TAG, "requestSignup: " + "1111111111111111");
//                Message message = new Message();
                if (response.body().getMessage().equals("success")){
//                    message.what = REGISTER_SUCCESS;
//                    handler.sendMessage(message);
                    Log.i(TAG, "requestSignup: " + "22222222222222222222");
                    Toast.makeText(SignupActivity.this, "onResponse isSuccessful:" +
                            " response from sign up", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = LoginActivity.newIntent(getApplicationContext());
                            startActivity(intent);
                        }
                    }, 1);
                    Log.i(TAG, "onResponse isSuccessful: " + " response from login");
                } else {
//                    message.what = REGISTER_FAIL;
//                    handler.sendMessage(message);
                    Toast.makeText(SignupActivity.this, "onResponse is not successful: " + " response from sign up", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse is not successful: " + " response from login");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "onFailure: " +
                        t.getMessage() + " response from failure", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.input_name:
            case R.id.input_email:
            case R.id.input_password:
            case R.id.input_reEnterPassword:
                Log.d(TAG, "onClick: "+"hfhgfghfhg");
                _emailText.setFocusable(true);
                _emailText.setFocusableInTouchMode(true);
                _nameText.setFocusable(true);
                _nameText.setFocusableInTouchMode(true);
                _passwordText.setFocusable(true);
                _passwordText.setFocusableInTouchMode(true);
                _reEnterPasswordText.setFocusable(true);
                _reEnterPasswordText.setFocusableInTouchMode(true);
                break;
        }
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, SignupActivity.class);
        return intent;
    }
}
