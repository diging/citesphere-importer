package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

public class JournalMeta {

    private List<JournalId> journalIds;
    private String journalTitle;
    private String publisherName;
    private List<Issn> issns;
    
    public List<JournalId> getJournalIds() {
        return journalIds;
    }
    public void setJournalIds(List<JournalId> journalIds) {
        this.journalIds = journalIds;
    }
    public String getJournalTitle() {
        return journalTitle;
    }
    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }
    public String getPublisherName() {
        return publisherName;
    }
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
    public List<Issn> getIssns() {
        return issns;
    }
    public void setIssns(List<Issn> issns) {
        this.issns = issns;
    }   
    
}
