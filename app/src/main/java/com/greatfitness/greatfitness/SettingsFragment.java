package com.greatfitness.greatfitness;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
public class SettingsFragment extends Fragment {

    //butons to take the user back to the log on page or to the edit profile
    Button logOut, editProfile;
    //setting a variable to set the activity to get the activity
    private Activity mActivity;
    //getting the firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //getting a refrence to the table users in firebase
    DatabaseReference getRefrenceToAllUsersInFirebase = database.getReference("Users");
    //strings to store the current user loged in and user profile image in firebase
    String userProfileInFireBase, userEmail;
    //image view to display user profile
    ImageView userProfilePic;
    //used to get the current user email
    private FirebaseAuth mAuth;


    public SettingsFragment() {
        // Required empty public constructor
    }

    //setting the buttons and user email to be used in this class
    //------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        logOut  = rootView.findViewById(R.id.btnLogOut);
        editProfile = rootView.findViewById(R.id.btnEditProfile);
        userProfilePic = rootView.findViewById(R.id.UserProfilePic);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userE = mAuth.getCurrentUser();
        userEmail = userE.getEmail();
        return rootView;
    }
    //------------------------------------------------------------

    //onActivityCreated loops through all the users to get the current user profile picture for the settings page
    //also check if the user clicked one of the buttons to take them to the destination they want
    //------------------------------------------------------------------------------------------
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        getRefrenceToAllUsersInFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSteps : dataSnapshot.getChildren()) {
                    UserUpload UserContainerInFirebase = userSteps.getValue(UserUpload.class);

                    if (UserContainerInFirebase.getUpPublisher().equals(userEmail)) {
                        userProfileInFireBase = UserContainerInFirebase.getUpProfileImage();
                    }
                }

                if(getActivity() != null){
                    Glide.with(getActivity())
                            .asBitmap()
                            .load(userProfileInFireBase)
                            .into(userProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserDetails.class);
                intent.putExtra("editProfile", "EditProfile");
                startActivity(intent);
            }
        });
    }
    //------------------------------------------------------------------------------------------

}
