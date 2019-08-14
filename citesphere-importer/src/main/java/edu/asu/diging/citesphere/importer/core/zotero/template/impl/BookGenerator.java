package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.IdTypes;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class BookGenerator extends ItemJsonGenerator {

    public String responsibleFor() {
        return Publication.BOOK;
    }
    
    public String processNumPages(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getPageCount() != null ? article.getArticleMeta().getPageCount() : "";
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
    
    public String processUrl(JsonNode node, BibEntry article) {
        if (article.getArticleMeta().getSelfUri() != null) {
            return article.getArticleMeta().getSelfUri().trim();
        }
        return "";
    }
}
