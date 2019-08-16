package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class BSTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "BS";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        String existing = entry.getContainerMeta().getSeriesSubTitle() != null ? entry.getContainerMeta().getSeriesSubTitle() : "";
        entry.getContainerMeta().setSeriesSubTitle(existing + value);
    }

}
