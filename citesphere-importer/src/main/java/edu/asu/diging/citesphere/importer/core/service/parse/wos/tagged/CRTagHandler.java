package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;

@Component
public class CRTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "CR";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        
        if (articleMeta.getReferences() == null) {
            articleMeta.setReferences(new ArrayList<>());
        }
        
        String[] parts = value.split(",");
        // let's assume there are at least name, year, and doi or title
        Reference ref = new Reference();
        if (parts.length > 3) {
            ref.setAuthors(parts[0]);
            ref.setYear(parts[1].trim());
            // assume last one is DOI
            setDOI(ref, parts[parts.length -1]);
        }
        
        ref.setReferenceString(value.trim());
        articleMeta.getReferences().add(ref);
    }

    private void setDOI(Reference ref, String part) {
        if (part.trim().startsWith("DOI")) {
            // cut off DOI
            ref.setIdentifier(part.trim().substring(4));
            ref.setIdentifierType("doi");
        } 
    }
}
