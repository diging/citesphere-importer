package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class BETagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "BE";
    }

    @Override
    public void handle(String field, String fieldValue, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        if (entry.getContainerMeta().getContributors() == null) {
            entry.getContainerMeta().setContributors(new ArrayList<Contributor>());
        }
        
        String[] values = splitValues(fieldValue, isColumnFormat);
        for (String value : values) {
            Contributor c = createContributor(value, ContributionType.EDITOR);
            entry.getContainerMeta().getContributors().add(c);
        }
    }

}
