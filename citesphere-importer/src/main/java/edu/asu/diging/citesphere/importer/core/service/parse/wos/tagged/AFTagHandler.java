package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;

@Component
public class AFTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AF";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        if (entry.getArticleMeta().getContributors() == null) {
            entry.getArticleMeta().setContributors(new ArrayList<>());
        }

        addFullnameToContributor(value, fieldIdx, entry.getArticleMeta().getContributors(), ContributionType.AUTHOR);
    }

}
