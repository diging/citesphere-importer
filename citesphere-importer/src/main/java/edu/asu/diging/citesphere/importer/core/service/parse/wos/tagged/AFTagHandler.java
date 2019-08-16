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
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getArticleMeta().getContributors() == null) {
            entry.getArticleMeta().setContributors(new ArrayList<>());
        }

        if (isColumnFormat) {
            if (value != null && !value.trim().isEmpty()) {
                String[] authors = value.split(";");
                for (fieldIdx = 0; fieldIdx < authors.length; fieldIdx++) {
                    addFullnameToContributor(authors[fieldIdx], fieldIdx, entry.getArticleMeta().getContributors(),
                            ContributionType.AUTHOR);
                }
            }
        } else {
            addFullnameToContributor(value, fieldIdx, entry.getArticleMeta().getContributors(),
                    ContributionType.AUTHOR);
        }
    }

}
