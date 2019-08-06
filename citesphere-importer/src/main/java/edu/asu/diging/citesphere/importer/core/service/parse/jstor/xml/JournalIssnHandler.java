package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class JournalIssnHandler implements JournalMetaTagHandler {

    @Override
    public String handledTag() {
        return "issn";
    }

    @Override
    public void handle(Node node, ContainerMeta journalMeta) {
        if (journalMeta.getIssns() == null) {
            journalMeta.setIssns(new ArrayList<>());
        }
        
        Issn issn = new Issn();
        issn.setIssn(node.getTextContent());
        if (node instanceof Element) {
            issn.setPubType(((Element) node).getAttribute("pub-type"));
        }
        journalMeta.getIssns().add(issn);
    }

}
