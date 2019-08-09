package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class MATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "MA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        // apparently it's just a random list of email addresses that we can't easily assign
        // to contributors; so we'll just save them.
        if (articleMeta.getAdditionalData() == null) {
            articleMeta.setAdditionalData(new ArrayList<>());
        }
        
        articleMeta.getAdditionalData().add(new AdditionalData(AdditionalData.MEETING_ABSTRACT, value)); 
    }

}
