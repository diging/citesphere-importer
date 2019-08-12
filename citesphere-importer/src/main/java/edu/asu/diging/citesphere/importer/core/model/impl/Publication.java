package edu.asu.diging.citesphere.importer.core.model.impl;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public class Publication implements BibEntry {
    
    public final static String ARTICLE = "article";
    public final static String BOOK = "book";
    public final static String REVIEW = "review";

    private String articleType;
    private ContainerMeta containerMeta;
    private ArticleMeta articleMeta;
    
    @Override
    public String getArticleType() {
        return articleType;
    }
    @Override
    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }
    @Override
    public ContainerMeta getContainerMeta() {
        return containerMeta;
    }
    @Override
    public void setJournalMeta(ContainerMeta journalMeta) {
        this.containerMeta = journalMeta;
    }
    @Override
    public ArticleMeta getArticleMeta() {
        return articleMeta;
    }
    @Override
    public void setArticleMeta(ArticleMeta articleMeta) {
        this.articleMeta = articleMeta;
    }
    
}