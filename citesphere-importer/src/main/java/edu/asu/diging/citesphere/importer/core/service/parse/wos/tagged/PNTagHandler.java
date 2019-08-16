package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class PNTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "PN";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry, boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getPartNumber() != null ? entry.getArticleMeta().getPartNumber() : "";
        entry.getArticleMeta().setPartNumber(existing + value);
    }

}
