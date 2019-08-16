package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class CLTagHandler extends MetaTagHandler {

    @Override
    public String handledTag() {
        return "CL";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry, boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getConferenceLocation() != null ? entry.getArticleMeta().getConferenceLocation() : "";
        entry.getArticleMeta().setConferenceLocation(existing + value);
    }

}
