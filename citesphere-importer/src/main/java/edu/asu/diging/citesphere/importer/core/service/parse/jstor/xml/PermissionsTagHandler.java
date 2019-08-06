package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class PermissionsTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "permissions";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        List<Node> statement = getChildNodes(node, "copyright-statement");
        if (!statement.isEmpty()) {
            articleMeta.setCopyrightStatement(statement.get(0).getTextContent());
        }
        
        List<Node> year = getChildNodes(node, "copyright-year");
        if (!year.isEmpty()) {
            articleMeta.setCopyrightYear(year.get(0).getTextContent());
        }
        
        List<Node> holder = getChildNodes(node, "copyright-holder");
        if (!holder.isEmpty()) {
            articleMeta.setCopyrightHolder(holder.get(0).getTextContent());
        }
    }

}
