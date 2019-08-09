package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;

public abstract class IdHandler implements WoSMetaTagHandler {

    public IdHandler() {
        super();
    }

    protected void addId(ArticleMeta articleMeta, String idString, String type) {
        if (articleMeta.getArticleIds() == null) {
            articleMeta.setArticleIds(new ArrayList<>());
        }
        
        ArticleId id = new ArticleId();
        id.setId(idString);
        id.setPubIdType(type);
        articleMeta.getArticleIds().add(id);
    }

}