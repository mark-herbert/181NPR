
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mark Herbert Cabuang
 */
public class BillingImpl {    
    private String full_name;
    private String registrationDate;
    private String balance;
    private String datePaid;
    

    public BillingImpl() {
        full_name = "";
        registrationDate = "";
        balance = "";
        datePaid = "";
    }

    public BillingImpl(String full_name, String registrationDate, String balance, String datePaid) {
        this.full_name = full_name;
        this.registrationDate = registrationDate;
        this.balance = balance;
        this.datePaid = datePaid;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }
}
