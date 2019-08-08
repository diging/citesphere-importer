package edu.asu.diging.citesphere.importer.core.model.impl;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public class Article implements BibEntry {

    private String articleType;
    private ContainerMeta containerMeta;
    private ArticleMeta articleMeta;
    
    public String getArticleType() {
        return articleType;
    }
    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }
    public ContainerMeta getJournalMeta() {
        return containerMeta;
    }
    public void setJournalMeta(ContainerMeta journalMeta) {
        this.containerMeta = journalMeta;
    }
    public ArticleMeta getArticleMeta() {
        return articleMeta;
    }
    public void setArticleMeta(ArticleMeta articleMeta) {
        this.articleMeta = articleMeta;
    }
    
}
