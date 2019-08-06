package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class CustomMetaTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "custom-meta-group";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        List<Node> customMetas = getChildNodes(node, "custom-meta");
        for (Node customMeta : customMetas) {
            NodeList customChildren = customMeta.getChildNodes();
            String name = null;
            String value = null;
            for (int i = 0; i < customChildren.getLength(); i++) {
                Node child = customChildren.item(i);
                if (child.getNodeName().equals("meta-name")) {
                    name = child.getTextContent();
                } else if (child.getNodeName().equals("meta-value")) {
                    value = child.getTextContent();
                }
            }
            if (name != null && name.equals("lang")) {
                articleMeta.setLanguage(value);
                return;
            }
        }
    }

}
