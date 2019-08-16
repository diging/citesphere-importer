package edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;

@Component
public class PDTagHandler implements WoSMetaTagHandler {
    
    @Override
    public String handledTag() {
        return "PD";
    }

    @Override
    public void handle(String field, String value, String previousField, int fieldIdx, BibEntry entry, boolean isColumnFormat) {
        ArticlePublicationDate date = entry.getArticleMeta().getPublicationDate();
        if (date == null) {
            date = new ArticlePublicationDate();
            entry.getArticleMeta().setPublicationDate(date);
        }
        date.setPublicationDate(value);
    }

}
