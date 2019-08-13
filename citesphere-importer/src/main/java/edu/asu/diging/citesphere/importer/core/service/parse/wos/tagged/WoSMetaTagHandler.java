package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public interface WoSMetaTagHandler {

    String handledTag();

    void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry);

}