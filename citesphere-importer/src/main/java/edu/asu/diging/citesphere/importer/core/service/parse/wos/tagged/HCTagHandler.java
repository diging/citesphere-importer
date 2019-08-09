package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class HCTagHandler implements WoSMetaTagHandler {
    
    private final String PREFIX = "HC:";

    @Override
    public String handledTag() {
        return "HC";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getAdditionalData() == null) {
            articleMeta.setAdditionalData(new ArrayList<>());
        }
        
        articleMeta.getAdditionalData().add(new AdditionalData(AdditionalData.SERVICE_SPECIFIC_DATA, PREFIX + value)); 
    }

}
