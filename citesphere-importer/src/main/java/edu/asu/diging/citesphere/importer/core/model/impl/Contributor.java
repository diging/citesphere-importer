package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.ArrayList;
import java.util.List;

public class Contributor {

    private String contributionType;
    private String givenName;
    private String surname;
    private String fullStandardizeName;
    private String fullGivenName;
    private String fullSurname;
    private String fullName;
    private List<Affiliation> affiliations = new ArrayList<Affiliation>();
    
    public String getContributionType() {
        return contributionType;
    }
    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }
    public String getGivenName() {
        return givenName;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getFullStandardizeName() {
        return fullStandardizeName;
    }
    public void setFullStandardizeName(String fullStandardizeName) {
        this.fullStandardizeName = fullStandardizeName;
    }
    public String getFullGivenName() {
        return fullGivenName;
    }
    public void setFullGivenName(String fullGivenName) {
        this.fullGivenName = fullGivenName;
    }
    public String getFullSurname() {
        return fullSurname;
    }
    public void setFullSurname(String fullSurname) {
        this.fullSurname = fullSurname;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public List<Affiliation> getAffiliations() {
        return affiliations;
    }
    public void setAffiliations(List<Affiliation> affiliations) {
        this.affiliations = affiliations;
    }
    
}
