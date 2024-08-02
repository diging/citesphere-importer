package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributorId;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Institution;
import edu.asu.diging.crossref.model.IssnType;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.model.Person;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

public class CrossRefIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JobInfo info;

    private Map<String, String> typeMap;

    private CrossrefWorksService crossrefService;

    private Iterator<String> doisIterator;

    public CrossRefIterator(JobInfo info) {
        this.info = info;
        doisIterator = info.getDois().iterator();
        init();
    }

    private void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());
        typeMap = new HashMap<String, String>();
        typeMap.put("journal-article", Publication.ARTICLE);
        typeMap.put("book", Publication.BOOK);
        typeMap.put("book-chapter", Publication.BOOK_CHAPTER); 
        typeMap.put("monograph", Publication.BOOK);
        typeMap.put("journal-issue", Publication.JOURNAL_ISSUE);
        typeMap.put("reference-entry", Publication.REFERNCE_ENTRY);
        typeMap.put("posted-content", Publication.POSTED_CONTENT);
        typeMap.put("component", Publication.COMPONENT);
        typeMap.put("edited-book", Publication.EDITED_BOOK);
        typeMap.put("proceedings-article", Publication.PROCEEDINGS_PAPER);
        typeMap.put("dissertation", Publication.DISSERTATION);
        typeMap.put("book-section", Publication.BOOK_CHAPTER);
        typeMap.put("report-component", Publication.REPORT_COMPONENT);
        typeMap.put("report", Publication.REPORT);
        typeMap.put("peer-review", Publication.PEER_REVIEW);
        typeMap.put("book-track", Publication.BOOK_TRACK);
        typeMap.put("book-part", Publication.BOOK_PART);
        typeMap.put("other", Publication.OTHER);
        typeMap.put("journal-volume", Publication.JORUNAL_VOLUME);
        typeMap.put("book-set", Publication.BOOK_SET);
        typeMap.put("journal", Publication.JOURNAL);
        typeMap.put("proceedings-series", Publication.PROCEEDINGS_SERIES);
        typeMap.put("report-series", Publication.REPORT_SERIES);
        typeMap.put("proceedings", Publication.PROCEEDINGS);
        typeMap.put("database", Publication.DATABASE);
        typeMap.put("standard", Publication.STANDARD);
        typeMap.put("reference-book", Publication.REFERENCE_BOOK);
        typeMap.put("grant", Publication.GRANT);
        typeMap.put("dataset", Publication.DATASET);
        typeMap.put("book-series", Publication.BOOK_SERIES);
    }

    private ContainerMeta parseJournalMeta(Item item) {
        ContainerMeta meta = new ContainerMeta();
        meta.setContainerTitle(String.join(", ", item.getContainerTitle()));
        meta.setPublisherName(item.getPublisher());
        meta.setPublisherLocation(item.getPublisherLocation());
        List<Issn> issnList = new ArrayList<Issn>();
        if(item.getIssnType() != null) {
            for(IssnType issnType : item.getIssnType()) {
                Issn issn = new Issn();
                issn.setIssn(issnType.getValue());
                issn.setPubType(issnType.getType());
                issnList.add(issn);
            }
        }
        meta.setIssns(issnList);
        return meta;
    }

    private ArticleMeta parseArticleMeta(Item item) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleTitle(String.join(", ", item.getTitle()));
        List<Contributor> contributors = new ArrayList<>();
        // List of authors
        if(item.getAuthor() != null) {
            contributors.addAll(mapPersonToContributor(item.getAuthor(), ContributionType.AUTHOR));
        }
        // List of editors
        if(item.getEditor() != null) {
            contributors.addAll(mapPersonToContributor(item.getEditor(), ContributionType.EDITOR));
        }
        // List of translators
        if(item.getTranslator() != null) {
            contributors.addAll(mapPersonToContributor(item.getTranslator(), ContributionType.TRANSLATOR));
        }
        // List of chair
        if(item.getChair() != null) {
            contributors.addAll(mapPersonToContributor(Arrays.asList(item.getChair()), ContributionType.CHAIR));
        }
        meta.setContributors(contributors);
        
        meta.setAuthorNotesCorrespondence(null);
        ArticlePublicationDate publicationDate = new ArticlePublicationDate();
        List<Integer> dateParts = item.getPublished().getIndexedDateParts();
        if(dateParts != null) {
            publicationDate.setPublicationDate(dateParts.get(2).toString());
            publicationDate.setPublicationMonth(dateParts.get(1).toString());
            publicationDate.setPublicationYear(dateParts.get(0).toString());
        }
        meta.setPublicationDate(publicationDate);
        meta.setVolume(item.getVolume());
        meta.setIssue(item.getIssue());
        meta.setPartNumber(item.getPartNumber());
        meta.setFirstPage(item.getPage());
        meta.setSelfUri(item.getUrl());
        meta.setArticleAbstract(item.getAbstractText());
        meta.setLanguage(item.getLanguage());
        ReviewInfo review = new ReviewInfo();
        if (item.getReview() != null) {
            review.setFullDescription(item.getReview().getCompetingInterestStatement());  
        }
        meta.setReviewInfo(review);       
        meta.setDocumentType(item.getType());
        if(item.getReference() != null) {
            meta.setReferences(mapReferences(item.getReference()));
        }
        meta.setReferenceCount(item.getReferenceCount().toString());

        return meta;
    }
    
    private List<Reference> mapReferences(List<edu.asu.diging.crossref.model.Reference> itemReferences) {
        List<Reference> references = new ArrayList<>();
        if(itemReferences != null) {
            for(edu.asu.diging.crossref.model.Reference itemRef: itemReferences) { 
                Reference ref = new Reference();
                ref.setAuthorString(itemRef.getAuthor());
                ref.setContributors(null);
                ref.setTitle(itemRef.getArticleTitle());
                ref.setYear(itemRef.getYear());
                if(itemRef.getDoi()!=null && !itemRef.getDoi().isBlank()) {
                    ref.setIdentifier(itemRef.getDoi());
                    ref.setIdentifierType("DOI");
                    ref.setSource(itemRef.getDoiAssertedBy());
                } else if (itemRef.getIssn()!=null && !itemRef.getIssn().isBlank()) {
                    ref.setIdentifier(itemRef.getIssn());
                    ref.setIdentifierType("ISSN");
                } else if (itemRef.getIsbn()!=null && !itemRef.getIsbn().isBlank()) {
                    ref.setIdentifier(itemRef.getIsbn());
                    ref.setIdentifierType("ISBN");
                }
                ref.setFirstPage(itemRef.getFirstPage());
                ref.setVolume(itemRef.getVolume());
                ref.setReferenceId(itemRef.getKey());
                ref.setReferenceString(itemRef.getUnstructured());
                ref.setReferenceStringRaw(itemRef.getUnstructured());
                references.add(ref);
            }
        }
        return references;
    }

    private List<Contributor> mapPersonToContributor(List<Person> personList, String contributionType) {
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(Person person: personList) {
            Contributor contributor = new Contributor();
            contributor.setContributionType(contributionType);
            contributor.setGivenName(person.getGiven());
            contributor.setSurname(person.getFamily());
            contributor.setFullName(person.getName());
            List<Affiliation> affiliations = new ArrayList<>();
            for(Institution institute: person.getAffiliation()) {
                Affiliation affiliation = new Affiliation();
                affiliation.setName(institute.getName());
                affiliations.add(affiliation);
            }
            contributor.setAffiliations(affiliations);
            ContributorId contributorID = new ContributorId();
            contributorID.setId(person.getOrcid());
            contributorID.setIdSystem("ORCID");
            contributor.setIds(Arrays.asList(contributorID));
            contributors.add(contributor); 
        }
        return contributors;
    }

    @Override
    public BibEntry next() {
        if (!doisIterator.hasNext()) {
            return null;
        }
        BibEntry nextEntry = new Publication();;
        
        try {
            Item item = crossrefService.get(doisIterator.next());
            nextEntry.setArticleType(typeMap.get(item.getType())); 
            nextEntry.setJournalMeta(parseJournalMeta(item));
            nextEntry.setArticleMeta(parseArticleMeta(item));
        } catch (RequestFailedException | IOException e) {
            logger.error("Could not retrieve work for doi: "+ doisIterator.next(), e);
            // for now we just log the exceptions
            // we might want to devise a way to decide if the 
            // service might be down and we should stop sending requests.
        }
        
        return nextEntry;
    }


    @Override
    public boolean hasNext() {
        return doisIterator.hasNext();
    }

    @Override
    public void close() {
        // do nothing
    }

}
