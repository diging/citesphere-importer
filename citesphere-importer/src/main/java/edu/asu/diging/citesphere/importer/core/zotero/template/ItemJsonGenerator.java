package edu.asu.diging.citesphere.importer.core.zotero.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.zotero.template.impl.ExtraFieldHelper;
import edu.asu.diging.citesphere.importer.core.zotero.template.impl.ZoteroCreatorTypes;

public abstract class ItemJsonGenerator {

    @Autowired
    protected ExtraFieldHelper fieldHelper;
   
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    protected Map<String, Method> methods;
    protected ObjectMapper mapper;
    
    /**
     * Maps jstor contributor types to zotero creator types
     */
    protected Map<String, String> jstorZoteroCreatorMap;
    protected Map<String, String> containerContributorsZoteroMap;

    
    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
        methods = new HashMap<>();
        
        Method[] declaredMethods = getClass().getMethods();
        for (Method method : declaredMethods) {
            methods.put(method.getName(), method);
        }
        
        jstorZoteroCreatorMap = fieldHelper.getJstorZoteroCreatorMap();
        containerContributorsZoteroMap = fieldHelper.getContainerContributorsZoteroMap();
    }

    public abstract String responsibleFor();

    public ObjectNode generate(JsonNode node, BibEntry bibEntry) {
        ObjectMapper mapper = new ObjectMapper();
        
        ObjectNode bibNode = mapper.createObjectNode();
        Iterator<Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Object result = null;
            Entry<String, JsonNode> entry = fields.next();
            Method processMethod = methods.get("process" + StringUtils.capitalize(entry.getKey()));
            if (processMethod == null) {
                logger.debug("No such method: " + "process" + StringUtils.capitalize(entry.getKey()));
                continue;
            }
            try {
                result = processMethod.invoke(this, entry.getValue(), bibEntry);
            } catch (SecurityException e) {
                logger.warn("There is no method or method is not callable to process field: " + entry.getKey(), e);
            } catch (IllegalAccessException e) {
                logger.warn("Could not call processing method.", e);
            } catch (IllegalArgumentException e) {
                logger.warn("Method does not take provided argument.", e);
            } catch (InvocationTargetException e) {
                logger.warn("An error occurred when calling processing method.", e);
            }
            
            if (result != null) {
                bibNode.putPOJO(entry.getKey(), result);
            }
        }
        return bibNode;
    }
    
    public String processItemType(JsonNode node, BibEntry bibEntry) {
        return node.asText();
    }
    
    public ArrayNode processTags(JsonNode node, BibEntry bibEntry) {
        return mapper.createArrayNode();
    }
    
    public JsonNode processRelations(JsonNode node, BibEntry bibEntry) {
        return mapper.createObjectNode();
    }
    
    public JsonNode processCollections(JsonNode node, BibEntry bibEntry) {
        return mapper.createArrayNode();
    }
    
    protected ObjectMapper getObjectMapper() {
        return mapper;
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
    
    public String processAbstractNote(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getArticleAbstract();
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
    
    public String processVolume(JsonNode node, BibEntry article) {
        return article.getArticleMeta().getVolume();
    }
    
    public String processPublisher(JsonNode node, BibEntry article) {
        String name = article.getContainerMeta().getPublisherName();
        if (name != null && !name.trim().isEmpty())  {
            return name;
        }
        return "";
    }
    
    public String processPlace(JsonNode node, BibEntry article) {
        String location = article.getContainerMeta().getPublisherLocation();
        if (location != null && !location.trim().isEmpty()) {
            return location;
        }
        return "";
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
    
    protected void fillContributorName(Contributor contributor, ObjectNode contributorNode) {
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
}
