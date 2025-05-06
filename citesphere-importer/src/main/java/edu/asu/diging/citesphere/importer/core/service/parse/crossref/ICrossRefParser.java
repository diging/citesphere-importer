package edu.asu.diging.citesphere.importer.core.service.parse.crossref;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.crossref.model.Item;

public interface ICrossRefParser {
    
    ContainerMeta parseJournalMeta(Item item);
    ArticleMeta parseArticleMeta(Item item);
    
}
