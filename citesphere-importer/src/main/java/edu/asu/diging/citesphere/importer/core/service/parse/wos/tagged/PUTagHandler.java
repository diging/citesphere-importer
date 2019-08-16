package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class PUTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "PU";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry, boolean isColumnFormat) {
        String existing = entry.getContainerMeta().getPublisherName() != null ? entry.getContainerMeta().getPublisherName() : "";
        entry.getContainerMeta().setPublisherName(existing + value);
    }

}
