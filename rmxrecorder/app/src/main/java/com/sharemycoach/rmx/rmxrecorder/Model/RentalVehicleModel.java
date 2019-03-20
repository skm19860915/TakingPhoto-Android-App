package com.sharemycoach.rmx.rmxrecorder.Model;

public class RentalVehicleModel {
    private String ReferenceEstimateSequenceId;
    private String QuickFindKeyWord;
    private String LocationSequenceId;
    private String LicensePlate;

    public String getReferenceEstimateSequenceId(){
        return ReferenceEstimateSequenceId;
    }
    public void setReferenceEstimateSequenceId(String ReferenceEstimateSequenceId){
        this.ReferenceEstimateSequenceId = ReferenceEstimateSequenceId;
    }
    public String getQuickFindKeyWord(){
        return QuickFindKeyWord;
    }
    public void setQuickFindKeyWord(String QuickFindKeyWord){
        this.QuickFindKeyWord = QuickFindKeyWord;
    }
    public String getLocationSequenceId() {
        return LocationSequenceId;
    }
    public void setLocationSequenceId(String LocationSequenceId){
        this.LocationSequenceId = LocationSequenceId;
    }
    public String getLicensePlate() {
        return LicensePlate;
    }
    public void setLicensePlate(String LicensePlate){
        this.LicensePlate = LicensePlate;
    }
}
