package com.greatfitness.greatfitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UserDetails extends AppCompatActivity {

    //varable to be used to get current user email
    private FirebaseAuth mAuth;
    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table users activity in firebase
    DatabaseReference refToUsersTableInFireBase = database.getReference("Users");
    //declaring text fields that will be used in this class
    EditText userFirstname, userLastname, userheight, userweight, userstepGoal, userweightGoal;
    //button for submit and chhoseProfilePicture the one will upload a user details the other will let the user choose
    //their profile picture
    Button submit, chhoseProfilePicture;
    //switch to allow a user to choose the unit measurements display
    Switch userChoiceOfMeasurements;
    //image view to display the user current profile picture
    ImageView profilePicture;
    //declaring variable that set 71 to pic image request
    private final int PICK_IMAGE_REQUEST = 71;
    //variable to store a image path
    private Uri filePath;
    //Variable to store the ref to the storage in firebase
    private StorageReference mStorageRef;
    //String that will be used to save a user information to set user information from firebase and for image web links
    String userContainerKey, currentUserFirstName,currentUserLastName, currentUserHeight, currentUserWeight, currentUserStepGoal,
            currentUserWeightGoal, currentUserChoiceOfMeasurements, editProfileCheck, currentUserEmail, imageURL;
    //declaring text input layouts for floating labels and error checking
    TextInputLayout floatingFirstnameLabel, floatingLastnameLabel, floatingHeightLabel, floatingWeightLabel, floatingWeightGoalLabel, floatingStepGoalLabel;

    //Setting all the buttons and text views as well as a switch to be used in this class, then this method also gets values
    //from intends that will from intends one is to get the container key from a table in firebase and the other one
    //is to see if the user wants to edit their profile, this method also checks what buttons the user clicks
    //and seeing if the user only changed their text view values or picture as well so tah the appropriate method runs.
    //It also check what measurements displayed the user want and then changes the hints label accordingly
    //------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Bundle extras = getIntent().getExtras();

        userContainerKey = extras.getString("userContainerKey");
        editProfileCheck = extras.getString("editProfile");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser getCurentUserDetails = mAuth.getCurrentUser();
        currentUserEmail = getCurentUserDetails.getEmail();
        userFirstname = findViewById(R.id.txbFirstname);
        userLastname = findViewById(R.id.txbLastname);
        userheight = findViewById(R.id.txbUserHeight);
        userweight = findViewById(R.id.txbUserWeight);
        userstepGoal = findViewById(R.id.txbUserStepGoal);
        profilePicture = findViewById(R.id.userProfilePicture);
        userweightGoal = findViewById(R.id.txbUserWeightGoal);
        userChoiceOfMeasurements = findViewById(R.id.swhMetricOrImperial);
        chhoseProfilePicture = findViewById(R.id.btnChoose);
        submit = findViewById(R.id.btnSubmit);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        DisplayOrCaptureResults();
        checkUnitMeasurements();
        setupFloatingLabelError();

        chhoseProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        userChoiceOfMeasurements.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (userChoiceOfMeasurements.isChecked()){
                    userChoiceOfMeasurements.setText("Imperial Measurements (lb/inches)");
                    currentUserChoiceOfMeasurements = userChoiceOfMeasurements.getText().toString();
                    checkUnitMeasurements();
                } else{
                    userChoiceOfMeasurements.setText("Metric Measurements (Kg/cm)");
                    currentUserChoiceOfMeasurements = userChoiceOfMeasurements.getText().toString();
                    checkUnitMeasurements();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUserFirstName = userFirstname.getText().toString();
                currentUserLastName = userLastname.getText().toString();
                currentUserHeight = userheight.getText().toString();
                currentUserWeight = userweight.getText().toString();
                currentUserStepGoal = userstepGoal.getText().toString();
                currentUserWeightGoal = userweightGoal.getText().toString();
                currentUserChoiceOfMeasurements = userChoiceOfMeasurements.getText().toString();
                if (filePath == null){
                    UserUpload userUpload = new UserUpload(currentUserEmail, userContainerKey, currentUserFirstName, currentUserLastName,
                            currentUserWeight, currentUserHeight, currentUserStepGoal, currentUserWeightGoal, currentUserChoiceOfMeasurements, imageURL);
                    refToUsersTableInFireBase.child(userContainerKey).setValue(userUpload);
                    Intent intent = new Intent(UserDetails.this, Home.class);
                    startActivity(intent);
                    finish();
                } else {
                    UploadUserInformation();
                }
            }
        });
    }
    //------------------------------------------------------------------------------------------------------------------------

    //UploadUserInformation method is used to upload the user new information the user typed in and the new picture they choose to be their
    //profile picture but this only happens if the user chhoses a new profile picture this is also used to upload a new user data when they sign up
    //so this method to uploads the user input of their details into firebase as well as saving their profile image to a blob storage
    //then getting the link to that image and saving that with the details of the activity into firebase
    //This method also brings up a dialog box to display to the user the completion of their activity
    //it also checks for any errors the user might have made
    //---------------------------------------------------------------------------------------------------------------------------------------------
    public void UploadUserInformation(){
        if(!currentUserFirstName.equals("") && !currentUserLastName.equals("") && !currentUserHeight.equals("")
                && !currentUserWeight.equals("") && !currentUserStepGoal.equals("") && !currentUserWeightGoal.equals("")) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = mStorageRef.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UserDetails.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserDetails.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }

                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageURL = downloadUri + "";
                        UserUpload userUpload = new UserUpload(currentUserEmail, userContainerKey, currentUserFirstName, currentUserLastName,
                                currentUserWeight, currentUserHeight, currentUserStepGoal, currentUserWeightGoal, currentUserChoiceOfMeasurements, imageURL);
                        refToUsersTableInFireBase.child(userContainerKey).setValue(userUpload);
                        Intent intent = new Intent(UserDetails.this, Home.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(UserDetails.this, "There are fields that are left empty", Toast.LENGTH_LONG).show();
        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------

    //method that will change the label names based on the Measurements they want to have displayed
    //------------------------------------------------------------------------------------------------
    public void checkUnitMeasurements(){
        if(currentUserChoiceOfMeasurements != null){
            if (currentUserChoiceOfMeasurements.equals("Imperial Measurements (lb/inches)")){
                floatingWeightLabel.setHint("Weight (lb)");
                floatingHeightLabel.setHint("Height (inches)");
                floatingWeightGoalLabel.setHint("Weight (lb)");
            }else {
                floatingWeightLabel.setHint("Weight (Kg)");
                floatingHeightLabel.setHint("Height (cm)");
                floatingWeightGoalLabel.setHint("Weight (Kg)");
            }
        }
    }
    //------------------------------------------------------------------------------------------------

    //This method happens when the user is done picking their image they want to upload
    //and then converting their image to a bitmap and then setting the image to the image view in the xml
    //----------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilePicture.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    //----------------------------------------------------------------------------------------------------

    //DisplayOrCaptureResults method is to display the current user information if they had come to this page
    //from the settings fragment, so it loops trough all the users and get the current user information and
    //also sets the picture to the image view in the xml
    //---------------------------------------------------------------------------------------------------------
    public void DisplayOrCaptureResults(){
        if (editProfileCheck != null){
            refToUsersTableInFireBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot getAllUsersInUsersTableInFirebase : dataSnapshot.getChildren()) {
                        UserUpload currentUserContainer = getAllUsersInUsersTableInFirebase.getValue(UserUpload.class);

                        if (currentUserContainer.getUpPublisher().equals(currentUserEmail)) {
                            currentUserFirstName = currentUserContainer.getUpFirstname();
                            currentUserLastName = currentUserContainer.getUpLastname();
                            currentUserHeight = currentUserContainer.getUpHeight();
                            currentUserWeight = currentUserContainer.getUpWeight();
                            currentUserStepGoal = currentUserContainer.getUpUserStepGoal();
                            currentUserWeightGoal = currentUserContainer.getUpUserWeightGoal();
                            currentUserChoiceOfMeasurements = currentUserContainer.getUpMetOrImp();
                            userContainerKey = currentUserContainer.getUpContainer();
                            imageURL = currentUserContainer.getUpProfileImage();
                        }
                    }

                    userFirstname.setText ( String.valueOf ( currentUserFirstName) );
                    userLastname.setText ( String.valueOf ( currentUserLastName) );
                    userChoiceOfMeasurements.setText ( String.valueOf ( currentUserChoiceOfMeasurements) );

                    if (currentUserChoiceOfMeasurements.equals("Imperial Measurements (lb/inches)")){
                        userheight.setText ( String.valueOf ( currentUserHeight) );
                        userweight.setText ( String.valueOf ( currentUserWeight) );
                        userstepGoal.setText ( String.valueOf ( currentUserStepGoal) );
                        userweightGoal.setText ( String.valueOf ( currentUserWeightGoal) );
                        userChoiceOfMeasurements.setChecked(true);
                        checkUnitMeasurements();
                    }else {
                        userheight.setText ( String.valueOf ( currentUserHeight) );
                        userweight.setText ( String.valueOf ( currentUserWeight) );
                        userstepGoal.setText ( String.valueOf ( currentUserStepGoal) );
                        userweightGoal.setText ( String.valueOf ( currentUserWeightGoal) );
                        userChoiceOfMeasurements.setChecked(false);
                        checkUnitMeasurements();
                    }

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(imageURL)
                            .into(profilePicture).waitForLayout();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    //---------------------------------------------------------------------------------------------------------

    //Method to open the images on the user phone so they can select one
    //---------------------------------------------------------
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //---------------------------------------------------------

    //Method to enable labels on text views to float above the user text and have error checking
    //the error checking calls strings to be displayed if there is an error, I will make the error checking
    // a little bit more strict in the final POE of this app
    //----------------------------------------------------------------------------------------------------
    private void setupFloatingLabelError() {
        floatingFirstnameLabel = (TextInputLayout) findViewById(R.id.text_firstname_input_layout);
        floatingFirstnameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingFirstnameLabel.setError(getString(R.string.firstname_required));
                    floatingFirstnameLabel.setErrorEnabled(true);
                } else {
                    floatingFirstnameLabel.setErrorEnabled(false);
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

        floatingLastnameLabel = (TextInputLayout) findViewById(R.id.text_lastname_input_layout);
        floatingLastnameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingLastnameLabel.setError(getString(R.string.lastname_required));
                    floatingLastnameLabel.setErrorEnabled(true);
                } else {
                    floatingLastnameLabel.setErrorEnabled(false);
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

        floatingHeightLabel = (TextInputLayout) findViewById(R.id.text_height_input_layout);
        floatingHeightLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingHeightLabel.setError(getString(R.string.height_required));
                    floatingHeightLabel.setErrorEnabled(true);
                } else {
                    floatingHeightLabel.setErrorEnabled(false);
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

        floatingWeightLabel = (TextInputLayout) findViewById(R.id.text_weight_input_layout);
        floatingWeightLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingWeightLabel.setError(getString(R.string.weight_required));
                    floatingWeightLabel.setErrorEnabled(true);
                } else {
                    floatingWeightLabel.setErrorEnabled(false);
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


        floatingWeightGoalLabel = (TextInputLayout) findViewById(R.id.text_weightgoal_input_layout);
        floatingWeightGoalLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingWeightGoalLabel.setError(getString(R.string.weightgoal_required));
                    floatingWeightGoalLabel.setErrorEnabled(true);
                } else {
                    floatingWeightGoalLabel.setErrorEnabled(false);
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

        floatingStepGoalLabel = (TextInputLayout) findViewById(R.id.text_stepGoal_input_layout);
        floatingStepGoalLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingStepGoalLabel.setError(getString(R.string.stepgoal_required));
                    floatingStepGoalLabel.setErrorEnabled(true);
                } else {
                    floatingStepGoalLabel.setErrorEnabled(false);
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
