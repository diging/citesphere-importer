package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SUTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SU";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getSupplement() != null ? entry.getArticleMeta().getSupplement() : "";
        entry.getArticleMeta().setSupplement(existing + value);
    }

}
