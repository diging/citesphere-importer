package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class GPTagHandler extends MetaTagHandler {

    @Override
    public String handledTag() {
        return "GP";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getArticleMeta().getContributors() == null) {
            entry.getArticleMeta().setContributors(new ArrayList<Contributor>());
        }

        String[] contributors = splitValues(value, isColumnFormat);
        for (String contrib : contributors) {
            Contributor contributor = new Contributor();
            contributor.setContributionType(ContributionType.AUTHOR);
            contributor.setFullStandardizedName(contrib);
            contributor.setSurname(contrib);
            contributor.setFullSurname(contrib);

            entry.getArticleMeta().getContributors().add(contributor);
        }
    }

}
