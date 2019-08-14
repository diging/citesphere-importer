package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class DATagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "DA";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        entry.getArticleMeta().setRetrievalDate(value);
    }

}
