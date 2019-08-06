package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class IssueTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "issue";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        articleMeta.setIssue(node.getTextContent());
    }

}
