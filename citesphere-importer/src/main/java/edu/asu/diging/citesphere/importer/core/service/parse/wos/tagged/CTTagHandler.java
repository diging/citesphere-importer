package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class CTTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "CT";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getConferenceTitle() != null ? entry.getArticleMeta().getConferenceTitle() : "";
        entry.getArticleMeta().setConferenceTitle(existing + value);
    }

}
