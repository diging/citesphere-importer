package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class RPTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "RP";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        String existing = entry.getArticleMeta().getReprintAddress() != null ? entry.getArticleMeta().getReprintAddress() : "";
        entry.getArticleMeta().setReprintAddress(existing + value);
    }

}
