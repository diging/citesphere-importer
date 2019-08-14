package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class HOTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "HO";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getConferenceHost() != null ? entry.getArticleMeta().getConferenceHost() : "";
        entry.getArticleMeta().setConferenceHost(existing + value);
    }

}
