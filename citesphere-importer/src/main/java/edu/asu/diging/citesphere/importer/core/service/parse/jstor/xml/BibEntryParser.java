package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class BibEntryParser implements IBibEntryParser {

    @Override
    public void parseJournalMetaTag(Node node, ContainerMeta meta) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void parseArticleMetaTag(Node node, ArticleMeta meta) {
        // TODO Auto-generated method stub
        
    }

}
