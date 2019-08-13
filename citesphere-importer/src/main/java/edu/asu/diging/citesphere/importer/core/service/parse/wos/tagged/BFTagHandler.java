package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;

@Component
public class BFTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "BF";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getContainerMeta().getContributors() == null) {
            entry.getContainerMeta().setContributors(new ArrayList<>());
        }

        addFullnameToContributor(value, fieldIdx, entry.getContainerMeta().getContributors(), ContributionType.AUTHOR);
    }
}
