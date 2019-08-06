package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta;

public interface IArticleTagParser {

    void parseJournalMetaTag(Node node, JournalMeta meta);

    void parseArticleMetaTag(Node node, ArticleMeta meta);

}