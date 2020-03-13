package com.mistershorr.databases;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.lang.ref.ReferenceQueue;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String EXTRA_USERNAME = "login username";

    private TextView textViewCreateAccount;
    private Button buttonLogin;
    private EditText editTextUsername;
    private EditText editTextPassword;
    public static final int REQEUST_CREATE_ACCOUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Backendless.initApp(this, Credentials.APP_ID, Credentials.API_KEY);

        wireWidgets();
        setListeners();
    }

    private void setListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginToBackendless();
            }
        });

        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO replace create account startActivity with startAcitivtyForResult
                Intent createAccountIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                createAccountIntent.putExtra(EXTRA_USERNAME, editTextUsername.getText().toString());
                // startActivity(createAccountIntent);
                startActivityForResult(createAccountIntent, REQEUST_CREATE_ACCOUNT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQEUST_CREATE_ACCOUNT){
            // check to see what result was
            if(resultCode == RESULT_OK);{
                // use the intent from the parameter to extract username and password
                // and prefill them into the editext fields here
                editTextUsername.setText(data.getStringExtra(RegistrationActivity.EXTRA_USERNAME));
                editTextPassword.setText(data.getStringExtra(RegistrationActivity.EXTRA_PASSWORD));
            }
        }
    }

    private void loginToBackendless() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if(!username.isEmpty() && !password.isEmpty()) {


            // do not forget to call Backendless.initApp in the app initialization code

            Backendless.UserService.login(username, password, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser user) {
                    Toast.makeText(LoginActivity.this, "Welcome " + user.getProperty("username"), Toast.LENGTH_SHORT).show();
                    Intent loggedInIntent = new Intent(LoginActivity.this, FriendListActivity.class);
                    startActivity(loggedInIntent);
                    finish();

                    //change screens via intent
                    // user has been logged in
                }

                public void handleFault(BackendlessFault fault) {
                    // login failed, to get the error code call fault.getCode()
                    Toast.makeText(LoginActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            Toast.makeText(this, "Enter a username and password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void wireWidgets() {
        textViewCreateAccount = findViewById(R.id.textview_login_create_account);
        buttonLogin = findViewById(R.id.button_login_login);
        editTextUsername = findViewById(R.id.edit_text_login_username);
        editTextPassword = findViewById(R.id.edit_text_login_password);
    }
}
