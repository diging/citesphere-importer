package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class UTTagHandler extends IdHandler {
    
    private final String ACCESSION_NUMBER = "wos-accession-number";

    @Override
    public String handledTag() {
        return "UT";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry) {
        addId(entry.getArticleMeta(), value, ACCESSION_NUMBER);
    }

}
