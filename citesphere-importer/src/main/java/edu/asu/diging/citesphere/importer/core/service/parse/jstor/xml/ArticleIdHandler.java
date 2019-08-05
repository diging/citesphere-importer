package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class ArticleIdHandler implements ArticleMetaTagHandler {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.JournalMetaTagHandler#handledTag()
     */
    @Override
    public String handledTag() {
        return "article-id";
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.JournalMetaTagHandler#handle(org.w3c.dom.Node, edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta)
     */
    @Override
    public void handle(Node node, ArticleMeta meta) {
        if (meta.getArticleIds() == null) {
            meta.setArticleIds(new ArrayList<>());
        }
        
        ArticleId id = new ArticleId();
        id.setId(node.getTextContent());
        if (node instanceof Element) {
            id.setPubIdType(((Element) node).getAttribute("pub-id-type"));
        }
        meta.getArticleIds().add(id);
    }
}
