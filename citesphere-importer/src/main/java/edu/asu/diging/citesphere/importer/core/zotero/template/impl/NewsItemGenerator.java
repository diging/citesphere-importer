package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class NewsItemGenerator extends ItemJsonGenerator {

    @Override
    public String responsibleFor() {
        return Publication.NEWS_ITEM;
    }

    public String processPublicationTitle(JsonNode node, BibEntry article) {
        return article.getContainerMeta().getContainerTitle();
    }
    
    public String processISSN(JsonNode node, BibEntry article) {
        String epubIssn = null;
        if (article.getContainerMeta().getIssns() != null) {
            for (Issn issn : article.getContainerMeta().getIssns()) {
                if (issn.getPubType() != null && issn.getPubType().equals("issn")) {
                    return issn.getIssn();
                }
                if (issn.getPubType() != null && issn.getPubType().equals("ppub")) {
                    epubIssn = issn.getIssn();
                }
                if (issn.getPubType() != null && issn.getPubType().equals("epub") && epubIssn == null) {
                    epubIssn = issn.getIssn();
                }
            }
        }
        return epubIssn;
    }
}
