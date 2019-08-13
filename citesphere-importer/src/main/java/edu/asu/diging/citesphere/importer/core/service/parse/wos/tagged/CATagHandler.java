package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class CATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "CA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        Contributor contributor = new Contributor();
        contributor.setContributionType(ContributionType.AUTHOR);
        contributor.setFullStandardizedName(value);
        contributor.setSurname(value);
        contributor.setFullSurname(value);
        
        if (entry.getArticleMeta().getContributors() == null) {
            entry.getArticleMeta().setContributors(new ArrayList<Contributor>());
        }
        
        entry.getArticleMeta().getContributors().add(contributor);
    }

}
