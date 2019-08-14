package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class AUTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AU";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        Contributor contributor = createContributor(value, ContributionType.AUTHOR);
        
        if (entry.getArticleMeta().getContributors() == null) {
            entry.getArticleMeta().setContributors(new ArrayList<Contributor>());
        }
        
        entry.getArticleMeta().getContributors().add(contributor);
    }


}
