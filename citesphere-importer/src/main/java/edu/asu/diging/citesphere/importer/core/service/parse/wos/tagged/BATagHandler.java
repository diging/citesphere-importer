package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

/**
 * Handler to parse the BA (book authors) tag. Currently, this is turned into creator type
 * "author".
 * @author jdamerow
 *
 */
@Component
public class BATagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "BA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        Contributor contributor = createContributor(value, ContributionType.AUTHOR);
        if (entry.getContainerMeta().getContributors() == null) {
            entry.getContainerMeta().setContributors(new ArrayList<Contributor>());
        }
        
        entry.getContainerMeta().getContributors().add(contributor);
    }


}
