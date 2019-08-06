package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

public interface IArticleWoSTagParser {

    void parseMetaTag(String field, String value, String previousField, String previousValue, ContainerMeta containerMeta, ArticleMeta articleMeta);
}