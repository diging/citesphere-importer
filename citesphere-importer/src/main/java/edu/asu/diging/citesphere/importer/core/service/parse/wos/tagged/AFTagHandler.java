package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;

@Component
public class AFTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AF";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<>());
        }

        addFullnameToContributor(value, fieldIdx, articleMeta.getContributors(), ContributionType.AUTHOR);
    }

}