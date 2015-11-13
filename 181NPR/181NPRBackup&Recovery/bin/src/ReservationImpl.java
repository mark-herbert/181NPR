

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jefren
 */
public class ReservationImpl {
    private String lName;
    private String fName;
    private String mName;
    private String mNumber;
    private String email;
    private String zipCode;
    private String address;
    private String ayFrom;
    private String ayTo;
    private String sTerm;
    private String roomType;
    private String others;
    private String fullName;
    private String room;
    private String datePaid;
    private String remarks;
    private int daysLeft;

    public ReservationImpl() {
        lName = "";
        fName = "";
        mName = "";
        mNumber = "";
        email = "";
        zipCode = "";
        address = "";
        ayFrom = "";
        ayTo = "";
        sTerm = "";
        roomType = "";
        others = "";
        fullName = "";
        room = "";
        datePaid = "";
        remarks = "";
        daysLeft = 0;
    }

    // idinagdag ko by: kenneth
    public ReservationImpl(String fullName) {
        this.fullName = fullName;
    }

    public ReservationImpl(String fullName, String room, String datePaid) {
        this.fullName = fullName;
        this.room = room;
        this.datePaid = datePaid;
    }
    
    public ReservationImpl(String fullname, int daysLeft, String remarks){
        this.fullName = fullname;
        this.daysLeft = daysLeft;
        this.remarks = remarks;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRoom() {
        return room;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public String getlName() {
        return lName;
    }

    public String getfName() {
        return fName;
    }

    public String getmName() {
        return mName;
    }

    public String getmNumber() {
        return mNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getAddress() {
        return address;
    }

    public String getAyFrom() {
        return ayFrom;
    }

    public String getAyTo() {
        return ayTo;
    }

    public String getsTerm() {
        return sTerm;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getOthers() {
        return others;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAyFrom(String ayFrom) {
        this.ayFrom = ayFrom;
    }

    public void setAyTo(String ayTo) {
        this.ayTo = ayTo;
    }

    public void setsTerm(String sTerm) {
        this.sTerm = sTerm;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
