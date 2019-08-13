package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SOTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SO";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        String existing = entry.getContainerMeta().getContainerTitle() != null ? entry.getContainerMeta().getContainerTitle() : "";
        entry.getContainerMeta().setContainerTitle(existing + value);
    }

}
