package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.FieldPrefixes;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class ArticleGenerator extends ItemJsonGenerator {

    public String responsibleFor() {
        return Publication.ARTICLE;
    }

    public String processPublicationTitle(JsonNode node, BibEntry article) {
        return article.getContainerMeta().getJournalTitle();
    }
    
    public String processIssue(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getIssue();
    }

    public String processPages(JsonNode node, BibEntry article) {
        List<String> pages = new ArrayList<>();
        if (article.getArticleMeta().getFirstPage() != null && !article.getArticleMeta().getFirstPage().isEmpty()) {
            pages.add(article.getArticleMeta().getFirstPage());
        }
        if (article.getArticleMeta().getLastPage() != null && !article.getArticleMeta().getLastPage().isEmpty()) {
            pages.add(article.getArticleMeta().getLastPage());
        }

        return String.join(" - ", pages);
    }
    
    public String processLanguage(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getLanguage();
    }

    public String processDOI(JsonNode node, BibEntry article) {
        if (article.getArticleMeta().getArticleIds() != null) {
            for (ArticleId id : article.getArticleMeta().getArticleIds()) {
                if (id.getPubIdType().equals("doi")) {
                    return id.getId();
                }
            }
        }
        return null;
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

    public String processUrl(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getSelfUri();
    }

    public String processJournalAbbreviation(JsonNode node, BibEntry entry) {
        String abbrev = "";
        if (entry.getContainerMeta().getJournalAbbreviations() != null) {
            for (String abb : entry.getContainerMeta().getJournalAbbreviations()) {
                if (abb.startsWith(FieldPrefixes.ISO)) {
                    return abb.substring(FieldPrefixes.ISO.length());
                }
                if (abb.length() > abb.indexOf(":")) {
                    abbrev = abb.substring(abb.indexOf(":") + 1);
                } else {
                    abbrev = abb;
                }
            }
        }
        return abbrev;
    }

}
