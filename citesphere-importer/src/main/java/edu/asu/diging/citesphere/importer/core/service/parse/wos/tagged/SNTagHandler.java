package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class SNTagHandler extends IdHandler {
    
    private final String ISSN = "issn";

    @Override
    public String handledTag() {
        return "SN";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        addId(articleMeta, value, ISSN);
    }

}
