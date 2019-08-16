package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

public class Reference {

    private String authorString;
    private List<Contributor> contributors;
    private String title;
    private String year;
    private String identifier;
    private String identifierType;
    private String firstPage;
    private String endPage;
    private String volume;
    private String source;
    private String referenceId;
    private String referenceLabel;
    private String publicationType;
    private String citationId;
    
    private String referenceString;
    private String referenceStringRaw;
    
    public String getAuthorString() {
        return authorString;
    }
    public void setAuthorString(String authors) {
        this.authorString = authors;
    }
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
    public String getFirstPage() {
        return firstPage;
    }
    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }
    public String getEndPage() {
        return endPage;
    }
    public void setEndPage(String endPage) {
        this.endPage = endPage;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getReferenceString() {
        return referenceString;
    }
    public void setReferenceString(String referenceString) {
        this.referenceString = referenceString;
    }
    public String getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    public String getReferenceLabel() {
        return referenceLabel;
    }
    public void setReferenceLabel(String referenceLabel) {
        this.referenceLabel = referenceLabel;
    }
    public String getPublicationType() {
        return publicationType;
    }
    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }
    public String getCitationId() {
        return citationId;
    }
    public void setCitationId(String citationId) {
        this.citationId = citationId;
    }
    public String getReferenceStringRaw() {
        return referenceStringRaw;
    }
    public void setReferenceStringRaw(String referenceStringRaw) {
        this.referenceStringRaw = referenceStringRaw;
    }
    
}
