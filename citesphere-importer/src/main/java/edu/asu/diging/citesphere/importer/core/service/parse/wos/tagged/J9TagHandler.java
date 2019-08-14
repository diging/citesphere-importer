package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class J9TagHandler implements WoSMetaTagHandler {
    
    private final String PREFIX = "29chars:";
    
    @Override
    public String handledTag() {
        return "J9";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getContainerMeta().getJournalAbbreviations() == null) {
            entry.getContainerMeta().setJournalAbbreviations(new ArrayList<>());
        }
        
        entry.getContainerMeta().getJournalAbbreviations().add(PREFIX + value);
    }

}
