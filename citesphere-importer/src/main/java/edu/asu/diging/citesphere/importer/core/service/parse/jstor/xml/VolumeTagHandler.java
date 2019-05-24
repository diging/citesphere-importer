package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

@Component
public class VolumeTagHandler extends TagHandler implements ArticleMetaTagHandler {

    @Override
    public String handledTag() {
        return "volume";
    }

    @Override
    public void handle(Node node, ArticleMeta articleMeta) {
        articleMeta.setVolume(node.getTextContent());
    }

}
