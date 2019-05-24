package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class IssueIdTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "issue-id";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        articleMeta.setIssueId(node.getTextContent());
    }

}
