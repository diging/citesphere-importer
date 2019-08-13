package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class CYTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "CY";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getConferenceDate() != null ? entry.getArticleMeta().getConferenceDate() : "";
        entry.getArticleMeta().setConferenceDate(existing + value);
    }

}
