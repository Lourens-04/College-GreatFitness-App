package com.greatfitness.greatfitness;

public class UserStepsUpload {

    public UserStepsUpload(){}

    //constuctor to be used to set values that are sent to this class
    //-------------------------------------------------------------------------------------------------------------------------
    public UserStepsUpload(String upPublisher, String upContainer, String upUserSteps, String upDate) {
        this.upPublisher = upPublisher;
        this.upContainer = upContainer;
        this.upUserSteps = upUserSteps;
        this.upDate = upDate;
    }
    //--------------------------------------------------------------------------------------------------------------------------

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

    public String getUpUserSteps() {
        return upUserSteps;
    }

    public void setUpUserSteps(String upUserSteps) {
        this.upUserSteps = upUserSteps;
    }

    public String getUpDate() {
        return upDate;
    }

    public void setUpDate(String upDate) {
        this.upDate = upDate;
    }
    //-----------------------------------------------------------------------------------------------------------

    //variables declared to be set and get from this class of a user steps taken
    //-------------------------------------
    private String upPublisher;
    private String upContainer;
    private String upUserSteps;
    private String upDate;
    //-------------------------------------
}
