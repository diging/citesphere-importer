package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class DITagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "DI";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getArticleIds() == null) {
            articleMeta.setArticleIds(new ArrayList<>());
        }

        ArticleId id = new ArticleId();
        id.setId(value);
        id.setPubIdType("doi");
        articleMeta.getArticleIds().add(id);
    }

}
