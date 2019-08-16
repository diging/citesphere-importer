package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SETagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SE";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String existing = entry.getContainerMeta().getSeriesTitle() != null ? entry.getContainerMeta().getSeriesTitle()
                : "";
        entry.getContainerMeta().setSeriesTitle(existing + value);
    }

}
