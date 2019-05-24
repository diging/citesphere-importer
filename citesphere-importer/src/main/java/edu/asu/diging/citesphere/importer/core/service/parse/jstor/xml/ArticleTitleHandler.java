package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public class ArticleTitleHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "title-group";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        if (node instanceof Element) {
            NodeList title = ((Element) node).getChildNodes();
            if (title != null) {
                for (int i = 0; i<title.getLength(); i++) {
                    Node titleNode = title.item(i);
                    if (!titleNode.getNodeName().equals("article-title")) {
                        continue;
                    } 
                    articleMeta.setArticleTitle(titleNode.getTextContent());
                    break;
                }
            }
        }
    }

}
