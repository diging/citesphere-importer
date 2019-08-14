package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.IdTypes;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class BookChapterGenerator extends ItemJsonGenerator {

    @Override
    public String responsibleFor() {
        return Publication.BOOK_CHAPTER;
    }

    public String processBookTitle(JsonNode node, BibEntry article) {
        if (article.getContainerMeta().getContainerTitle() != null) {
            return article.getContainerMeta().getContainerTitle();
        }
        return "";
    }
    
    public String processISBN(JsonNode node, BibEntry article) {
        if (article.getArticleMeta().getArticleIds() != null) {
            for (ArticleId id : article.getArticleMeta().getArticleIds()) {
                if (id.getPubIdType() != null && id.getPubIdType().toLowerCase().equals(IdTypes.ISBN)) {
                    return id.getId();
                }
            }
        }
        return "";
    }

}
