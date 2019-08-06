package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.ArrayList;
import java.util.List;

public class Contributor {

    private String contributionType;
    private String givenName;
    private String surname;
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
    public List<Affiliation> getAffiliations() {
        return affiliations;
    }
    public void setAffiliations(List<Affiliation> affiliations) {
        this.affiliations = affiliations;
    }
    
}
