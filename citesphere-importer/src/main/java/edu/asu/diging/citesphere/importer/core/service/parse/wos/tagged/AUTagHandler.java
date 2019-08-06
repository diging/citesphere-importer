package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class AUTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "AU";
    }

    @Override
    public void handle(String field, String value, String previousField, String previousValue, ContainerMeta containerMeta, ArticleMeta articleMeta) {
        Contributor contributor = new Contributor();
        contributor.setContributionType(ContributionType.AUTHOR);
        contributor.setFullStandardizeName(value);
        if (value.contains(",")) {
            String[] nameParts = value.split(",");
            if (nameParts.length == 1) {
                contributor.setSurname(nameParts[0]);
            } else if (nameParts.length == 2) {
                contributor.setSurname(nameParts[0]);
                contributor.setGivenName(nameParts[1]);
            } else {
                contributor.setSurname(value);
            }
        }
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<Contributor>());
        }
        
        articleMeta.getContributors().add(contributor);
    }


}
