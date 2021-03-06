package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class PGTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "PG";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        entry.getArticleMeta().setPageCount(value); 
    }

}
