package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.FieldPrefixes;

@Component
public class JITagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "JI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getContainerMeta().getJournalAbbreviations() == null) {
            entry.getContainerMeta().setJournalAbbreviations(new ArrayList<>());
        }

        entry.getContainerMeta().getJournalAbbreviations().add(FieldPrefixes.ISO + value);
    }

}
