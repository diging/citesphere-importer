package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.List;

import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

public abstract class CreatorTagHandler implements WoSMetaTagHandler {

    protected Contributor createContributor(String name, String contributionType) {
        Contributor contributor = new Contributor();
        contributor.setContributionType(contributionType);
        contributor.setFullStandardizedName(name);
        if (name.contains(",")) {
            String[] nameParts = name.split(",");
            if (nameParts.length == 1) {
                contributor.setSurname(nameParts[0]);
            } else if (nameParts.length == 2) {
                contributor.setSurname(nameParts[0]);
                contributor.setGivenName(nameParts[1]);
            } else {
                contributor.setSurname(name);
            }
        }
        return contributor;
    }

    protected void addFullnameToContributor(String nameString, int fieldIdx, List<Contributor> contributors, String contributionType) {
        Name name = getName(nameString);

        // we assume that the order of standardized names is the same as of fullnames
        if (contributors.size() > fieldIdx) {
            Contributor c = contributors.get(fieldIdx);
            c.setFullGivenName(name.givenName);
            c.setFullSurname(name.surname);
            c.setFullName(nameString);

        } else {
            Contributor contributor = new Contributor();
            contributor.setContributionType(contributionType);
            contributor.setGivenName(name.givenName);
            contributor.setFullGivenName(name.givenName);
            contributor.setSurname(name.surname);
            contributor.setFullSurname(name.surname);
            contributor.setFullName(nameString);
            contributors.add(contributor);
        }
    }

    protected Name getName(String fullName) {
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
