package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;

@Component
public class ARTagHandler extends CreatorTagHandler {

    @Override
    public String handledTag() {
        return "AR";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry,
            boolean isColumnFormat) {
        if (entry.getArticleMeta().getArticleIds() == null) {
            entry.getArticleMeta().setArticleIds(new ArrayList<>());
        }

        ArticleId id = new ArticleId();
        id.setId(value);
        id.setPubIdType("web-of-science");
        entry.getArticleMeta().getArticleIds().add(id);
    }

}
