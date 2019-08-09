package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class Z9TagHandler implements WoSMetaTagHandler {
    
    private final String WOS_PREFIX = "wos-total:";

    @Override
    public String handledTag() {
        return "Z9";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getAdditionalData() == null) {
            articleMeta.setAdditionalData(new ArrayList<>());
        }
        
        articleMeta.getAdditionalData().add(new AdditionalData(AdditionalData.TIMES_CITED, WOS_PREFIX + value.trim()));
    }

}
