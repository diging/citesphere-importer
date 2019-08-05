package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public interface ArticleMetaTagHandler {

    String handledTag();

    void handle(Node node, ArticleMeta articleMeta);

}