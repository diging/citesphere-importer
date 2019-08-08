package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

/**
 * This class hold information about the "container" of a publication (e.g. the journal
 * an article was published in, or the book a chapter was part of.
 * @author jdamerow
 *
 */
public class ContainerMeta {

    private List<JournalId> journalIds;
    private String journalTitle;
    private String publisherName;
    private String seriesTitle;
    private String seriesSubTitle;
    private List<Issn> issns;
    private List<Contributor> contributors;
    
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
    public String getSeriesTitle() {
        return seriesTitle;
    }
    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }
    public String getSeriesSubTitle() {
        return seriesSubTitle;
    }
    public void setSeriesSubTitle(String seriesSubTitle) {
        this.seriesSubTitle = seriesSubTitle;
    }
    public List<Issn> getIssns() {
        return issns;
    }
    public void setIssns(List<Issn> issns) {
        this.issns = issns;
    }
    public List<Contributor> getContributors() {
        return contributors;
    }
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }   
    
}
