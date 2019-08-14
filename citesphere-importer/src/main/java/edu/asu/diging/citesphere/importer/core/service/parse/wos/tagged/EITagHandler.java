package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class EITagHandler extends IdHandler {
    
    private final String EISSN = "eissn";

    @Override
    public String handledTag() {
        return "EI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        addId(entry.getArticleMeta(), value, EISSN);
    }

}
