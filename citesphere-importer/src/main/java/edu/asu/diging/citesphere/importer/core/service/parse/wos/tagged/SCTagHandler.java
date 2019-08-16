package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class SCTagHandler extends CategoryHandler {

    private final String CATEGORY_GROUP = "wos-research-areas";

    @Override
    public String handledTag() {
        return "SC";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        addCategories(value, previousField, entry.getArticleMeta(), CATEGORY_GROUP);
    }

}
