package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;

@Component
public class PublicationDateTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "pub-date";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        ArticlePublicationDate pubDate = new ArticlePublicationDate();
        List<Node> day = getChildNodes(node, "day");
        if (!day.isEmpty()) {
            pubDate.setPublicationDay(day.get(0).getTextContent());
        }
        
        List<Node> month = getChildNodes(node, "month");
        if (!month.isEmpty()) {
            pubDate.setPublicationMonth(month.get(0).getTextContent());
        }
        
        List<Node> year = getChildNodes(node, "year");
        if (!year.isEmpty()) {
            pubDate.setPublicationYear(month.get(0).getTextContent());
        }
        
        List<Node> date = getChildNodes(node, "string-date");
        if (!date.isEmpty()) {
            pubDate.setPublicationDate(date.get(0).getTextContent());
        }
        
        pubDate.setPublicationDateType(((Element)node).getAttribute("pub-type"));
        articleMeta.setPublicationDate(pubDate);
    }

}
