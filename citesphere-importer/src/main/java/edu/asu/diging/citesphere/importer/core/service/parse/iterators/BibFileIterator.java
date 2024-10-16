package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.javers.common.collections.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String filePath;
    private Iterator<String> lineIterator;
    private String currentLine = null;
    private Map<String, String> typeMap;

    public BibFileIterator(String filePath) {
        this.filePath = filePath;
        init();
    }

    private void init() {
        try {
            lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }
        typeMap = new HashMap<String, String>();
        typeMap.put("journal-article", Publication.ARTICLE);
        typeMap.put("book", Publication.BOOK);
        typeMap.put("book-chapter", Publication.BOOK_CHAPTER); 
        typeMap.put("monograph", Publication.BOOK);
        //        typeMap.put("journal-issue", Publication.JOURNAL_ISSUE);
        //        typeMap.put("reference-entry", Publication.REFERNCE_ENTRY);
        //        typeMap.put("posted-content", Publication.POSTED_CONTENT);
        //        typeMap.put("component", Publication.COMPONENT);
        //        typeMap.put("edited-book", Publication.EDITED_BOOK);
        typeMap.put("proceedings-article", Publication.PROCEEDINGS_PAPER);
        //        typeMap.put("dissertation", Publication.DISSERTATION);
        typeMap.put("book-section", Publication.BOOK_CHAPTER);
        //        typeMap.put("report-component", Publication.REPORT_COMPONENT);
        //        typeMap.put("report", Publication.REPORT);
        //        typeMap.put("peer-review", Publication.PEER_REVIEW);
        //        typeMap.put("book-track", Publication.BOOK_TRACK);
        //        typeMap.put("book-part", Publication.BOOK_PART);
        //        typeMap.put("other", Publication.OTHER);
        //        typeMap.put("journal-volume", Publication.JORUNAL_VOLUME);
        //        typeMap.put("book-set", Publication.BOOK_SET);
        //        typeMap.put("journal", Publication.JOURNAL);
        //        typeMap.put("proceedings-series", Publication.PROCEEDINGS_SERIES);
        //        typeMap.put("report-series", Publication.REPORT_SERIES);
        //        typeMap.put("proceedings", Publication.PROCEEDINGS);
        //        typeMap.put("database", Publication.DATABASE);
        //        typeMap.put("standard", Publication.STANDARD);
        //        typeMap.put("reference-book", Publication.REFERENCE_BOOK);
        //        typeMap.put("grant", Publication.GRANT);
        //        typeMap.put("dataset", Publication.DATASET);
        //        typeMap.put("book-series", Publication.BOOK_SERIES);
    }

    @Override
    public BibEntry next() {
        BibEntry entry = new Publication();
        Map<String, String> fields = new HashMap<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next().trim();
            if(line.contains("@")) {
                entry.setArticleType(typeMap.get(line.substring(line.indexOf('@')+1, line.indexOf('{'))));
            } else if (line.equals("}")) {
                entry.setJournalMeta(parseJournalMeta(fields));
                entry.setArticleMeta(parseArticleMeta(fields));
                System.out.println("====================== entry - " + entry.toString());
                fields.clear();
                break;
            } else if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim().replaceAll("[{},]", ""); // Remove curly braces and commas
                    fields.put(key, value);
                }
            }
        }
        return entry;
    }

    private ContainerMeta parseJournalMeta(Map<String, String> fields) {
        ContainerMeta meta = new ContainerMeta();
//        journalIds
        //        meta.setContainerTitle(fields.get("title"));
        List<String> journalAbbrev = new ArrayList<>();
        journalAbbrev.add(fields.get("journal"));
        meta.setJournalAbbreviations(journalAbbrev);
        meta.setPublisherName(fields.get("publisher"));
        meta.setPublisherLocation(fields.get("place"));
//        publisherAddress
        List<Issn> issnList = new ArrayList<Issn>();
        if(fields.get("issn") != null) {
            String issnListString = fields.get("issn");
            for(String issnString : issnListString.split("and")) {
                Issn issn = new Issn();
                issn.setIssn(issnString.trim());
                issnList.add(issn);
            }
        }
        meta.setIssns(issnList);
        meta.setSeriesTitle(fields.get("series"));
//        private String seriesSubTitle;
        return meta;
    }

    private ArticleMeta parseArticleMeta(Map<String, String> fields) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleTitle(fields.get("title"));
        //        meta.setShortTitle(fields.get("shortTitle"));
        List<Contributor> contributors = new ArrayList<>();
        // List of authors
        if(fields.get("author") != null) {
            contributors.addAll(mapPersonToContributor(fields.get("author"), ContributionType.AUTHOR));
            //            System.out.println(fields.get("author")+ "========================== authors");
        }
        // List of editors
                if(fields.get("editor") != null) {
                    contributors.addAll(mapPersonToContributor(fields.get("editor"), ContributionType.EDITOR));
                }
                meta.setContributors(contributors);
        //        meta.setAuthorNotesCorrespondence(null);
                ArticlePublicationDate publicationDate = new ArticlePublicationDate();
        //        if(dateParts != null) {
        //            publicationDate.setPublicationDate(dateParts.get(2).toString());
        //            publicationDate.setPublicationMonth(dateParts.get(1).toString());
                    publicationDate.setPublicationYear(fields.get("year").trim());
        //        }
                meta.setPublicationDate(publicationDate);
        meta.setVolume(fields.get("volume"));
        meta.setIssue(fields.get("issue"));   // no eg
        meta.setIssueId(fields.get("issueId"));  //no eg
        meta.setPartNumber(fields.get("partNumber"));  // no eg
        meta.setFirstPage(fields.get("pages").split("--")[0].trim());
        meta.setLastPage(fields.get("pages").split("--")[1].trim());
        meta.setSelfUri(fields.get("uri"));
        meta.setArticleAbstract(fields.get("abstract"));
        meta.setLanguage(fields.get("language"));
        //        ReviewInfo review = new ReviewInfo();
        //        if (item.getReview() != null) {
        //            review.setFullDescription(item.getReview().getCompetingInterestStatement());  
        //        }
        //        meta.setReviewInfo(review);       
        meta.setDocumentType(fields.get("type"));
        //        if(item.getReference() != null) {
        //            meta.setReferences(mapReferences(item.getReference()));
        //        }
        //        meta.setReferenceCount(item.getReferenceCount().toString());

        return meta;
    }
    
    private List<Contributor> mapPersonToContributor(String contributorsString, String contributionType) {
        String[] contributorStringList = contributorsString.split("and");
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(String personStr: contributorStringList) {
            Contributor contributor = new Contributor();
            String[] parts = personStr.split(" ");
            contributor.setContributionType(contributionType);
            contributor.setGivenName(parts[1].trim());
            contributor.setSurname(parts[0].trim());
            contributor.setFullName(parts[1].trim()+" "+parts[0].trim());
//            List<Affiliation> affiliations = new ArrayList<>();
//            for(Institution institute: person.getAffiliation()) {
//                Affiliation affiliation = new Affiliation();
//                affiliation.setName(institute.getName());
//                affiliations.add(affiliation);
//            }
//            contributor.setAffiliations(affiliations);
//            ContributorId contributorID = new ContributorId();
//            contributorID.setId(person.getOrcid());
//            contributorID.setIdSystem("ORCID");
//            contributor.setIds(Arrays.asList(contributorID));
            contributors.add(contributor); 
        }
        return contributors;
    }

    @Override
    public boolean hasNext() {
        return lineIterator.hasNext();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
