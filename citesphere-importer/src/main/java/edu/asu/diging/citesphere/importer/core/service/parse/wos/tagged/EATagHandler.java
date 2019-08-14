package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;

@Component
public class EATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "EA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getArticleMeta().getAdditionalData() == null) {
            entry.getArticleMeta().setAdditionalData(new ArrayList<>());
        }
        
        entry.getArticleMeta().getAdditionalData().add(new AdditionalData(AdditionalData.EARLY_ACCESS_DATE, value)); 
    }

}
