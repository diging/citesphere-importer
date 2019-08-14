package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SITagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "SI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String existing = entry.getArticleMeta().getSpecialIssue() != null ? entry.getArticleMeta().getSpecialIssue() : "";
        entry.getArticleMeta().setSpecialIssue(existing + value);
    }

}
