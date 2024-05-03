package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import edu.asu.diging.citesphere.importer.core.model.impl.CrossRefPublication;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Institution;
import edu.asu.diging.crossref.model.IssnType;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.model.Person;
import edu.asu.diging.crossref.model.Review;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

public class CrossRefIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JobInfo info;
    private List<BibEntry> articles;
    private BibEntry article;
    private int currentIndex;

    private boolean iteratorDone = false;
    private Map<String, String> typeMap;

    private CrossrefWorksService crossrefService;



    public CrossRefIterator(JobInfo info) {
        this.info = info;
        currentIndex = 0;
        init();
    }

    private void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());
        typeMap = new HashMap<String, String>();
        typeMap.put("journal-article", CrossRefPublication.ARTICLE);
        typeMap.put("book", CrossRefPublication.BOOK);
        typeMap.put("book-chapter", CrossRefPublication.BOOK_CHAPTER); 
        typeMap.put("monograph", CrossRefPublication.MONOGRAPH);
        typeMap.put("journal-issue", CrossRefPublication.JOURNAL_ISSUE);
        typeMap.put("reference-entry", CrossRefPublication.REFERNCE_ENTRY);
        typeMap.put("posted-content", CrossRefPublication.POSTED_CONTENT);
        typeMap.put("component", CrossRefPublication.COMPONENT);
        typeMap.put("edited-book", CrossRefPublication.EDITED_BOOK);
        typeMap.put("proceedings-article", CrossRefPublication.PROCEEDINGS_ARTICLE);
        typeMap.put("dissertation", CrossRefPublication.DISSERTATION);
        typeMap.put("book-section", CrossRefPublication.BOOK_SECTION);
        typeMap.put("report-component", CrossRefPublication.REPORT_COMPONENT);
        typeMap.put("report", CrossRefPublication.REPORT);
        typeMap.put("peer-review", CrossRefPublication.PEER_REVIEW);
        typeMap.put("book-track", CrossRefPublication.BOOK_TRACK);
        typeMap.put("book-part", CrossRefPublication.BOOK_PART);
        typeMap.put("other", CrossRefPublication.OTHER);
        typeMap.put("journal-volume", CrossRefPublication.JORUNAL_VOLUME);
        typeMap.put("book-set", CrossRefPublication.BOOK_SET);
        typeMap.put("journal", CrossRefPublication.JOURNAL);
        typeMap.put("proceedings-series", CrossRefPublication.PROCEEDINGS_SERIES);
        typeMap.put("report-series", CrossRefPublication.REPORT_SERIES);
        typeMap.put("proceedings", CrossRefPublication.PROCEEDINGS);
        typeMap.put("database", CrossRefPublication.DATABASE);
        typeMap.put("standard", CrossRefPublication.STANDARD);
        typeMap.put("reference-book", CrossRefPublication.REFERENCE_BOOK);
        typeMap.put("grant", CrossRefPublication.GRANT);
        typeMap.put("dataset", CrossRefPublication.DATASET);
        typeMap.put("book-series", CrossRefPublication.BOOK_SERIES);
        parseCrossRef();

    }

    private void parseCrossRef() {

        articles = new ArrayList<>();
        for (String doi : info.getDois()) {
            try {
                Item item = crossrefService.get(doi);
                article = new CrossRefPublication();
                article.setArticleType(typeMap.get(item.getType())); 
                article.setJournalMeta(parseJournalMeta(item));
                article.setArticleMeta(parseArticleMeta(item));

                articles.add(article);
            } catch (RequestFailedException | IOException e) {
                logger.error("Couuld not retrieve work for doi: "+ doi, e);
                // for now we just log the exceptions
                // we might want to devise a way to decide if the 
                // service might be down and we should stop sending requests.
            }
        }
    }

    private ContainerMeta parseJournalMeta(Item item) {
        ContainerMeta meta = new ContainerMeta();
        meta.setContainerTitle(String.join(", ", item.getContainerTitle()));
        meta.setPublisherName(item.getPublisher());
        meta.setPublisherLocation(item.getPublisherLocation());
        List<Issn> issnList = new ArrayList<Issn>();
        for(IssnType issnType : item.getIssnType()) {
            Issn issn = new Issn();
            issn.setIssn(issnType.getValue());
            issn.setPubType(issnType.getType());
            issnList.add(issn);
        }
        meta.setIssns(issnList);

        List<Contributor> contributors = new ArrayList<>();
        if(item.getChair() != null) {
            Person itemChair = item.getChair();
            Contributor chair = new Contributor();
            chair.setContributionType(ContributionType.CHAIR);
            chair.setGivenName(itemChair.getGiven());
            chair.setSurname(itemChair.getFamily());
            chair.setFullName(itemChair.getName());
            List<Affiliation> affiliations = new ArrayList<>();
            for(Institution institute: itemChair.getAffiliation()) {
                Affiliation affiliation = new Affiliation();
                affiliation.setName(institute.getName());
                affiliations.add(affiliation);
            }
            chair.setAffiliations(affiliations);
            ContributorId contributorID = new ContributorId();
            contributorID.setId(itemChair.getOrcid());
            contributorID.setIdSystem("ORCID");
            chair.setIds(Arrays.asList(contributorID));
            contributors.add(chair);
        }
        // added Editors & translators to article meta. 

        return meta;
    }

    private ArticleMeta parseArticleMeta(Item item) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleTitle(String.join(", ", item.getTitle()));
        List<Contributor> contributors = new ArrayList<>();
        // List of authors
        if(item.getAuthor() != null) {
            contributors.addAll(mapPersonToContributor(item.getAuthor()));
        }
        // List of editors
        if(item.getEditor() != null) {
            contributors.addAll(mapPersonToContributor(item.getEditor()));
        }
        // List of translators
        if(item.getTranslator() != null) {
            contributors.addAll(mapPersonToContributor(item.getTranslator()));
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

        List<Reference> references = new ArrayList<>();
        if(item.getReference() != null) {
            for(edu.asu.diging.crossref.model.Reference itemRef: item.getReference()) { 
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
        meta.setReferences(references);
        meta.setReferenceCount(item.getReferenceCount().toString());

        return meta;
    }

    public List<Contributor> mapPersonToContributor(List<Person> personList) {
        List<Contributor> contributors = new ArrayList<Contributor>();
        for(Person person: personList) {
            Contributor contributor = new Contributor();
            contributor.setContributionType(ContributionType.EDITOR);
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
        if (iteratorDone) {
            return null;
        }
//        iteratorDone = true;
        BibEntry nextEntry = articles.get(currentIndex);
        currentIndex++;
        return nextEntry;
    }


    @Override
    public boolean hasNext() {
        if (currentIndex >= articles.size()) {
            iteratorDone = true;
        }
        return !iteratorDone;
    }

    @Override
    public void close() {
        //        if (lineIterator != null) {
        //            try {
        //                lineIterator.close();
        //            } catch (IOException e) {
        //                logger.error("Couldn't close line iterator.", e);
        //            }
        //        }
    }

}
