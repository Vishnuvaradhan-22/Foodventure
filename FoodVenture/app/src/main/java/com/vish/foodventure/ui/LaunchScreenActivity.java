package com.vish.foodventure.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vish.foodventure.R;

public class LaunchScreenActivity extends MenuLoader {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        initializeUi();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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
        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button createAccount = (Button)findViewById(R.id.createAccount);

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
        EditText emailId = (EditText)findViewById(R.id.emailId);
        final EditText password = (EditText)findViewById(R.id.password);

        progressDialog = ProgressDialog.show(this, "FoodVenture",
                "Login", true);


        mAuth.signInWithEmailAndPassword(emailId.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Exception exception = task.getException();
                            progressDialog.dismiss();
                            switch (exception.getClass().getSimpleName()){
                                case "FirebaseAuthInvalidUserException":
                                    Toast.makeText(LaunchScreenActivity.this,"Please register with your email",Toast.LENGTH_LONG).show();
                                    break;
                                case "FirebaseAuthInvalidCredentialsException":
                                    password.setError("Incorrect password!");
                                    break;
                            }
                        }
                        else {
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

    private void createNewAccount(){
        EditText emailId = (EditText)findViewById(R.id.emailId);
        EditText password = (EditText)findViewById(R.id.password);
        progressDialog = ProgressDialog.show(this, "FoodVenture",
                "Creating Account", true);

        mAuth.createUserWithEmailAndPassword(emailId.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Exception exception = task.getException();
                            if(exception.getClass().getSimpleName().equals("FirebaseAuthUserCollisionException")){
                                Toast.makeText(LaunchScreenActivity.this, "User already registered! Please Signin",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            mAuth.signOut();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

}
