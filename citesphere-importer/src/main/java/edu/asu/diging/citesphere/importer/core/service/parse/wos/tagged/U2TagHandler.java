package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;

@Component
public class U2TagHandler implements WoSMetaTagHandler {

    private final String WOS_PREFIX = "wos-usage-2013:";

    @Override
    public String handledTag() {
        return "U2";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getArticleMeta().getAdditionalData() == null) {
            entry.getArticleMeta().setAdditionalData(new ArrayList<>());
        }

        entry.getArticleMeta().getAdditionalData()
                .add(new AdditionalData(AdditionalData.USAGE_COUNT, WOS_PREFIX + value.trim()));
    }

}
