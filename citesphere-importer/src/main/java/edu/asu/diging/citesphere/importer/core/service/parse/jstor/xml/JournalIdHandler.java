package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.JournalId;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class JournalIdHandler implements JournalMetaTagHandler {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.JournalMetaTagHandler#handledTag()
     */
    @Override
    public String handledTag() {
        return "journal-id";
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.JournalMetaTagHandler#handle(org.w3c.dom.Node, edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta)
     */
    @Override
    public void handle(Node node, ContainerMeta journalMeta) {
        if (journalMeta.getJournalIds() == null) {
            journalMeta.setJournalIds(new ArrayList<>());
        }
        
        JournalId id = new JournalId();
        id.setId(node.getTextContent());
        if (node instanceof Element) {
            id.setIdType(((Element) node).getAttribute("journal-id-type"));
        }
        journalMeta.getJournalIds().add(id);
    }
}
