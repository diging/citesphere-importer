package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class ABTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "AB";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        String artAbstract = entry.getArticleMeta().getArticleAbstract() != null ? entry.getArticleMeta().getArticleAbstract() : "";
        entry.getArticleMeta().setArticleAbstract(artAbstract + value);
    }

}
