package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributorId;

public class ContributorIdsHandler {

    public ContributorIdsHandler() {
        super();
    }

    protected void parseIds(String value, ArticleMeta articleMeta, String idSystem) {
        if (articleMeta.getContributors() == null) {
            articleMeta.setContributors(new ArrayList<>());
        }
        if (articleMeta.getUnassignedIds() == null) {
            articleMeta.setUnassignedIds(new ArrayList<>());
        }
    
        String[] ids = value.split(";");
    
        for (String id : ids) {
            String[] nameId = id.split("/");
            if (nameId.length == 2) {
                boolean found = false;
                for (Contributor c : articleMeta.getContributors()) {
                    if ((c.getFullStandardizeName() != null
                            && c.getFullStandardizeName().trim().toLowerCase().equals(nameId[0].toLowerCase()))
                            || (c.getFullName() != null && c.getFullName().equals(nameId[1]))) {
                        if (c.getIds() == null) {
                            c.setIds(new ArrayList<>());
                        }
                        c.getIds().add(new ContributorId(nameId[1], idSystem));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    articleMeta.getUnassignedIds().add(new ContributorId(nameId[1], idSystem));
                }
            }
        }
    }

}