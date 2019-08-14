package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.IdTypes;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class ProceedingsPaperGenerator extends ItemJsonGenerator {

    @Override
    public String responsibleFor() {
        return Publication.PROCEEDINGS_PAPER;
    }

    public String processProceedingsTitle(JsonNode node, BibEntry article) {
        return article.getContainerMeta().getContainerTitle();
    }
    
    public String processConferenceName(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getConferenceTitle();
    }
    
    public String processDOI(JsonNode node, BibEntry article) {
        if (article.getArticleMeta().getArticleIds() != null) {
            for (ArticleId id : article.getArticleMeta().getArticleIds()) {
                if (id.getPubIdType().equals(IdTypes.DOI)) {
                    return id.getId();
                }
            }
        }
        return null;
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
