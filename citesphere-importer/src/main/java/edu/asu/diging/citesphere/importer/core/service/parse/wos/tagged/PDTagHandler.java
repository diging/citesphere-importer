package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;

@Component
public class PDTagHandler implements WoSMetaTagHandler {
    
    @Override
    public String handledTag() {
        return "PD";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, ContainerMeta containerMeta,
            ArticleMeta articleMeta) {
        ArticlePublicationDate date = articleMeta.getPublicationDate();
        if (date == null) {
            date = new ArticlePublicationDate();
            articleMeta.setPublicationDate(date);
        }
        date.setPublicationDate(value);
    }

}
