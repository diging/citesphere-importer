package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class LATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "LA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getLanguage() != null ? entry.getArticleMeta().getLanguage() : "";
        entry.getArticleMeta().setLanguage(existing + value);
    }

}
