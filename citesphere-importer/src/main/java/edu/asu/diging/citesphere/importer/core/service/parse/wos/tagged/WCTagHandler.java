package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class WCTagHandler extends CategoryHandler {

    private final String WOS_GROUP = "web-of-science";

    @Override
    public String handledTag() {
        return "WC";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        addCategories(value, previousField, articleMeta, WOS_GROUP);
    }

}