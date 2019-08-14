package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.JournalId;

@Component
public class D2TagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "D2";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getContainerMeta().getJournalIds() == null) {
            entry.getContainerMeta().setJournalIds(new ArrayList<>());
        }

        JournalId id = new JournalId();
        id.setId(value);
        id.setIdType("doi");
        entry.getContainerMeta().getJournalIds().add(id);
    }

}
