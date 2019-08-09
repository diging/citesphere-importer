package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;

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
        if (containerMeta.getIssns() == null) {
            containerMeta.setIssns(new ArrayList<>());
        }
        
        Issn issn = new Issn();
        issn.setIssn(value);
        issn.setPubType(ISSN);
        containerMeta.getIssns().add(issn);
    }

}
