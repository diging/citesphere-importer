package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class ARTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AR";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        if (articleMeta.getArticleIds() == null) {
            articleMeta.setArticleIds(new ArrayList<>());
        }

        ArticleId id = new ArticleId();
        id.setId(value);
        id.setPubIdType("web-of-science");
        articleMeta.getArticleIds().add(id);
    }

}
