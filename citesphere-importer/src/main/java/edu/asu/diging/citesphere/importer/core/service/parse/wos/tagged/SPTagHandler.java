package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SPTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SP";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getConferenceSponsor() != null
                ? entry.getArticleMeta().getConferenceSponsor()
                : "";
        entry.getArticleMeta().setConferenceSponsor(existing + value);
        ;
    }

}
