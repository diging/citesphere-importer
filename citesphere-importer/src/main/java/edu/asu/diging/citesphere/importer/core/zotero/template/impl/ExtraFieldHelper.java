package edu.asu.diging.citesphere.importer.core.zotero.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.AdditionalData;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategory;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleCategoryGroup;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleId;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributorId;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.JournalId;
import edu.asu.diging.citesphere.importer.core.model.impl.Keyword;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;

@Component
public class ExtraFieldHelper {

    private Map<String, String> jstorZoteroCreatorMap;
    private Map<String, String> containerContributorsZoteroMap;

    @PostConstruct
    public void init() {
        jstorZoteroCreatorMap = new HashMap<>();
        jstorZoteroCreatorMap.put("author", ZoteroCreatorTypes.AUTHOR);
        jstorZoteroCreatorMap.put("editor", ZoteroCreatorTypes.EDITOR);

        containerContributorsZoteroMap = new HashMap<>();
        containerContributorsZoteroMap.put(ContributionType.AUTHOR, ZoteroCreatorTypes.BOOK_AUTHOR);
        containerContributorsZoteroMap.put(ContributionType.EDITOR, ZoteroCreatorTypes.EDITOR);
    }

    public Map<String, String> getJstorZoteroCreatorMap() {
        return jstorZoteroCreatorMap;
    }

    public Map<String, String> getContainerContributorsZoteroMap() {
        return containerContributorsZoteroMap;
    }

    public void createCreatorsExtra(BibEntry article, ObjectNode root) {
        ArrayNode authors = root.putArray("authors");
        ArrayNode editors = root.putArray("editors");
        ArrayNode others = root.putArray("otherCreators");

        int idx = 0;
        if (article.getArticleMeta().getContributors() != null) {
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

        if (article.getContainerMeta().getContributors() != null) {
            for (Contributor contrib : article.getContainerMeta().getContributors()) {
                ObjectNode contribNode = null;
                String role = contrib.getContributionType();
                if (role.equals("author")) {
                    role = ZoteroCreatorTypes.BOOK_AUTHOR;
                } else if (jstorZoteroCreatorMap.get(contrib.getContributionType()) != null) {
                    role = jstorZoteroCreatorMap.get(contrib.getContributionType());
                }
                contribNode = others.addObject();
                contribNode.put("role", role);
                ObjectNode personNode = contribNode.putObject("person");
                fillPerson(contrib, personNode, idx);
                idx++;
            }
        }
    }

    public void createIds(BibEntry article, ObjectNode root) {
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

    public void createCategories(BibEntry article, ObjectNode root) {
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

    public void addCategoryGroups(ObjectNode categoriesNode, ArticleCategoryGroup group) {
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

    public void createReviewInfo(BibEntry article, ObjectNode root) {
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

    public void createCopyright(BibEntry article, ObjectNode root) {
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

    public void createCorrespondenceNotes(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getAuthorNotesCorrespondence() != null) {
            root.put("authorCorrespondenceNote", article.getArticleMeta().getAuthorNotesCorrespondence());
        }
    }

    public void createIssueId(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getIssueId() != null) {
            root.put("issueId", article.getArticleMeta().getIssueId());
        }
    }

    public void createSpecialIssue(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getSpecialIssue() != null) {
            root.put("specialIssue", article.getArticleMeta().getSpecialIssue());
        }
    }

    public void createPartNumber(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getPartNumber() != null) {
            root.put("partNumber", article.getArticleMeta().getPartNumber());
        }
    }

    public void createSupplement(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getSupplement() != null) {
            root.put("supplement", article.getArticleMeta().getSupplement());
        }
    }

    public void createPageCount(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getPageCount() != null) {
            root.put("pageCount", article.getArticleMeta().getPageCount());
        }
    }

    public void createChapterCount(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getChapterCount() != null) {
            root.put("chapterCount", article.getArticleMeta().getChapterCount());
        }
    }

    public void createDocumentType(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getDocumentType() != null) {
            root.put("documentType", article.getArticleMeta().getDocumentType());
        }
    }

    public void createConferenceInfo(BibEntry article, ObjectNode root) {
        ObjectNode conference = root.putObject("conferenceInfo");
        if (article.getArticleMeta().getConferenceTitle() != null) {
            conference.put("title", article.getArticleMeta().getConferenceTitle());
        }
        if (article.getArticleMeta().getConferenceDate() != null) {
            conference.put("date", article.getArticleMeta().getConferenceDate());
        }
        if (article.getArticleMeta().getConferenceHost() != null) {
            conference.put("host", article.getArticleMeta().getConferenceHost());
        }
        if (article.getArticleMeta().getConferenceLocation() != null) {
            conference.put("location", article.getArticleMeta().getConferenceLocation());
        }
        if (article.getArticleMeta().getConferenceSponsor() != null) {
            conference.put("sponsor", article.getArticleMeta().getConferenceSponsor());
        }
    }

    public void createKeywords(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getKeywords() != null && !article.getArticleMeta().getKeywords().isEmpty()) {
            ArrayNode keywords = root.putArray("keywords");
            for (Keyword keyword : article.getArticleMeta().getKeywords()) {
                ObjectNode keywordNode = keywords.addObject();
                keywordNode.put("keyword", keyword.getKeyword());
                keywordNode.put("source", keyword.getCreator());
            }
        }
    }

    public void createJournalAbbreviations(BibEntry article, ObjectNode root) {
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

    public void createPublisher(BibEntry article, ObjectNode root) {
        ObjectNode pubNode = root.putObject("publisher");
        ContainerMeta info = article.getContainerMeta();
        pubNode.put("name", info.getPublisherName() != null ? info.getPublisherName() : "");
        pubNode.put("location", info.getPublisherLocation() != null ? info.getPublisherLocation() : "");
        pubNode.put("address", info.getPublisherAddress() != null ? info.getPublisherAddress() : "");
    }

    public void createSeries(BibEntry article, ObjectNode root) {
        ObjectNode pubNode = root.putObject("series");
        ContainerMeta info = article.getContainerMeta();
        pubNode.put("title", info.getSeriesTitle() != null ? info.getSeriesTitle() : "");
        pubNode.put("subtitle", info.getSeriesSubTitle() != null ? info.getSeriesSubTitle() : "");

    }

    public void createIssns(BibEntry article, ObjectNode root) {
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
    
    public void createReprintAddress(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getReprintAddress() != null) {
            root.put("reprintAddress", article.getArticleMeta().getReprintAddress());
        }
    }
    
    public void createAdditionalData(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getAdditionalData() != null) {
            ArrayNode array = root.putArray("additionalData");
            for (AdditionalData data : article.getArticleMeta().getAdditionalData()) {
                ObjectNode node = array.addObject();
                node.put("type", data.getFieldname());
                node.put("data", data.getValue());
            }
        }
    }
    
    public void createUnassignedContributorIds(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getUnassignedIds() != null) {
            ArrayNode array = root.putArray("unassignedContributorIds");
            for (ContributorId id : article.getArticleMeta().getUnassignedIds()) {
                ObjectNode node = array.addObject();
                node.put("id", id.getId());
                node.put("system", id.getIdSystem());
            }
        }
    }
    
    public void createFundingInfo(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getFundingInfo() != null) {
            ObjectNode funding = root.putObject("funding");
            funding.put("info", article.getArticleMeta().getFundingInfo());
            funding.put("text", article.getArticleMeta().getFundingText());
        }
    }
    
    public void createReferences(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getReferences() != null) {
            ArrayNode references = root.putArray("references");
            for (Reference ref : article.getArticleMeta().getReferences()) {
                ObjectNode refNode = references.addObject();
                if (ref.getAuthorString() != null) {
                    refNode.put("authorString", ref.getAuthorString());
                }
                if (ref.getContributors() != null) {
                    ArrayNode contributors = refNode.putArray("contributors");
                    int idx = 0;
                    for (Contributor contrib : ref.getContributors()) {
                        ObjectNode contribNode = contributors.addObject();
                        fillPerson(contrib, contribNode, idx);
                        idx++;
                    }
                }
                if (ref.getTitle() != null) {
                    refNode.put("title", ref.getTitle());
                }
                if (ref.getEndPage() != null) {
                    refNode.put("endPage", ref.getEndPage());
                }
                if (ref.getFirstPage() != null) {
                    refNode.put("firstPage", ref.getFirstPage());
                }
                if (ref.getIdentifier() != null) {
                    refNode.put("identifier", ref.getIdentifier());
                }
                if (ref.getIdentifierType() != null) {
                    refNode.put("identifierType", ref.getIdentifierType());
                }
                if (ref.getReferenceString() != null) {
                    refNode.put("referenceString", ref.getReferenceString());
                }
                if (ref.getReferenceStringRaw() != null) {
                    refNode.put("referenceStringRaw", ref.getReferenceStringRaw());
                }
                if (ref.getSource() != null) {
                    refNode.put("source", ref.getSource());
                }
                if (ref.getVolume() != null) {
                    refNode.put("volume", ref.getVolume());
                }
                if (ref.getYear() != null) {
                    refNode.put("year", ref.getYear());
                }
                if (ref.getPublicationType() != null) {
                    refNode.put("publicationType", ref.getPublicationType());
                }
                if (ref.getCitationId()!= null) {
                    refNode.put("citationId", ref.getCitationId());
                }
                if (ref.getReferenceId() != null) {
                    refNode.put("referenceId", ref.getReferenceId());
                }
                if (ref.getReferenceLabel() != null) {
                    refNode.put("referenceLabel", ref.getReferenceLabel());
                }
            }
        }
    }
    
    public void createReferenceCount(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getReferenceCount() != null) {
            root.put("referenceCount", article.getArticleMeta().getReferenceCount());
        }
    }
    
    public void createRetrievalDate(BibEntry article, ObjectNode root) {
        if (article.getArticleMeta().getRetrievalDate() != null) {
            root.put("retrievalDate", article.getArticleMeta().getRetrievalDate());
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
