package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.FieldPrefixes;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategory;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategoryGroup;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.JournalId;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;
import edu.asu.diging.citesphere.importer.core.zotero.template.ItemJsonGenerator;

@Service
public class ArticleGenerator extends ItemJsonGenerator {

    /**
     * Maps jstor contributor types to zotero creator types
     */
    private Map<String, String> jstorZoteroCreatorMap;

    @PostConstruct
    public void init() {
        super.init();
        jstorZoteroCreatorMap = new HashMap<>();
        jstorZoteroCreatorMap.put("author", "author");
        jstorZoteroCreatorMap.put("editor", "editor");
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
                contributorNode.put("creatorType", "reviewedAuthor");
                fillContributorName(reviewedAuthor, contributorNode);
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
        seriesTitle.append(article.getContainerMeta().getSeriesTitle());

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
        createCreatorsExtra(article, root);
        createIds(article, root);
        createCategories(article, root);
        createReviewInfo(article, root);
        createCopyright(article, root);
        createCorrespondenceNotes(article, root);
        createJournalAbbreviations(article, root);
        createPublisher(article, root);
        createSeries(article, root);
        createIssns(article, root);

        try {
            return prefix + getObjectMapper().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            logger.error("Could not create Citesphere json.", e);
            return prefix;
        }
    }

    private void createCreatorsExtra(BibEntry article, ObjectNode root) {
        ArrayNode authors = root.putArray("authors");
        ArrayNode editors = root.putArray("editors");
        ArrayNode others = root.putArray("otherCreators");
        if (article.getArticleMeta().getContributors() != null) {
            int idx = 0;
            for (Contributor contrib : article.getArticleMeta().getContributors()) {
                ObjectNode contribNode = null;
                if (contrib.getContributionType().equals("author")) {
                    contribNode = authors.addObject();
                    fillPerson(contrib, contribNode, idx);
                } else if (contrib.getContributionType().equals("editor")) {
                    contribNode = editors.addObject();
                    fillPerson(contrib, contribNode, idx);
                } else {
                    contribNode = others.addObject();
                    String role = jstorZoteroCreatorMap.get(contrib.getContributionType()) != null
                            ? jstorZoteroCreatorMap.get(contrib.getContributionType())
                            : "contributor";
                    contribNode.put("role", role);
                    ObjectNode personNode = contribNode.putObject("person");
                    fillPerson(contrib, personNode, idx);
                }
                idx++;
            }
        }
    }

    private void createIds(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getArticleIds() == null || article.getArticleMeta().getArticleIds().isEmpty()) {
            return;
        }
        ArrayNode idArray = root.putArray("ids");
        if (article.getArticleMeta().getArticleIds() != null) {
            for (ArticleId id : article.getArticleMeta().getArticleIds()) {
                ObjectNode idNode = idArray.addObject();
                idNode.put("type", id.getPubIdType());
                idNode.put("id", id.getId());
            }
        }

        ArrayNode journalIdArray = root.putArray("journalIds");
        if (article.getContainerMeta().getJournalIds() != null) {
            for (JournalId id : article.getContainerMeta().getJournalIds()) {
                ObjectNode idNode = journalIdArray.addObject();
                idNode.put("type", id.getIdType());
                idNode.put("id", id.getId());
            }
        }
    }

    private void createCategories(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getCategories().isEmpty()) {
            return;
        }

        ArrayNode categoryArray = root.putArray("categories");
        for (ArticleCategoryGroup group : article.getArticleMeta().getCategories()) {
            ObjectNode groupNode = categoryArray.addObject();
            groupNode.put("type", group.getType());
            addCategoryGroups(groupNode, group);
        }
    }

    private void addCategoryGroups(ObjectNode categoriesNode, ArticleCategoryGroup group) {
        ArrayNode categoryArray = categoriesNode.putArray("categories");
        for (ArticleCategory category : group.getCategories()) {
            ObjectNode catNode = categoryArray.addObject();
            catNode.put("name", category.getSubject());
        }

        ArrayNode subGroupArray = categoriesNode.putArray("categoryGroups");
        for (ArticleCategoryGroup subGroup : group.getSubGroups()) {
            ObjectNode groupNode = subGroupArray.addObject();
            groupNode.put("type", subGroup.getType());
            addCategoryGroups(groupNode, subGroup);
        }
    }

    private void createReviewInfo(BibEntry article, ObjectNode root) {
        ReviewInfo info = article.getArticleMeta().getReviewInfo();
        if (info != null) {
            ObjectNode reviewNode = root.putObject("reviewInfo");
            if (info.getContributors() != null) {
                ArrayNode contributorsNode = reviewNode.putArray("contributors");
                int idx = 0;
                for (Contributor contrib : info.getContributors()) {
                    ObjectNode contribNode = contributorsNode.addObject();
                    fillPerson(contrib, contribNode, idx);
                    idx++;
                }
            }

            if (info.getTitle() != null) {
                reviewNode.put("title", info.getTitle());
            }

            if (info.getYear() != null) {
                reviewNode.put("year", info.getYear());
            }

            if (info.getFullDescription() != null) {
                reviewNode.put("fullDescription", info.getFullDescription());
            }
        }
    }

    private void createCopyright(BibEntry article, ObjectNode root) {
        ObjectNode copyrightNode = root.putObject("copyright");
        ArticleMeta meta = article.getArticleMeta();
        if (meta.getCopyrightHolder() != null) {
            copyrightNode.put("holder", meta.getCopyrightHolder());
        }
        if (meta.getCopyrightStatement() != null) {
            copyrightNode.put("statement", meta.getCopyrightStatement());
        }
        if (meta.getCopyrightYear() != null) {
            copyrightNode.put("year", meta.getCopyrightYear());
        }
    }

    private void createCorrespondenceNotes(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getAuthorNotesCorrespondence() != null) {
            root.put("authorCorrespondenceNote", article.getArticleMeta().getAuthorNotesCorrespondence());
        }
    }
    
    private void createJournalAbbreviations(BibEntry article, ObjectNode root) {
        if (article.getContainerMeta().getJournalAbbreviations() != null) {
            ArrayNode abbrevs = root.putArray("journalAbbreviations");
            for (String abbrev : article.getContainerMeta().getJournalAbbreviations()) {
                ObjectNode abbrevNode = abbrevs.addObject();
                if (abbrev.contains(":")) {
                    String type = abbrev.substring(0, abbrev.indexOf(":"));
                    abbrev = abbrev.substring(abbrev.indexOf(":") + 1);
                    abbrevNode.put("type", type);
                }
                abbrevNode.put("abbreviation", abbrev);
            }
        }
    }
    
    private void createPublisher(BibEntry article, ObjectNode root) {
        ObjectNode pubNode = root.putObject("publisher");
        ContainerMeta info = article.getContainerMeta();
        pubNode.put("name", info.getPublisherName() != null ? info.getPublisherName() : "");
        pubNode.put("location", info.getPublisherLocation() != null ? info.getPublisherLocation() : "");
        pubNode.put("address", info.getPublisherAddress() != null ? info.getPublisherAddress() : "");
    }
    
    private void createSeries(BibEntry article, ObjectNode root) {
        ObjectNode pubNode = root.putObject("series");
        ContainerMeta info = article.getContainerMeta();
        pubNode.put("title", info.getSeriesTitle() != null ? info.getSeriesTitle() : "");
        pubNode.put("subtitle", info.getSeriesSubTitle() != null ? info.getSeriesSubTitle() : "");
       
    }
    
    private void createIssns(BibEntry article, ObjectNode root) {
        ArrayNode array = root.putArray("issns");
        ContainerMeta info = article.getContainerMeta();
        if (info.getIssns() != null) {
            for (Issn issn : info.getIssns()) {
                ObjectNode node = array.addObject();
                node.put("type", issn.getPubType() != null ? issn.getPubType() : "");
                node.put("issn", issn.getIssn());
            }
        }
    }

    private void fillPerson(Contributor contrib, ObjectNode creatorNode, int idx) {
        List<String> nameParts = new ArrayList<>();
        if (contrib.getGivenName() != null && !contrib.getGivenName().isEmpty()) {
            nameParts.add(contrib.getGivenName());
        }
        if (contrib.getSurname() != null && !contrib.getSurname().isEmpty()) {
            nameParts.add(contrib.getSurname());
        }
        creatorNode.put("name", String.join(" ", nameParts));
        creatorNode.put("firstName", contrib.getGivenName());
        creatorNode.put("lastName", contrib.getSurname());
        creatorNode.put("positionInList", idx);

        ArrayNode affiliationArray = creatorNode.arrayNode();
        for (Affiliation aff : contrib.getAffiliations()) {
            if (aff.getName() != null && !aff.getName().trim().isEmpty()) {
                ObjectNode affNode = affiliationArray.addObject();
                affNode.put("name", aff.getName());
            }
        }
        creatorNode.set("affiliations", affiliationArray);
    }
}
