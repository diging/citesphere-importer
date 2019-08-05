package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta;

@Component
public class PublisherTagHandler implements JournalMetaTagHandler {

    @Override
    public String handledTag() {
        return "publisher";
    }

    @Override
    public void handle(Node node, JournalMeta journalMeta) {
        if (node instanceof Element) {
            NodeList publisher = ((Element) node).getChildNodes();
            if (publisher != null) {
                for (int i=0; i<publisher.getLength(); i++) {
                    Node publisherNode = publisher.item(i);
                    if (!publisherNode.getNodeName().equals("publisher-name")) {
                        continue;
                    }
                    journalMeta.setPublisherName(publisherNode.getTextContent());
                    break;
                }
            }
        }
    }

}
