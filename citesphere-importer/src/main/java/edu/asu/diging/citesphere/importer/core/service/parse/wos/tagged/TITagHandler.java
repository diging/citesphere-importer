package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class TITagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "TI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String title = entry.getArticleMeta().getArticleTitle() != null ? entry.getArticleMeta().getArticleTitle() : "";
        entry.getArticleMeta().setArticleTitle(title + value);
    }

}
