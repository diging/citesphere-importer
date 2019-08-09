package edu.asu.diging.citesphere.importer.core.model.impl;

public class AdditionalData {
    
    public final static String EMAIL_ADDRESSES = "email-addresses";
    public final static String TIMES_CITED = "times-cited";
    public final static String USAGE_COUNT = "usage-count";
    public final static String MEETING_ABSTRACT = "meeting-abstract";
    public final static String EARLY_ACCESS_DATE = "early-access-date";
    public final static String EARLY_ACCESS_YEAR = "early-access-year";
    public final static String DOCUMENT_DELIVERY_NUMBER = "document-delivery-number";
    public final static String OPEN_ACCESS = "open-access";
    public final static String SERVICE_SPECIFIC_DATA = "service-specific-data";

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
