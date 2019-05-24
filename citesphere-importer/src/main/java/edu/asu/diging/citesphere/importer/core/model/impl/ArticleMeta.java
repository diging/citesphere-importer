package edu.asu.diging.citesphere.importer.core.model.impl;

import java.util.List;

public class ArticleMeta {

    private List<ArticleId> articleIds;
    private String articleTitle;
    private List<ArticleCategory> categories;
    private List<Contributor> contributors;
    private String authorNotesCorrespondence;
    private ArticlePublicationDate publicationDate;
    private String volume;
    private String issue;
    private String issueId;
    private String firstPage;
    private String lastPage;
    private String copyrightStatement;
    private String copyrightYear;
    private String copyrightHolder;
    private String selfUri;
    private String articleAbstract;
    private String language;
    private ReviewInfo reviewInfo;
    
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
    public List<ArticleCategory> getCategories() {
        return categories;
    }
    public void setCategories(List<ArticleCategory> categories) {
        this.categories = categories;
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
    
    
}
