package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.ArrayList;
import java.util.List;

public class ArticleMeta {

    private List<ArticleId> articleIds;
    private String articleTitle;
    private List<ArticleCategoryGroup> categoryGroups = new ArrayList<ArticleCategoryGroup>();
    private List<Contributor> contributors;
    private String authorNotesCorrespondence;
    private ArticlePublicationDate publicationDate;
    private String volume;
    private String issue;
    private String issueId;
    private String specialIssue;
    private String partNumber;
    private String supplement;
    private String firstPage;
    private String lastPage;
    private String pageCount;
    private String chapterCount;
    private String copyrightStatement;
    private String copyrightYear;
    private String copyrightHolder;
    private String selfUri;
    private String articleAbstract;
    private String language;
    private ReviewInfo reviewInfo;
    private String documentType;
    private String conferenceTitle;
    private String conferenceDate;
    private String conferenceLocation;
    private String conferenceSponsor;
    private String conferenceHost;
    private List<Keyword> keywords;
    private String reprintAddress;
    private List<AdditionalData> additionalData;
    private List<ContributorId> unassignedIds;
    private String fundingInfo;
    private String fundingText;
    private List<Reference> references;
    private String referenceCount;
    private String retrievalDate;
    
    public List<ArticleId> getArticleIds() {
        return articleIds;
    }
    public void setArticleIds(List<ArticleId> articleIds) {
        this.articleIds = articleIds;
    }
    public String getArticleTitle() {
        return articleTitle;
    }
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }
    public List<ArticleCategoryGroup> getCategories() {
        return categoryGroups;
    }
    public void setCategories(List<ArticleCategoryGroup> categoryGroups) {
        this.categoryGroups = categoryGroups;
    }
    public List<Contributor> getContributors() {
        return contributors;
    }
    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }
    public String getAuthorNotesCorrespondence() {
        return authorNotesCorrespondence;
    }
    public void setAuthorNotesCorrespondence(String authorNotesCorrespondence) {
        this.authorNotesCorrespondence = authorNotesCorrespondence;
    }
    public ArticlePublicationDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(ArticlePublicationDate publicationDate) {
        this.publicationDate = publicationDate;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public String getIssue() {
        return issue;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }
    public String getIssueId() {
        return issueId;
    }
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
    public String getSpecialIssue() {
        return specialIssue;
    }
    public void setSpecialIssue(String specialIssue) {
        this.specialIssue = specialIssue;
    }
    public String getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
    public String getSupplement() {
        return supplement;
    }
    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }
    public String getFirstPage() {
        return firstPage;
    }
    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }
    public String getLastPage() {
        return lastPage;
    }
    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }
    public String getPageCount() {
        return pageCount;
    }
    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }
    public String getChapterCount() {
        return chapterCount;
    }
    public void setChapterCount(String chapterCount) {
        this.chapterCount = chapterCount;
    }
    public String getCopyrightStatement() {
        return copyrightStatement;
    }
    public void setCopyrightStatement(String copyrightStatement) {
        this.copyrightStatement = copyrightStatement;
    }
    public String getCopyrightYear() {
        return copyrightYear;
    }
    public void setCopyrightYear(String copyrightYear) {
        this.copyrightYear = copyrightYear;
    }
    public String getCopyrightHolder() {
        return copyrightHolder;
    }
    public void setCopyrightHolder(String copyrightHolder) {
        this.copyrightHolder = copyrightHolder;
    }
    public String getSelfUri() {
        return selfUri;
    }
    public void setSelfUri(String selfUri) {
        this.selfUri = selfUri;
    }
    public String getArticleAbstract() {
        return articleAbstract;
    }
    public void setArticleAbstract(String articleAbstract) {
        this.articleAbstract = articleAbstract;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public ReviewInfo getReviewInfo() {
        return reviewInfo;
    }
    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }
    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public String getConferenceTitle() {
        return conferenceTitle;
    }
    public void setConferenceTitle(String conferenceTitle) {
        this.conferenceTitle = conferenceTitle;
    }
    public String getConferenceDate() {
        return conferenceDate;
    }
    public void setConferenceDate(String conferenceDate) {
        this.conferenceDate = conferenceDate;
    }
    public String getConferenceLocation() {
        return conferenceLocation;
    }
    public void setConferenceLocation(String conferenceLocation) {
        this.conferenceLocation = conferenceLocation;
    }
    public String getConferenceSponsor() {
        return conferenceSponsor;
    }
    public void setConferenceSponsor(String conferenceSponsor) {
        this.conferenceSponsor = conferenceSponsor;
    }
    public String getConferenceHost() {
        return conferenceHost;
    }
    public void setConferenceHost(String conferenceHost) {
        this.conferenceHost = conferenceHost;
    }
    public List<Keyword> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }
    public String getReprintAddress() {
        return reprintAddress;
    }
    public void setReprintAddress(String reprintAddress) {
        this.reprintAddress = reprintAddress;
    }
    public List<AdditionalData> getAdditionalData() {
        return additionalData;
    }
    public void setAdditionalData(List<AdditionalData> additionalData) {
        this.additionalData = additionalData;
    }
    public List<ContributorId> getUnassignedIds() {
        return unassignedIds;
    }
    public void setUnassignedIds(List<ContributorId> unassignedIds) {
        this.unassignedIds = unassignedIds;
    }
    public String getFundingInfo() {
        return fundingInfo;
    }
    public void setFundingInfo(String fundingInfo) {
        this.fundingInfo = fundingInfo;
    }
    public String getFundingText() {
        return fundingText;
    }
    public void setFundingText(String fundingText) {
        this.fundingText = fundingText;
    }
    public List<Reference> getReferences() {
        return references;
    }
    public void setReferences(List<Reference> references) {
        this.references = references;
    }
    public String getReferenceCount() {
        return referenceCount;
    }
    public void setReferenceCount(String referenceCount) {
        this.referenceCount = referenceCount;
    }
    public String getRetrievalDate() {
        return retrievalDate;
    }
    public void setRetrievalDate(String retrievalDate) {
        this.retrievalDate = retrievalDate;
    }
    
}
