package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class OITagHandler extends ContributorIdsHandler {

    private final String ID_SYSTEM = "orcid";

    @Override
    public String handledTag() {
        return "OI";
    }

    /**
     * Adds reseracher ids to contributors. Value should have following format:
     * last, first/C-xxxx-xxxx; last, first/B-xxxx-xxxx If contributors can't be
     * matched with id, ids are added to article meta object directly.
     */
    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String[] ids = splitValues(value, isColumnFormat);
        for (String id : ids) {
            parseIds(id, entry.getArticleMeta(), ID_SYSTEM);
        }
    }

}
