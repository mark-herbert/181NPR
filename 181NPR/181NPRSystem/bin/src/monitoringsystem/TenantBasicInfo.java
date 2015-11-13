/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoringsystem;

import java.util.Date;

/**
 *
 * @author Jefren
 */
public class TenantBasicInfo {
    private String lastname;
    private String firstname;
    private String middlename;
    private String college;
    private String department;
    private String course;
    private String year;
    private String gender;
    private String address;
    private long picture;
    private Date birthdate;
    
    public TenantBasicInfo(){
        lastname = "";
        firstname = "";
        middlename = "";
        college = "";
        department = "";
        course = "";
        year = "";
        gender = "";
        address = "";
        picture = 0;
        birthdate = new Date();
    }

    public TenantBasicInfo(String lastname, String firstname, String middlename, String college, String department, String course, String year, String gender, String address, long picture, Date birthdate) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlename = middlename;
        this.college = college;
        this.department = department;
        this.course = course;
        this.year = year;
        this.gender = gender;
        this.address = address;
        this.picture = picture;
        this.birthdate = birthdate;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPicture(long picture) {
        this.picture = picture;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getCollege() {
        return college;
    }

    public String getDepartment() {
        return department;
    }

    public String getCourse() {
        return course;
    }

    public String getYear() {
        return year;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public long getPicture() {
        return picture;
    }

    public Date getBirthdate() {
        return birthdate;
    }
    
    
}
