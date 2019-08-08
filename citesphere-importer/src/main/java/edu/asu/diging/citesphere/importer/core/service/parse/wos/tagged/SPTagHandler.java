package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class SPTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SP";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            ContainerMeta containerMeta, ArticleMeta articleMeta) {
        String existing = articleMeta.getConferenceSponsor() != null ? articleMeta.getConferenceSponsor() : "";
        articleMeta.setConferenceSponsor(existing + value);;
    }

}
