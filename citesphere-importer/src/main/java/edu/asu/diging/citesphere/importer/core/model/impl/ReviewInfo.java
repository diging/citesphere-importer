package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

public class ReviewInfo {

    private List<Contributor> contributors;
    private String title;
    private String year;
    private String fullDescription;
    
    public List<Contributor> getContributors() {
        return contributors;
    }
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getFullDescription() {
        return fullDescription;
    }
    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }
}
