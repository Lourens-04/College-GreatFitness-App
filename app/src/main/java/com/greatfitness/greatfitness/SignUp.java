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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    //edit text for email an password
    EditText currentUserEmail, currentUserPassword;
    Button signUp;
    String currentUserEmailToString, currentUserPasswordToString;
    //used to save the user to firebase
    private FirebaseAuth mAuth;
    //declaring a progress bar
    ProgressBar signUpProgress;
    //getting a refrence to the table users activity in firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refToUsersTableInFireBase = database.getReference("Users");
    //text input layout to float lables and check for errors
    TextInputLayout floatingEmailSignUpLabel, floatingPasswordSignUpLabel;

    //On create method to set buttons an text edit views to be used in this class  and checks if the one of the buttons is click
    //and takes the user to the appropriate activity also saves the user to firebase with dummy data
    //-------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        currentUserEmail = findViewById(R.id.txbEmail);
        currentUserPassword = findViewById(R.id.txbPassword);
        signUp = findViewById(R.id.btnSignUp);
        signUpProgress = findViewById(R.id.pgbSignUpProgress);
        signUpProgress.setVisibility(View.INVISIBLE);

        setupFloatingLabelError();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpProgress.setVisibility(View.VISIBLE);
                mAuth = FirebaseAuth.getInstance();
                currentUserEmailToString = currentUserEmail.getText().toString();
                currentUserPasswordToString = currentUserPassword.getText().toString();

                if (!currentUserEmailToString.equals("") && !currentUserPasswordToString.equals("")){
                    mAuth.createUserWithEmailAndPassword(currentUserEmailToString, currentUserPasswordToString).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(SignUp.this, "User Saved", Toast.LENGTH_SHORT).show();
                                String userContainerKey = refToUsersTableInFireBase.child("Users").push().getKey();

                                UserUpload userUpload = new UserUpload(currentUserEmailToString, userContainerKey, "No Name", "No Last Name", "0", "0", "0", "0", "Metric Measurements (Kg/m)", "https://firebasestorage.googleapis.com/v0/b/greatfitness-34f33.appspot.com/o/images%2F3c6f2d2f-50c3-48a3-bc08-b11edb623c7c?alt=media&token=95acec51-1b16-409c-9627-4cbba45d4ec9");

                                refToUsersTableInFireBase.child(userContainerKey).setValue(userUpload);

                                Intent intent = new Intent(SignUp.this, UserDetails.class);
                                intent.putExtra("userContainerKey", userContainerKey);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignUp.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                signUpProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }else{
                    signUpProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp.this, "There are fields that are left empty", Toast.LENGTH_LONG).show();
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

        floatingEmailSignUpLabel = (TextInputLayout) findViewById(R.id.text_email_signup_input_layout);
        floatingEmailSignUpLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingEmailSignUpLabel.setError(getString(R.string.signup_email_required));
                    floatingEmailSignUpLabel.setErrorEnabled(true);
                } else {
                    floatingEmailSignUpLabel.setErrorEnabled(false);
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

        floatingPasswordSignUpLabel = (TextInputLayout) findViewById(R.id.text_password_signup_input_layout);
        floatingPasswordSignUpLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 7) {
                    floatingPasswordSignUpLabel.setError(getString(R.string.signup_password_required));
                    floatingPasswordSignUpLabel.setErrorEnabled(true);
                } else {
                    floatingPasswordSignUpLabel.setErrorEnabled(false);
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
