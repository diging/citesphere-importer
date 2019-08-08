package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class BETagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "BE";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (containerMeta.getContributors() == null) {
            containerMeta.setContributors(new ArrayList<Contributor>());
        }
        
        Contributor c = createContributor(value, ContributionType.EDITOR);
        containerMeta.getContributors().add(c);
    }

}
