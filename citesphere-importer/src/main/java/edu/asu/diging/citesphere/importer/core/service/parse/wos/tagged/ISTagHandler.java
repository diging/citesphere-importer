package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class ISTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "IS";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getIssue() != null ? entry.getArticleMeta().getIssue() : "";
        entry.getArticleMeta().setIssue(existing + value);
    }

}
