package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class BNTagHandler extends IdHandler {
    
    private final String ISBN = "ISBN";

    @Override
    public String handledTag() {
        return "BN";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        addId(entry.getArticleMeta(), value, ISBN);
    }

}
