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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddActivity extends AppCompatActivity {

    //declaring variables that will be used to hold values, or to set buttons/ text views or a ref to firebase
    //declaring text fields for an activity name, the activity a user is doing, calories and duration
    EditText activityName, activity, caloriesBurned, duration;
    //buttons that will be used to get a picture from a user and to save a user entered information
    Button upload, chooseImage;
    //Strings to hold values that will be used to push to fire base
    String activityNameToString, activityToString, caloriesBurnedToString, durationToString;
    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table users activity in firebase
    DatabaseReference firebaseRefForUsersActivities = database.getReference("UsersActivities");
    //varable to be used to get current user email
    private FirebaseAuth mAuth;
    //string values that will be used to hold user email, today's date and the image url
    String userEmail, todayDate, imageURL;
    //declaring an image view to show the user what image they choose
    private ImageView imageView;
    //variable to store a image path
    private Uri filePath;
    //declaring variable that set 71 to pic image request
    private final int PICK_IMAGE_REQUEST = 71;
    //Variable to store the ref to the storage in firebase
    private StorageReference mStorageRef;
    //declaring text input layouts for floating labels and error checking
    TextInputLayout floatingActivityNameLabel, floatingActivityLabel, floatingDurationLabel, floatingCaloriesBurnedLabel;

    //On create method to set buttons in xml to buttons in the class and listening to buttons getting clicked
    //also gets today date then
    //---------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        activityName = findViewById(R.id.txbActivityName);
        activity = findViewById(R.id.txbActivity);
        caloriesBurned = findViewById(R.id.txbCaloriesBurned);
        duration = findViewById(R.id.txbDuration);

        chooseImage = (Button) findViewById(R.id.btnChoose);
        imageView = (ImageView) findViewById(R.id.imgView);

        upload = findViewById(R.id.btnUpload);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser curentUserLogedIn = mAuth.getCurrentUser();
        userEmail = curentUserLogedIn.getEmail();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        setupFloatingLabelError();

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadActivity();
            }
        });
    }
    //---------------------------------------------------------------------

    //Method to open the images on the user phone so they can select one
    //---------------------------------------------------------
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    //---------------------------------------------------------

    //Method to upload the user input of their activity into firebase as well as saving the image to a blob storage
    //then getting the link to that image and saving that with the details of the activity into firebase
    //This method also brings up a dialog box to display to the user the completion of their activity
    //it also checks for any errors the user might have made
    //--------------------------------------------------------------------------------------------------
    private void UploadActivity() {

        activityNameToString = activityName.getText().toString();
        activityToString = activity.getText().toString();
        caloriesBurnedToString = caloriesBurned.getText().toString();
        durationToString = duration.getText().toString();

        if(filePath != null && !activityNameToString.equals("") && !activityToString.equals("")
                && !caloriesBurnedToString.equals("") && !durationToString.equals(""))
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = mStorageRef.child("images/"+ UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
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
                        String activityFirebaseKey = firebaseRefForUsersActivities.child("UsersActivities").push().getKey();
                        UserActivityUpload userActivityUpload = new UserActivityUpload(userEmail, activityFirebaseKey, activityNameToString.trim(), activityToString.trim(), caloriesBurnedToString.trim(), durationToString.trim(), todayDate, imageURL);
                        firebaseRefForUsersActivities.child(activityFirebaseKey).setValue(userActivityUpload);
                        Intent intent = new Intent(AddActivity.this, Home.class);
                        startActivity(intent);
                    } else {
                    }
                }
            });
        }
        else{
            Toast.makeText(AddActivity.this, "There are fields that are left empty", Toast.LENGTH_LONG).show();
        }
    }
    //--------------------------------------------------------------------------------------------------

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
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    //----------------------------------------------------------------------------------------------------

    //Method to enable labels on text views to float above the user text and have error checking
    //the error checking calls strings to be displayed if there is an error, I will make the error checking
    // a little bit more strict in the final POE of this app
    //It is for activity name, activity, calories burned and duration.
    //----------------------------------------------------------------------------------------------------
    private void setupFloatingLabelError() {
        floatingActivityNameLabel = (TextInputLayout) findViewById(R.id.text_activity_name_input_layout);
        floatingActivityNameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingActivityNameLabel.setError(getString(R.string.activityname_required));
                    floatingActivityNameLabel.setErrorEnabled(true);
                } else {
                    floatingActivityNameLabel.setErrorEnabled(false);
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

        floatingActivityLabel = (TextInputLayout) findViewById(R.id.text_activity_input_layout);
        floatingActivityLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingActivityLabel.setError(getString(R.string.activity_required));
                    floatingActivityLabel.setErrorEnabled(true);
                } else {
                    floatingActivityLabel.setErrorEnabled(false);
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

        floatingDurationLabel = (TextInputLayout) findViewById(R.id.text_duration_input_layout);
        floatingDurationLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingDurationLabel.setError(getString(R.string.duration_required));
                    floatingDurationLabel.setErrorEnabled(true);
                } else {
                    floatingDurationLabel.setErrorEnabled(false);
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

        floatingCaloriesBurnedLabel = (TextInputLayout) findViewById(R.id.text_caloriesburned_input_layout);
        floatingCaloriesBurnedLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 1) {
                    floatingCaloriesBurnedLabel.setError(getString(R.string.caloriesburned_required));
                    floatingCaloriesBurnedLabel.setErrorEnabled(true);
                } else {
                    floatingCaloriesBurnedLabel.setErrorEnabled(false);
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
