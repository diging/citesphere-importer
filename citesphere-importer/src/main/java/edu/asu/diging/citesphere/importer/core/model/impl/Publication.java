package edu.asu.diging.citesphere.importer.core.model.impl;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public class Publication implements BibEntry {
    
    public final static String ARTICLE = "article";
    public final static String BOOK = "book";
    public final static String BOOK_CHAPTER = "bookChapter";
    public final static String REVIEW = "review";
    public final static String LETTER = "letter";
    public final static String NEWS_ITEM = "newspaperArticle";
    public final static String PROCEEDINGS_PAPER = "conferencePaper";
    public final static String DOCUMENT = "document";
    // publication types in CrossRef
    public final static String JOURNAL_ISSUE = "journal-issue";
    public final static String REFERNCE_ENTRY = "reference-entry";
    public final static String POSTED_CONTENT = "posted-content";
    public final static String COMPONENT = "component";
    public final static String EDITED_BOOK = "edited-book";
    public final static String DISSERTATION = "dissertation";
    public final static String REPORT_COMPONENT = "report-component";
    public final static String REPORT = "report";
    public final static String PEER_REVIEW = "peer-review";
    public final static String BOOK_TRACK = "book-track";
    public final static String BOOK_PART = "book-part";
    public final static String OTHER = "other";
    public final static String JORUNAL_VOLUME = "journal-volume";
    public final static String BOOK_SET = "book-set";
    public final static String JOURNAL = "journal";
    public final static String PROCEEDINGS_SERIES = "proceedings-series";
    public final static String REPORT_SERIES = "report-series";
    public final static String PROCEEDINGS = "proceedings";
    public final static String DATABASE = "database";
    public final static String STANDARD = "standard";
    public final static String REFERENCE_BOOK = "reference-book";
    public final static String GRANT = "grant";
    public final static String DATASET = "dataset";
    public final static String BOOK_SERIES = "book-series";

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
