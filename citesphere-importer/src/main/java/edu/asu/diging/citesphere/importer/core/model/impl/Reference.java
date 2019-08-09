package edu.asu.diging.citesphere.importer.core.model.impl;

public class Reference {

    private String authors;
    private String year;
    private String identifier;
    private String identifierType;
    private String referenceString;
    
    public String getAuthors() {
        return authors;
    }
    public void setAuthors(String authors) {
        this.authors = authors;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getIdentifierType() {
        return identifierType;
    }
    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }
    public String getReferenceString() {
        return referenceString;
    }
    public void setReferenceString(String referenceString) {
        this.referenceString = referenceString;
    }
    
}
