package com.greatfitness.greatfitness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivityAdapter extends RecyclerView.Adapter<UserActivityAdapter.ViewHolder>{

    //declaring arrays that will contain values sent to this class for the recycle view and making a variable for the context
    //--------------------------------------------------------------------------
    private ArrayList<String> mActivitiesNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mActivityInfo = new ArrayList<>();
    private ArrayList<String> mAcivityResults = new ArrayList<>();
    private Context mContext;
    //--------------------------------------------------------------------------

    //constructer to set the values in the globle array list declared in this class as well as the context
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public UserActivityAdapter(ArrayList<String> mActivitiesNames, ArrayList<String> mImages, ArrayList<String> mActivityInfo, ArrayList<String> mAcivityResults, Context mContext) {
        this.mActivitiesNames = mActivitiesNames;
        this.mImages = mImages;
        this.mActivityInfo = mActivityInfo;
        this.mAcivityResults = mAcivityResults;
        this.mContext = mContext;
    }
    //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // onCreateViewHolder that will put the layout_user_activities xml into a holder to be used in the rest of the class like setting text views
    //------------------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_activities,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    //------------------------------------------------------------------------------------------------------------------------------

    //onBindViewHolder method to set the text views values to display the data that was pulled from firebase
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);

        holder.activityName.setText(mActivitiesNames.get(position));
        holder.activityInfo.setText(mActivityInfo.get(position));
        holder.acivityResults.setText(mAcivityResults.get(position));
    }
    //-------------------------------------------------------------------------------------------------

    //getItemCount gets the number of items it needs to create to be displayed to the user
    //---------------------------------------------------------------
    @Override
    public int getItemCount() {
        return mActivitiesNames.size();
    }
    //---------------------------------------------------------------


    //ViewHolder method that extends the RecyclerView to set the content on there that will be used to populate them with
    //data that is pulled from firebase
    //-----------------------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView activityName, activityInfo, acivityResults;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            activityName = itemView.findViewById(R.id.txbNameOfActivity);
            activityInfo = itemView.findViewById(R.id.txbActivityDetails);
            acivityResults = itemView.findViewById(R.id.tbxActivityResults);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
    //-----------------------------------------------------------------------------
}
