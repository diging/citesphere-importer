package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class SelfUriTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "self-uri";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        String uri = ((Element)node).getAttributeNS("http://www.w3.org/1999/xlink", "href");
        articleMeta.setSelfUri(uri);
    }

}
