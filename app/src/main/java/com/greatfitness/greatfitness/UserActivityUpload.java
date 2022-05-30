package com.greatfitness.greatfitness;

public class UserActivityUpload {

    public UserActivityUpload(){}

    //constuctor to be used to set values that are sent to this class
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public UserActivityUpload(String userEmail, String activityFirebaseKey, String activityName, String activity, String caloriesBurned, String duration, String todayDate, String imageRef) {
        this.userEmail = userEmail;
        this.activityFirebaseKey = activityFirebaseKey;
        this.activityName = activityName;
        this.activity = activity;
        this.caloriesBurned = caloriesBurned;
        this.duration = duration;
        this.todayDate = todayDate;
        this.imageRef = imageRef;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //getters and setters for this class
    //-----------------------------------------------------------------------------------------------------------
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getActivityFirebaseKey() {
        return activityFirebaseKey;
    }

    public void setActivityFirebaseKey(String activityFirebaseKey) {
        this.activityFirebaseKey = activityFirebaseKey;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(String caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
    //-----------------------------------------------------------------------------------------------------------

    //variables declared to be set and get from this class of a user activity they added
    //-------------------------------------
    private String userEmail;
    private String activityFirebaseKey;
    private String activityName;
    private String activity;
    private String caloriesBurned;
    private String duration;
    private String todayDate;
    private String imageRef;
    //-------------------------------------
}
