package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;

@Component
public class EMTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "EM";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        // apparently it's just a random list of email addresses that we can't easily
        // assign
        // to contributors; so we'll just save them.
        if (entry.getArticleMeta().getAdditionalData() == null) {
            entry.getArticleMeta().setAdditionalData(new ArrayList<>());
        }

        entry.getArticleMeta().getAdditionalData().add(new AdditionalData(AdditionalData.EMAIL_ADDRESSES, value));
    }

}
