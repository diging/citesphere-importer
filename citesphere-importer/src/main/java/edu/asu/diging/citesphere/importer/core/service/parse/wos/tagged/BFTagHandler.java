package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

public class BFTagHandler implements WoSMetaTagHandler {

    @Override
    public String handledTag() {
        return "BF";
    }

    @Override
    public void handle(String field, String value, String previousField, String previousValue,
            ContainerMeta containerMeta, ArticleMeta articleMeta) {
        if (containerMeta.getContributors() == null) {
            containerMeta.setContributors(new ArrayList<>());
        }
        
        Name name = getName(value);
        if (previousValue != null && !previousValue.trim().isEmpty()) {
            for (Contributor c : containerMeta.getContributors()) {
                if (c.getFullStandardizeName().equals(previousValue)) {
                    c.setFullGivenName(name.givenName);
                    c.setFullSurname(name.surname);
                    c.setFullName(value);
                    break;
                }
            }
        } else {
            Contributor contributor = new Contributor();
            contributor.setContributionType(ContributionType.AUTHOR);
            contributor.setGivenName(name.givenName);
            contributor.setFullGivenName(name.givenName);
            contributor.setSurname(name.surname);
            contributor.setFullSurname(name.surname);
            contributor.setFullName(value);
            containerMeta.getContributors().add(contributor);
        }
        
        
    }

    private Name getName(String fullName) {
        String[] nameParts = fullName.split(",");
        Name name = new Name();
        if (nameParts.length == 1) {
            name.surname = nameParts[0];
        } else if (nameParts.length == 2) {
            name.surname = nameParts[0];
            name.givenName = nameParts[1];
        } else {
            name.surname = fullName;
        }
        return name;
    }
    
    class Name {
        public String givenName;
        public String surname;
    }
}
