package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class GPTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "GP";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        Contributor contributor = new Contributor();
        contributor.setContributionType(ContributionType.AUTHOR);
        contributor.setFullStandardizedName(value);
        contributor.setSurname(value);
        contributor.setFullSurname(value);
        
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<Contributor>());
        }
        
        containerMeta.getContributors().add(contributor);
    }

}
