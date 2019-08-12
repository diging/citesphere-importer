package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.FieldPrefixes;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class ArticleGenerator extends ItemJsonGenerator {

    @Autowired
    private ExtraFieldHelper fieldHelper;
    
    /**
     * Maps jstor contributor types to zotero creator types
     */
    private Map<String, String> jstorZoteroCreatorMap;
    private Map<String, String> containerContributorsZoteroMap;

    @PostConstruct
    public void init() {
        super.init();
        jstorZoteroCreatorMap = fieldHelper.getJstorZoteroCreatorMap();
        containerContributorsZoteroMap = fieldHelper.getContainerContributorsZoteroMap();
    }

    public String responsibleFor() {
        return Publication.ARTICLE;
    }

    public String processTitle(JsonNode node, BibEntry article) {
        String title = article.getArticleMeta().getArticleTitle();
        if (title != null && !title.trim().isEmpty()) {
            return title;
        }

        if (article.getArticleMeta().getReviewInfo() != null
                && article.getArticleMeta().getReviewInfo().getTitle() != null
                && !article.getArticleMeta().getReviewInfo().getTitle().trim().isEmpty()) {
            return "Review of: " + article.getArticleMeta().getReviewInfo().getTitle();
        }

        return null;
    }

    public ArrayNode processCreators(JsonNode node, BibEntry article) {
        ArrayNode creators = getObjectMapper().createArrayNode();
        if (article.getArticleMeta().getContributors() != null) {
            for (Contributor contributor : article.getArticleMeta().getContributors()) {
                ObjectNode contributorNode = getObjectMapper().createObjectNode();
                if (jstorZoteroCreatorMap.get(contributor.getContributionType()) == null) {
                    logger.warn("Could not find creator type for " + contributor.getContributionType());
                }
                contributorNode.put("creatorType",
                        jstorZoteroCreatorMap.get(contributor.getContributionType()) != null
                                ? jstorZoteroCreatorMap.get(contributor.getContributionType())
                                : "author");
                fillContributorName(contributor, contributorNode);
                creators.add(contributorNode);
            }
        } else {
            ObjectNode contributorNode = getObjectMapper().createObjectNode();
            contributorNode.put("creatorType", jstorZoteroCreatorMap.get("author"));
            contributorNode.put("name", "Unknown");
            creators.add(contributorNode);
        }

        if (article.getArticleMeta().getReviewInfo() != null) {
            for (Contributor reviewedAuthor : article.getArticleMeta().getReviewInfo().getContributors()) {
                ObjectNode contributorNode = getObjectMapper().createObjectNode();
                contributorNode.put("creatorType", ZoteroCreatorTypes.REVIEWED_AUTHOR);
                fillContributorName(reviewedAuthor, contributorNode);
                creators.add(contributorNode);
            }
        }

        if (article.getContainerMeta().getContributors() != null) {
            for (Contributor contributor : article.getContainerMeta().getContributors()) {
                ObjectNode contributorNode = getObjectMapper().createObjectNode();
                contributorNode.put("creatorType",
                        containerContributorsZoteroMap.get(contributor.getContributionType()));
                fillContributorName(contributor, contributorNode);
                creators.add(contributorNode);
            }
        }

        return creators;
    }

    private void fillContributorName(Contributor contributor, ObjectNode contributorNode) {
        if (contributor.getGivenName() != null && !contributor.getGivenName().trim().isEmpty()
                && contributor.getSurname() != null && !contributor.getSurname().trim().isEmpty()) {
            contributorNode.put("firstName", contributor.getGivenName());
            contributorNode.put("lastName", contributor.getSurname());

        } else if (contributor.getGivenName() != null && !contributor.getGivenName().trim().isEmpty()) {
            contributorNode.put("name", contributor.getGivenName());
        } else if (contributor.getSurname() != null && !contributor.getSurname().trim().isEmpty()) {
            contributorNode.put("name", contributor.getSurname());
        } else {
            contributorNode.put("name", "Unknown");
        }
    }

    public String processAbstractNote(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getArticleAbstract();
    }

    public String processPublicationTitle(JsonNode node, BibEntry article) {
        return article.getContainerMeta().getJournalTitle();
    }

    public String processVolume(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getVolume();
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

    public String processDate(JsonNode node, BibEntry article) {
        List<String> date = new ArrayList<>();
        if (article.getArticleMeta().getPublicationDate() != null) {
            if (article.getArticleMeta().getPublicationDate().getPublicationDate() != null
                    && !article.getArticleMeta().getPublicationDate().getPublicationDate().isEmpty()) {
                return article.getArticleMeta().getPublicationDate().getPublicationDate();
            }
            ArticlePublicationDate pubDate = article.getArticleMeta().getPublicationDate();
            if (pubDate.getPublicationYear() != null && !pubDate.getPublicationYear().isEmpty()) {
                date.add(pubDate.getPublicationYear());
            }
            if (pubDate.getPublicationMonth() != null && !pubDate.getPublicationMonth().isEmpty()) {
                date.add(pubDate.getPublicationMonth());
            }
            if (pubDate.getPublicationDay() != null && !pubDate.getPublicationDay().isEmpty()) {
                date.add(pubDate.getPublicationDay());
            }
        }
        return String.join("-", date);
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

    public String processSeries(JsonNode node, BibEntry article) {
        StringBuffer seriesTitle = new StringBuffer();
        if (article.getContainerMeta().getSeriesTitle() != null) {
            seriesTitle.append(article.getContainerMeta().getSeriesTitle());
        }

        if (article.getContainerMeta().getSeriesSubTitle() != null
                && !article.getContainerMeta().getSeriesSubTitle().trim().isEmpty()) {
            seriesTitle.append(" - " + article.getContainerMeta().getSeriesSubTitle());
        }

        return seriesTitle.toString();
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

    public String processExtra(JsonNode node, BibEntry article) {
        String prefix = "Citesphere: ";
        ObjectNode root = getObjectMapper().createObjectNode();
        fieldHelper.createCreatorsExtra(article, root);
        fieldHelper.createIds(article, root);
        fieldHelper.createCategories(article, root);
        fieldHelper.createReviewInfo(article, root);
        fieldHelper.createCopyright(article, root);
        fieldHelper.createCorrespondenceNotes(article, root);
        fieldHelper.createJournalAbbreviations(article, root);
        fieldHelper.createPublisher(article, root);
        fieldHelper.createSeries(article, root);
        fieldHelper.createIssns(article, root);
        fieldHelper.createIssueId(article, root);
        fieldHelper.createSpecialIssue(article, root);
        fieldHelper.createPartNumber(article, root);
        fieldHelper.createSupplement(article, root);
        fieldHelper.createPageCount(article, root);
        fieldHelper.createChapterCount(article, root);
        fieldHelper.createDocumentType(article, root);
        fieldHelper.createConferenceInfo(article, root);
        fieldHelper.createKeywords(article, root);
        fieldHelper.createReprintAddress(article, root);
        fieldHelper.createAdditionalData(article, root);
        fieldHelper.createUnassignedContributorIds(article, root);
        fieldHelper.createFundingInfo(article, root);
        fieldHelper.createReferences(article, root);
        fieldHelper.createReferenceCount(article, root);
        fieldHelper.createRetrievalDate(article, root);

        try {
            return prefix + getObjectMapper().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            logger.error("Could not create Citesphere json.", e);
            return prefix;
        }
    }

    

    
}
