package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class AuthorNotesTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "author-notes";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        List<Node> corresps = getChildNodes(node, "corresp");
        StringBuffer sb = new StringBuffer();
        for (Node corr : corresps) {
            sb.append(corr.getTextContent());
        }
        
        articleMeta.setAuthorNotesCorrespondence(sb.toString());
    }

}
