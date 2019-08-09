package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class JITagHandler implements WoSMetaTagHandler {
    
    @Override
    public String handledTag() {
        return "JI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (containerMeta.getJournalAbbreviations() == null) {
            containerMeta.setJournalAbbreviations(new ArrayList<>());
        }
        
        containerMeta.getJournalAbbreviations().add(value);
    }

}
