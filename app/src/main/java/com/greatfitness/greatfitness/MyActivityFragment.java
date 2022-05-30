package com.greatfitness.greatfitness;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyActivityFragment extends Fragment {

    //setting a variable to set the activity to get the activity
    private Activity mActivity;
    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table Users Activitiesin in firebase
    DatabaseReference firebaseRefForUsersActivities = database.getReference("UsersActivities");
    private FirebaseAuth mAuth;
    //varble to be used to get current user email
    String userEmail;
    //array to store all the image url that are puled from firebase
    private ArrayList<String> imageURL = new ArrayList<>();
    //array to store all the activities names that are puled from firebase
    private ArrayList<String> activityname = new ArrayList<>();
    //array to store all the other info that are puled from firebase
    private ArrayList<String> activityInfo = new ArrayList<>();
    //array to store all the results from there activites they did that are puled from firebase
    private ArrayList<String> activityResults = new ArrayList<>();
    //declaring a recycle view
    RecyclerView recyclerView;

    public MyActivityFragment() {
        // Required empty public constructor
    }

    //On create view set the the recycle view in the xml
    //also getting the current user loged in
    //--------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_my_activity, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser curentUserLogedIn = mAuth.getCurrentUser();
        userEmail = curentUserLogedIn.getEmail();

        recyclerView = rootView.findViewById(R.id.reclrAllActivities);

        return rootView;
    }
    //--------------------------------------------------------------------------------

    //onActivityCreated method that will call the AddAllActivities() method
    //----------------------------------------------------------------
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        AddAllActivities();
    }
    //----------------------------------------------------------------

    //AddAllActivities method is where I pull all the activities created by the current user and set them into there appropriate Array list
    //then checking if there is any data puled an if there is I am sending that data to the user activity adapter to set the values as
    //the layout user activities xml, this used for the recycled view
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void AddAllActivities(){
        firebaseRefForUsersActivities.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapShotOfAllUsersAddedActivities : dataSnapshot.getChildren()) {
                    UserActivityUpload userActivityUploadContainer = snapShotOfAllUsersAddedActivities.getValue(UserActivityUpload.class);

                    if (userActivityUploadContainer.getUserEmail().equals(userEmail)) {

                        imageURL.add(userActivityUploadContainer.getImageRef());
                        activityname.add("Activity Name : " + userActivityUploadContainer.getActivityName());
                        activityInfo.add(" Activity : " + userActivityUploadContainer.getActivity() + "     Date : " + userActivityUploadContainer.getTodayDate());
                        activityResults.add("Duration : " + userActivityUploadContainer.getDuration() + "     Calories Burned :" + " " + userActivityUploadContainer.getCaloriesBurned());
                    }

                    if (activityname.size() > 0 && imageURL.size() > 0) {
                        UserActivityAdapter mAdapter = new UserActivityAdapter(activityname, imageURL, activityInfo, activityResults, getActivity());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------
}
