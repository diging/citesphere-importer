package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class SETagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SE";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        String existing = containerMeta.getSeriesTitle() != null ? containerMeta.getSeriesTitle() : "";
        containerMeta.setSeriesTitle(existing + value);
    }

}
