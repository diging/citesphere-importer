package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Data;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.Library;

import edu.asu.diging.citesphere.factory.impl.CitationFactory;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.model.bib.IAffiliation;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IPerson;
import edu.asu.diging.citesphere.model.bib.IReference;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CitationFactory citationFactory;

    private String filePath;
    private String groupId;
    private Iterator<String> lineIterator;
    private String currentLine = null;
    private Map<String, String> typeMap;

    public BibFileIterator(String filePath, String groupId) {
        this.filePath = filePath;
        this.groupId = groupId;
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
        Item item = new Item();
        Library lib = new Library();
        lib.setId(Long.parseLong(groupId));
        item.setLibrary(lib);
        Data itemData = new Data();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next().trim();
            if(line.contains("@")) {
                entry.setArticleType(typeMap.get(line.substring(line.indexOf('@')+1, line.indexOf('{'))));
                itemData.setItemType(typeMap.get(line.substring(line.indexOf('@')+1, line.indexOf('{'))));
            } else if (line.equals("}")) {
                //                entry.setJournalMeta(parseJournalMeta(fields));
                //                entry.setArticleMeta(parseArticleMeta(fields));
                itemData = parseItemData(itemData, fields);
                item.setData(itemData);
                ICitation citation = citationFactory.createCitation(item);
                // convert citation to bibentry
                entry.setJournalMeta(parseJournalMeta(citation, fields));
                entry.setArticleMeta(parseArticleMeta(citation, fields));
                //                System.out.println("====================== entry - " + entry.toString());
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

    private Data parseItemData(Data data, Map<String, String> fields) {
        //        linkMode;  accessDate contentType charset filename md5 mtime dateAdded dateModified
        //        creators  publicationTitle  seriesTitle  seriesText archive
        //        archiveLocation libraryCatalog  callNumber  rights extra tags collections
        data.setTitle(fields.get("title"));
        data.setPublicationTitle(fields.get("publisher"));
        data.setUrl(fields.get("url"));
        data.setNote(fields.get("note"));
        data.setAbstractNote(fields.get("abstract"));
        data.setVolume(fields.get("volume"));
        data.setPages(fields.get("pages"));
        data.setSeries(fields.get("series"));
        data.setJournalAbbreviation(fields.get("journal"));
        data.setLanguage(fields.get("language"));
        data.setDoi(fields.get("doi"));
        data.setIssn(fields.get("issn"));
        data.setShortTitle(fields.get("shorttitle"));
        data.setDate(fields.get("year")+"-01-01T00:00Z");
        data.setIssue(fields.get("number"));

        return data;
    }

    private ContainerMeta parseJournalMeta(ICitation citation, Map<String, String> fields) {
        ContainerMeta meta = new ContainerMeta();
        //        journalIds
        //        meta.setContainerTitle(fields.get("title"));
        List<String> journalAbbrev = new ArrayList<>();
        journalAbbrev.add(citation.getJournalAbbreviation());
        meta.setJournalAbbreviations(journalAbbrev);
        meta.setPublisherName(citation.getPublicationTitle());
        meta.setPublisherLocation(fields.get("place"));
        //        publisherAddress
        List<Issn> issnList = new ArrayList<Issn>();
        if(fields.get("issn") != null) {
            for(String issnString : fields.get("issn").split("and")) {
                Issn issn = new Issn();
                issn.setIssn(issnString.trim());
                issnList.add(issn);
            }
        }
        meta.setIssns(issnList);
        meta.setSeriesTitle(citation.getSeriesTitle());
        //        private String seriesSubTitle;
        return meta;
    }

    private ArticleMeta parseArticleMeta(ICitation citation, Map<String, String> fields) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleTitle(citation.getTitle());
        meta.setArticleShortTitle(citation.getShortTitle());
        //        categoryGroups
        List<Contributor> contributors = new ArrayList<>();
        // List of authors
        if(citation.getAuthors() != null) {
            contributors.addAll(mapPersonToContributor(citation.getAuthors(), ContributionType.AUTHOR));
            //            System.out.println(fields.get("author")+ "========================== authors");
        }
        // List of editors
        //                if(fields.get("editor") != null) {
        //                    contributors.addAll(mapPersonToContributor(fields.get("editor"), ContributionType.EDITOR));
        //                }
        //                meta.setContributors(contributors);
        //        meta.setAuthorNotesCorrespondence(null);
        ArticlePublicationDate publicationDate = new ArticlePublicationDate();
        //                if(citation != null) {
        //                    publicationDate.setPublicationDate(dateParts.get(2).toString());
        //                    publicationDate.setPublicationMonth(dateParts.get(1).toString());
        publicationDate.setPublicationYear(citation.getDateFreetext());
        //        }
        meta.setPublicationDate(publicationDate);
        meta.setVolume(citation.getVolume());
        meta.setIssue(citation.getIssue());
        meta.setFirstPage(citation.getPages().split("--")[0].trim());
        meta.setLastPage(citation.getPages().split("--")[1].trim());
        meta.setSelfUri(citation.getUrl());
        meta.setArticleAbstract(citation.getAbstractNote());
        meta.setLanguage(citation.getLanguage());
        //        ReviewInfo review = new ReviewInfo();
        //        if (item.getReview() != null) {
        //            review.setFullDescription(item.getReview().getCompetingInterestStatement());  
        //        }
        //        meta.setReviewInfo(review);       
        //        meta.setDocumentType(fields.get("type"));   // document type in note
        //        conferenceInfo in note - map to conferenceTitle, conferenceDate, conferenceLocation, conferenceSponsor, conferenceHost
        //        keywords in note
        //        reprintAddress in note
        // additionalData in note
        //         funding in note - map to fundingInfo, fundingText


        if(citation.getReferences() != null) {
            meta.setReferences(mapReferences(citation.getReferences()));
        }
        meta.setReferenceCount(meta.getReferences().size()+"");

        //        retrievalDate in note
        return meta;
    }

    private List<Contributor> mapPersonToContributor(Set<IPerson> citationContributors, String contributionType) {
        //        String[] contributorStringList = contributorsString.split("and");
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(IPerson person: citationContributors) {
            Contributor contributor = new Contributor();
            contributor.setContributionType(contributionType);
            contributor.setGivenName(person.getFirstName());
            contributor.setSurname(person.getLastName());
            contributor.setFullName(person.getName());

            List<Affiliation> affiliations = new ArrayList<>();
            for(IAffiliation institute: person.getAffiliations()) {                
                Affiliation affiliation = new Affiliation();
                affiliation.setName(institute.getName());
                affiliation.setUri(institute.getUri());
                affiliation.setLocalAuthorityId(institute.getLocalAuthorityId());
                affiliations.add(affiliation);
            }
            contributor.setAffiliations(affiliations);
            //            ContributorId contributorID = new ContributorId();
            //            contributorID.setId(person.getOrcid());
            //            contributorID.setIdSystem("ORCID");
            //            contributor.setIds(Arrays.asList(contributorID));
            contributors.add(contributor); 
        }
        return contributors;
    }

    private List<Reference> mapReferences(Set<IReference> citationReferences) {
        List<Reference> references = new ArrayList<Reference>();
        for(IReference citationRef: citationReferences) {
            references.add(mapSingleReference(citationRef));
        }

        return references;
    }

    private Reference mapSingleReference(IReference citationRef) {
        Reference ref = new Reference();
        ref.setAuthorString(citationRef.getAuthorString());
        ref.setTitle(citationRef.getTitle());
        ref.setYear(citationRef.getYear());
        ref.setIdentifier(citationRef.getIdentifier());
        ref.setIdentifierType(citationRef.getIdentifierType());
        ref.setFirstPage(citationRef.getFirstPage());
        ref.setEndPage(citationRef.getEndPage());
        ref.setVolume(citationRef.getVolume());
        ref.setSource(citationRef.getSource());
        ref.setReferenceId(citationRef.getReferenceId());
        ref.setReferenceLabel(citationRef.getReferenceLabel());
        ref.setPublicationType(citationRef.getPublicationType());
        ref.setCitationId(citationRef.getCitationId());
        ref.setReferenceString(citationRef.getReferenceString());
        ref.setReferenceStringRaw(citationRef.getReferenceStringRaw());

        return ref;
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
