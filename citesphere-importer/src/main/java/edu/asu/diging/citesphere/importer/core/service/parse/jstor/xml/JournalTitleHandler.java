package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta;

@Component
public class JournalTitleHandler implements JournalMetaTagHandler {

    @Override
    public String handledTag() {
        return "journal-title-group";
    }

    @Override
    public void handle(Node node, JournalMeta journalMeta) {
        if (node instanceof Element) {
            NodeList title = ((Element) node).getChildNodes();
            if (title != null) {
                for (int i = 0; i< title.getLength(); i++) {
                    Node titleNode = title.item(i);
                    if (!titleNode.getNodeName().equals("journal-title")) {
                        continue;
                    } 
                    journalMeta.setJournalTitle(titleNode.getTextContent());
                    break;
                }
            }
        }
    }

}
