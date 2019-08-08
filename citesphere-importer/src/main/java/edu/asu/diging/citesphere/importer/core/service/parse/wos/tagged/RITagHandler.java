package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributorId;
import edu.asu.diging.citesphere.importer.core.model.impl.Keyword;
import edu.asu.diging.citesphere.importer.core.model.impl.KeywordType;

@Component
public class RITagHandler implements WoSMetaTagHandler {
    
    private final String ID_SYSTEM = "web-of-science";

    @Override
    public String handledTag() {
        return "RI";
    }

    /** 
     * Adds reseracher ids to contributors. Value should have following format:
     * last, first/C-xxxx-xxxx; last, first/B-xxxx-xxxx
     * If contributors can't be matched with id, ids are added to article meta object
     * directly.
     */
    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
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
                    if (c.getFullStandardizeName().equals(nameId[0]) || c.getFullName().equals(nameId[1])) {
                        if (c.getIds() == null) {
                            c.setIds(new ArrayList<>());
                        }
                        c.getIds().add(new ContributorId(nameId[1], ID_SYSTEM));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    articleMeta.getUnassignedIds().add(new ContributorId(nameId[1], ID_SYSTEM));
                }
            }
         }
    }

}
