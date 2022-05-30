package com.greatfitness.greatfitness;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    //setting a variable to set the activity to get the activity
    private Activity mActivity;
    //setting a variable for sensor manager for step counter
    SensorManager sensorManager;
    //setting sensor variable for step counter
    Sensor sensor;
    //declaring the text views to display a user steps and th current user loged in
    TextView steps, user;
    //boolean to see if the user is moving to count steps
    boolean running = false;
    //integer to hold the user current steps
    int count;
    //declaring the strings to store today date the date in firebabase (previousDate), the container key in firebase(upKey),
    //the user email and the steps that are in firebase (stepsInMemory)
    String todayDate, previousDate, userProfileInFireBase,upKey, userEmail, stepsInMemory;
    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table users and users steps in firebase
    DatabaseReference upUserSteps = database.getReference("UsersSteps");
    DatabaseReference getRefrenceToAllUsersInFirebase = database.getReference("Users");
    //button to take the user to create an activity
    Button createActivity;
    //variable to get the current user email
    private FirebaseAuth mAuth;
    Boolean userExsits = true;
    //image view to display the current user profile
    ImageView userProfile;

    public HomeFragment() {
        // Required empty public constructor
    }

    //On create view method to set the button, image view and text views so that it can be filled with values to be displayed to the user
    //it also gets today date and gets the current user that is loged in email
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        steps = rootView.findViewById(R.id.txbSteps);
        user = rootView.findViewById(R.id.txbUser);
        userProfile = rootView.findViewById(R.id.userProfileImage);
        createActivity = rootView.findViewById(R.id.btnCreateActivity);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userE = mAuth.getCurrentUser();
        userEmail = userE.getEmail();
        user.setText(userEmail);
        return rootView;
    }
    //----------------------------------------------------------------------------------------------

    //On Activity created method to go trough the user steps table in firebase to set global variables
    //in ths class for example the container key, the date stored in firebase and the steps the user have
    //taken since the last time they used the app
    //then there are if statements to check if today date is equel to previousDate to see if the step
    //couter should be converted to zero or not and to create a new container for a new user that signed up to the app
    //then we go through all the users in the users table to get the current user profile picture and set that to an image view
    //I am also using a glide import to set the image to the view
    //-----------------------------------------------------------------------------------------------------------------
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        upUserSteps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSteps : dataSnapshot.getChildren()) {
                    UserStepsUpload uSU = userSteps.getValue(UserStepsUpload.class);

                    if (uSU.getUpPublisher().equals(userEmail)) {
                        upKey = uSU.getUpContainer();
                        previousDate = uSU.getUpDate();
                        stepsInMemory = uSU.getUpUserSteps();
                        userExsits = true;
                        break;
                    } else {
                        userExsits = false;
                    }
                }

                if (userExsits != true){
                    String key = upUserSteps.child("UsersSteps").push().getKey();
                    String steps = "" + count;
                    UserStepsUpload userUpload = new UserStepsUpload(userEmail, key, steps, todayDate);
                    upKey = key;
                    previousDate = todayDate;
                    stepsInMemory = steps;
                    upUserSteps.child(key).setValue(userUpload);
                    userExsits = true;
                }

                if (todayDate.equals(previousDate)){
                    count = Integer.parseInt(stepsInMemory);
                    steps.setText (String.valueOf (count));

                } else {
                    count = 1;
                    String steps = "" + count;
                    UserStepsUpload userUpload = new UserStepsUpload(userEmail, upKey, steps, todayDate);
                    upUserSteps.child(upKey).setValue(userUpload);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        getRefrenceToAllUsersInFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSteps : dataSnapshot.getChildren()) {
                    UserUpload UserContainerInFirebase = userSteps.getValue(UserUpload.class);

                    if (UserContainerInFirebase.getUpPublisher().equals(userEmail)) {
                        userProfileInFireBase = UserContainerInFirebase.getUpProfileImage();
                    }
                }

                if (getActivity() != null){
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(userProfileInFireBase)
                            .into(userProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------------------

    //on resume method checks if the user has started to move again to cahnge the running variable back to true
    //to start counting steps again it also uses the type step counter for the sensor
    //---------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume ();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor ( sensor.TYPE_STEP_COUNTER );
        if(countSensor!= null){
            sensorManager.registerListener ( this,countSensor,SensorManager.SENSOR_DELAY_UI );
        }else {
        }
    }
    //---------------------------------------------------------------------------------------

    //On pause method checks that the user stoped moving and changing the running variable back to false
    //--------------------------------------------------------------
    @Override
    public void onPause() {
        super.onPause ();
        running = false;
    }
    //--------------------------------------------------------------

    //On sensor chaged method is to count the steps the user have taken and upload it to firebase
    //---------------------------------------------------------------------------------
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running){
            steps.setText ( String.valueOf ( count++ ) );
            if (upKey != null){
                String steps = "" + count;
                UserStepsUpload userUpload = new UserStepsUpload(userEmail, upKey, steps, todayDate);
                upUserSteps.child(upKey).setValue(userUpload);
            }
        }
    }
    //---------------------------------------------------------------------------------

    //onAccuracyChanged method to make the sensor more acurate this will be improved on in final POE
    //--------------------------------------------------
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    //--------------------------------------------------

}
