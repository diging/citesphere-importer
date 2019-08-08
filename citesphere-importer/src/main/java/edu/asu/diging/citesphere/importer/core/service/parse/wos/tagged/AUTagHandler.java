package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class AUTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AU";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta, ArticleMeta articleMeta) {
        Contributor contributor = createContributor(value, ContributionType.AUTHOR);
        
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<Contributor>());
        }
        
        articleMeta.getContributors().add(contributor);
    }


}
