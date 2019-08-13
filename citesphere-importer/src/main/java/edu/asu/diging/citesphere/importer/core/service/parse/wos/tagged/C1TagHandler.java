package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class C1TagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "C1";
    }

    /**
     * Assigns addresses to contributors. Strings should have the format:
     * [author 1; author 2] address where author can be either AU or AF
     */
    @Override
    public void handle(String field, String value, String previousField, int fieldIdx,
            BibEntry entry) {
        String[] authorsAddress = value.split("]", 2);
        if (authorsAddress.length == 2) {
            // cut [ from author string
            String authors = authorsAddress[0].substring(1);
            String[] authorsList = authors.split(";");
            
            for (String author : authorsList) {
                for (Contributor contributor : entry.getArticleMeta().getContributors()) {
                    if (contributor.getFullStandardizeName().equals(author) || contributor.getFullName().equals(author)) {
                        Affiliation affiliation = new Affiliation();
                        affiliation.setName(authorsAddress[1]);
                        if (contributor.getAffiliations() == null) {
                            contributor.setAffiliations(new ArrayList<Affiliation>());
                        }
                        contributor.getAffiliations().add(affiliation);
                    }
                }
            }
        }
    }

}
