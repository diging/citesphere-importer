package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

@Component
public class FXTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "FX";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        String existing = entry.getArticleMeta().getFundingText() != null ? entry.getArticleMeta().getFundingText()
                : "";
        entry.getArticleMeta().setFundingText(existing + value);
    }

}
