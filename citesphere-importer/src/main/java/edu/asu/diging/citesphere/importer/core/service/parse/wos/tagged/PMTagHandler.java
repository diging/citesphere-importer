package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class PMTagHandler extends IdHandler {
    
    private final String PUBMED = "pubmed";

    @Override
    public String handledTag() {
        return "BN";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        addId(entry.getArticleMeta(), value, PUBMED);
    }

}
