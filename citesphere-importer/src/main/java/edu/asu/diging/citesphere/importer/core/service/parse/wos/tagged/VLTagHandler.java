package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class VLTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "VL";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getVolume() != null ? entry.getArticleMeta().getVolume() : "";
        entry.getArticleMeta().setVolume(existing + value);
    }

}
