package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;

@Component
public class BFTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "BF";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (containerMeta.getContributors() == null) {
            containerMeta.setContributors(new ArrayList<>());
        }

        addFullnameToContributor(value, fieldIdx, containerMeta.getContributors(), ContributionType.AUTHOR);
    }
}
