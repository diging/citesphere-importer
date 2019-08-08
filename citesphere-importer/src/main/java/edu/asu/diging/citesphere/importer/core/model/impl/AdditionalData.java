package edu.asu.diging.citesphere.importer.core.model.impl;

public class AdditionalData {
    
    public final static String EMAIL_ADDRESSES = "email-addresses";

    private String fieldname;
    private String value;
    
    public AdditionalData() {}
    
    public AdditionalData(String fieldname, String value) {
        this.fieldname = fieldname;
        this.value = value;
    }
    
    public String getFieldname() {
        return fieldname;
    }
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
