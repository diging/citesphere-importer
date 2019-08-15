package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;

@Component
public class ContributorHelper {

    public void setContributorData(Node stringName, Contributor contributor) {
     NodeList nameParts = stringName.getChildNodes();
        setContributorName(contributor, nameParts);
    }
    
    private void setContributorName(Contributor contributor, NodeList nameParts) {
        if (nameParts != null) {
            for (int i = 0; i<nameParts.getLength(); i++) {
                Node namePart = nameParts.item(i);
                if (namePart.getNodeName().equals("given-names")) {
                    contributor.setGivenName(namePart.getTextContent());
                    continue;
                }
                if (namePart.getNodeName().equals("surname")) {
                    contributor.setSurname(namePart.getTextContent());
                }
            }
        }
    }
}
