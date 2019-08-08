package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
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
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta, ArticleMeta articleMeta) {
        Contributor contributor = createContributor(value, ContributionType.AUTHOR);
        if (containerMeta.getContributors() == null) {
            containerMeta.setContributors(new ArrayList<Contributor>());
        }
        
        containerMeta.getContributors().add(contributor);
    }


}
