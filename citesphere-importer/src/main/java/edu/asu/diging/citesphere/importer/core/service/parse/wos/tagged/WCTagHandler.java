package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class WCTagHandler extends CategoryHandler {

    private final String WOS_GROUP = "web-of-science";

    @Override
    public String handledTag() {
        return "WC";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        addCategories(value, previousField, entry.getArticleMeta(), WOS_GROUP);
    }

}
