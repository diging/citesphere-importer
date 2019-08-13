package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;

@Component
public class U1TagHandler implements WoSMetaTagHandler {
    
    private final String WOS_PREFIX = "wos-usage:";

    @Override
    public String handledTag() {
        return "U1";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getArticleMeta().getAdditionalData() == null) {
            entry.getArticleMeta().setAdditionalData(new ArrayList<>());
        }
        
        entry.getArticleMeta().getAdditionalData().add(new AdditionalData(AdditionalData.USAGE_COUNT, WOS_PREFIX + value.trim()));
    }

}
