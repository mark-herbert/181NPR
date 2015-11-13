

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Kathreen Silen
 */
public class BillingGadgetImpl {
    private String gadgetId;
    private String billingId;
    private String itemName;
    private double gadgetRate;
    private boolean isSelected;

    public BillingGadgetImpl() {
        gadgetId = "";
        billingId = "";
        String itemName = "";
        double gadgetRate = 0.0;
        isSelected = false;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getGadgetRate() {
        return gadgetRate;
    }

    public void setGadgetRate(double gadgetRate) {
        this.gadgetRate = gadgetRate;
    }

    public String getGadgetId() {
        return gadgetId;
    }

    public void setGadgetId(String gadgetId) {
        this.gadgetId = gadgetId;
    }

    public String getBillingId() {
        return billingId;
    }

    public void setBillingId(String billingId) {
        this.billingId = billingId;
    }

    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    
}
