package com.greatfitness.greatfitness;

public class UserUpload {

    //variables declared to be set and get from this class of a user details
    //-------------------------------------
    private String upPublisher;
    private String upContainer;
    private String upFirstname;
    private String upLastname;
    private String upWeight;
    private String upHeight;
    private String upUserStepGoal;
    private String upUserWeightGoal;
    private String upMetOrImp;
    private String upProfileImage;
    //-------------------------------------

    public UserUpload(){}

    //constuctor to be used to set values that are sent to this class
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public UserUpload(String upPublisher, String upContainer, String upFirstname, String upLastname, String upWeight, String upHeight, String upUserStepGoal, String upUserWeightGoal, String upMetOrImp, String upProfileImage) {

        if (upFirstname.trim().equals("")){
            upFirstname = "No Name";
        }
        if (upLastname.trim().equals("")){
            upLastname = "No Last Name";
        }
        if (upWeight.trim().equals("")){
            upWeight = "0";
        }
        if (upHeight.trim().equals("")){
            upHeight = "0";
        }
        if (upUserStepGoal.trim().equals("")){
            upUserStepGoal = "0";
        }
        if (upUserWeightGoal.trim().equals("")){
            upUserWeightGoal = "0";
        }

        this.upPublisher = upPublisher;
        this.upContainer = upContainer;
        this.upFirstname = upFirstname;
        this.upLastname = upLastname;
        this.upWeight = upWeight;
        this.upHeight = upHeight;
        this.upUserStepGoal = upUserStepGoal;
        this.upUserWeightGoal = upUserWeightGoal;
        this.upMetOrImp = upMetOrImp;
        this.upProfileImage = upProfileImage;
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //getters and setters for this class
    //-----------------------------------------------------------------------------------------------------------
    public String getUpPublisher() {
        return upPublisher;
    }

    public void setUpPublisher(String upPublisher) {
        this.upPublisher = upPublisher;
    }

    public String getUpContainer() {
        return upContainer;
    }

    public void setUpContainer(String upContainer) {
        this.upContainer = upContainer;
    }

    public String getUpFirstname() {
        return upFirstname;
    }

    public void setUpFirstname(String upFirstname) {
        this.upFirstname = upFirstname;
    }

    public String getUpLastname() {
        return upLastname;
    }

    public void setUpLastname(String upLastname) {
        this.upLastname = upLastname;
    }

    public String getUpWeight() {
        return upWeight;
    }

    public void setUpWeight(String upWeight) {
        this.upWeight = upWeight;
    }

    public String getUpHeight() {
        return upHeight;
    }

    public void setUpHeight(String upHeight) {
        this.upHeight = upHeight;
    }

    public String getUpUserStepGoal() {
        return upUserStepGoal;
    }

    public void setUpUserStepGoal(String upUserStepGoal) {
        this.upUserStepGoal = upUserStepGoal;
    }

    public String getUpUserWeightGoal() {
        return upUserWeightGoal;
    }

    public void setUpUserWeightGoal(String upUserWeightGoal) {
        this.upUserWeightGoal = upUserWeightGoal;
    }

    public String getUpMetOrImp() {
        return upMetOrImp;
    }

    public void setUpMetOrImp(String upMetricSystem) {
        this.upMetOrImp = upMetricSystem;
    }

    public String getUpProfileImage() {
        return upProfileImage;
    }

    public void setUpProfileImage(String upProfileImage) {
        this.upProfileImage = upProfileImage;
    }
    //-----------------------------------------------------------------------------------------------------------



















}
