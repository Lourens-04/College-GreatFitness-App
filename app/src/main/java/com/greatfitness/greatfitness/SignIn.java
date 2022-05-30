package com.greatfitness.greatfitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    //used to check if the user exist
    private FirebaseAuth mAuth;
    //edit text for user email and password
    EditText email, password;
    //button for signed up and log in
    Button signUp, logIn;
    //strings for current user email and current user password
    String emailE, passwordE;
    //declaring a progress bar
    ProgressBar logInProgress;
    //text input layout to float lables and check for errors
    TextInputLayout floatingEmailLabel, floatingPasswordLabel;

    //On create method to set buttons an text edit views to be used in this class  and checks if the one of the buttons is click
    //and takes the user to the appropriate activity also check if the user signing in exist in firebase
    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.txbEmail);
        password = findViewById(R.id.txbPassword);
        signUp = findViewById(R.id.btnSignUp);
        logIn = findViewById(R.id.btnLogIn);
        logInProgress = findViewById(R.id.prgbLogInProgress);
        logInProgress.setVisibility(View.INVISIBLE);

        setupFloatingLabelError();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logInProgress.setVisibility(View.VISIBLE);

                emailE = email.getText().toString();
                passwordE = password.getText().toString();

                if (!emailE.equals("") && !passwordE.equals("")){
                    mAuth.signInWithEmailAndPassword(emailE, passwordE ).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SignIn.this, "Log in successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignIn.this, Home.class);
                                logInProgress.setVisibility(View.INVISIBLE);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignIn.this, "Failed to log in", Toast.LENGTH_LONG).show();
                                logInProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                else{
                    logInProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignIn.this, "There are fields that are left empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //-------------------------------------------------------------------------------------------------------------------------

    //Method to enable labels on text views to float above the user text and have error checking
    //the error checking calls strings to be displayed if there is an error, I will make the error checking
    // a little bit more strict in the final POE of this app
    //----------------------------------------------------------------------------------------------------
    private void setupFloatingLabelError() {

        floatingEmailLabel = (TextInputLayout) findViewById(R.id.text_email_signin_input_layout);
        floatingEmailLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingEmailLabel.setError(getString(R.string.signin_email_required));
                    floatingEmailLabel.setErrorEnabled(true);
                } else {
                    floatingEmailLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floatingPasswordLabel = (TextInputLayout) findViewById(R.id.text_password_signin_input_layout);
        floatingPasswordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 7) {
                    floatingPasswordLabel.setError(getString(R.string.signin_password_required));
                    floatingPasswordLabel.setErrorEnabled(true);
                } else {
                    floatingPasswordLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------------
}
