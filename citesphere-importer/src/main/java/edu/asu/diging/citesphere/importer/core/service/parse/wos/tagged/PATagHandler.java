package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class PATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "PA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getContainerMeta().getPublisherAddress() != null ? entry.getContainerMeta().getPublisherAddress() : "";
        entry.getContainerMeta().setPublisherAddress(existing + value);
    }

}
