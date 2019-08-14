package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public interface IArticleWoSTagParser {

    void parseMetaTag(String field, String value, String previousField, int fieldIdx, BibEntry entry);
}