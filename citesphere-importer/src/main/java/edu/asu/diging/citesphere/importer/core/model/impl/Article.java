package edu.asu.diging.citesphere.importer.core.model.impl;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public class Article implements BibEntry {

    private String articleType;
    private JournalMeta journalMeta;
    private ArticleMeta articleMeta;
    
    public String getArticleType() {
        return articleType;
    }
    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }
    public JournalMeta getJournalMeta() {
        return journalMeta;
    }
    public void setJournalMeta(JournalMeta journalMeta) {
        this.journalMeta = journalMeta;
    }
    public ArticleMeta getArticleMeta() {
        return articleMeta;
    }
    public void setArticleMeta(ArticleMeta articleMeta) {
        this.articleMeta = articleMeta;
    }
    
}
