package edu.asu.diging.citesphere.importer.core.model;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

public interface BibEntry {

    void setArticleMeta(ArticleMeta articleMeta);

    ArticleMeta getArticleMeta();

    void setJournalMeta(ContainerMeta journalMeta);

    ContainerMeta getContainerMeta();

    void setArticleType(String articleType);

    String getArticleType();

}
