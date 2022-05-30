package com.greatfitness.greatfitness;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProgressFragment extends Fragment {

    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table users and users steps in firebase
    DatabaseReference currentUserStepsRefrence = database.getReference("UsersSteps");
    DatabaseReference currentUserUeserDetailRefrence = database.getReference("Users");
    //setting a variable to set the activity to get the activity
    private Activity mActivity;
    //variable to get the current user email
    private FirebaseAuth mAuth;
    //setting textviews to be used in this class
    TextView userWeight, userWeightGoal, userStepsTaken, userStepGoal, infoStepGoal, infoWeightGoal;
    //declaring strings that will be used to hold values globally in this class
    String currentUserEmail, curentUserSteps, curentUserStepGoal, curentUserWeight, curentUserWeightGoal, currentUserMesurements;
    //declaring the scroll view
    ScrollView scrollViewMyProgress;

    public MyProgressFragment() {
        // Required empty public constructor
    }


    //On create view method to set the text views so that it can be filled with values to be displayed to the user
    //---------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_progress, container, false);
        scrollViewMyProgress = rootView.findViewById(R.id.scrollViewMyProgress);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser getCurentUserDetails = mAuth.getCurrentUser();
        currentUserEmail = getCurentUserDetails.getEmail();
        userWeight = rootView.findViewById(R.id.txbUserCurrentWeight);
        userWeightGoal = rootView.findViewById(R.id.txbUserWeightGoal);
        userStepsTaken = rootView.findViewById(R.id.txbUserStepsTaken);
        userStepGoal = rootView.findViewById(R.id.txbUserStepGoal);
        infoStepGoal = rootView.findViewById(R.id.txbInfoStepGoal);
        infoWeightGoal = rootView.findViewById(R.id.txbInfoWeightGoal);


        return rootView;
    }
    //---------------------------------------------------------------------------------------------------


    //On activity created to poulate the text fields with the appropriate data
    //---------------------------------------------------------------------------------
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        currentUserStepsRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot getAllUsersStepsInRefrence : dataSnapshot.getChildren()) {
                    UserStepsUpload currentUserStepsContainer = getAllUsersStepsInRefrence.getValue(UserStepsUpload.class);

                    if (currentUserStepsContainer.getUpPublisher().equals(currentUserEmail)) {
                        curentUserSteps = currentUserStepsContainer.getUpUserSteps();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        currentUserUeserDetailRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot getAllUserDetailsInRefrence : dataSnapshot.getChildren()) {
                    UserUpload currentUserDetailsContainer = getAllUserDetailsInRefrence.getValue(UserUpload.class);

                    if (currentUserDetailsContainer.getUpPublisher().equals(currentUserEmail)) {
                        curentUserWeight = currentUserDetailsContainer.getUpWeight();
                        curentUserWeightGoal = currentUserDetailsContainer.getUpUserWeightGoal();
                        curentUserStepGoal = currentUserDetailsContainer.getUpUserStepGoal();
                        currentUserMesurements = currentUserDetailsContainer.getUpMetOrImp();
                    }
                }

                userStepsTaken.setText ( String.valueOf ("Steps : " + curentUserSteps));
                userStepGoal.setText ( String.valueOf ("Target Steps : " +  curentUserStepGoal));

                if ( currentUserMesurements.equals("Metric Measurements (Kg/cm)")){
                    userWeight.setText ( String.valueOf ("Current Weight : " + curentUserWeight + "  Kg") );
                    userWeightGoal.setText ( String.valueOf ("Target Weight : " +  curentUserWeightGoal + "  Kg" ) );
                } else {
                    userWeight.setText ( String.valueOf ("Current Weight : " + curentUserWeight + "  lb") );
                    userWeightGoal.setText ( String.valueOf ("Target Weight : " + curentUserWeightGoal + "  lb" ) );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //---------------------------------------------------------------------------------
}
