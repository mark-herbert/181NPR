

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jefren
 */
public class ShuttleImpl {
    private String shuttleDate;
    private int numberOfRides;

    public ShuttleImpl(String shuttleDate, int numberOfRides) {
        this.shuttleDate = shuttleDate;
        this.numberOfRides = numberOfRides;
    }

    public ShuttleImpl() {
        shuttleDate = "";
        numberOfRides = 0;
    }
    
    public String getShuttleDate() {
        return shuttleDate;
    }

    public void setShuttleDate(String shuttleDate) {
        this.shuttleDate = shuttleDate;
    }

    public int getNumberOfRides() {
        return numberOfRides;
    }

    public void setNumberOfRides(int numberOfRides) {
        this.numberOfRides = numberOfRides;
    }
    
    
}
