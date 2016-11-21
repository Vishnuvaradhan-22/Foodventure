package com.vish.foodventure.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vish.foodventure.R;
import com.vish.foodventure.utility.NetworkManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaunchScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;
    private EditText emailId;
    private EditText password;
    private TextView errorMessage;

    private Button loginButton;
    private Button createAccount;

    private Pattern pattern;
    private Matcher matcher;

    private final String email_patters = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        initializeUi();
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth !=null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void initializeUi(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        pattern = Pattern.compile(email_patters);

        emailId = (EditText)findViewById(R.id.emailId);
        password = (EditText)findViewById(R.id.password);
        errorMessage = (TextView)findViewById(R.id.errorMessage);

        loginButton = (Button)findViewById(R.id.loginButton);
        createAccount = (Button)findViewById(R.id.createAccount);
        NetworkManager networkManager = new NetworkManager(this);
        boolean connectionResult = networkManager.testConnection();
        if(!connectionResult){
            errorMessage.setVisibility(View.VISIBLE);
            emailId.setVisibility(View.INVISIBLE);
            password.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            createAccount.setVisibility(View.INVISIBLE);
        }
        else {
            if(emailId.getVisibility() == View.INVISIBLE)
                emailId.setVisibility(View.VISIBLE);
            if(password.getVisibility()==View.INVISIBLE)
                password.setVisibility(View.VISIBLE);
            if (loginButton.getVisibility() == View.INVISIBLE)
                loginButton.setVisibility(View.VISIBLE);
            if(createAccount.getVisibility() == View.INVISIBLE)
                createAccount.setVisibility(View.VISIBLE);
            if(errorMessage.getVisibility() == View.VISIBLE)
                errorMessage.setVisibility(View.GONE);
            loginButton.setOnClickListener(loginListener);
            createAccount.setOnClickListener(createAccountListener);

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("UserEmail",user.getEmail());
                        intent.setClass(getApplicationContext(),HomeScreen.class);
                        startActivity(intent);
                    }
                }
            };
        }

    }

    private boolean validateData(){
        String email = emailId.getText().toString();

        matcher = pattern.matcher(email);
        if(!matcher.matches()){
            emailId.setError("Please enter valid email");
            return false;
        }

        return true;
    }

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginUser();
        }
    };

    private View.OnClickListener createAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            createNewAccount();
        }
    };


    private void loginUser(){

        progressDialog = ProgressDialog.show(this, "FoodVenture",
                "Login", true);
        if(emailId.getText().toString().length() == 0){
            emailId.setError("Please enter valid email");
            progressDialog.dismiss();
        }
        else if(password.getText().toString().length() == 0) {
            password.setError("Please enter valid password");
            progressDialog.dismiss();
        }
        else if(validateData()) {
            mAuth.signInWithEmailAndPassword(emailId.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Exception exception = task.getException();
                                progressDialog.dismiss();
                                switch (exception.getClass().getSimpleName()) {
                                    case "FirebaseAuthInvalidUserException":
                                        Toast.makeText(LaunchScreenActivity.this, "Please register with your email", Toast.LENGTH_LONG).show();
                                        break;
                                    case "FirebaseAuthInvalidCredentialsException":
                                        password.setError("Incorrect password!");
                                        break;
                                }
                            } else {
                                progressDialog.dismiss();
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                //bundle.putString("UserEmail",user.getEmail());
                                intent.setClass(getApplicationContext(), HomeScreen.class);
                                startActivity(intent);
                            }

                        }
                    });
        }
        else
            progressDialog.dismiss();
    }

    private void createNewAccount(){
        progressDialog = ProgressDialog.show(this, "FoodVenture",
                "Creating Account", true);
        if(emailId.getText().toString().length() == 0){
            emailId.setError("Please enter valid email");
            progressDialog.dismiss();
        }
        else if(password.getText().toString().length()==0){
            password.setError("Please enter valid password");
            progressDialog.dismiss();
        }
        else if(validateData()) {
            mAuth.createUserWithEmailAndPassword(emailId.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Exception exception = task.getException();
                                if (exception.getClass().getSimpleName().equals("FirebaseAuthUserCollisionException")) {
                                    Toast.makeText(LaunchScreenActivity.this, "User already registered! Please Signin",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(LaunchScreenActivity.this, "Successfully registered! Please Signin",
                                        Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
        else
            progressDialog.dismiss();
    }

}