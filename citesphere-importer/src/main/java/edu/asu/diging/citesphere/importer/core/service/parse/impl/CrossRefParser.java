package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import edu.asu.diging.citesphere.importer.core.model.impl.Affiliation;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributionType;
import edu.asu.diging.citesphere.importer.core.model.impl.Contributor;
import edu.asu.diging.citesphere.importer.core.model.impl.ContributorId;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.model.impl.ReviewInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.crossref.ICrossRefParser;
import edu.asu.diging.crossref.model.Institution;
import edu.asu.diging.crossref.model.IssnType;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.model.Person;

@Component
public class CrossRefParser implements ICrossRefParser {
    
    /**
     * Parses journal metadata from the given {@link Item} object and returns a {@link ContainerMeta} object
     * containing the extracted journal metadata.
     *
     * @param item the {@link Item} object containing journal metadata to be parsed.
     * 
     * @return a {@link ContainerMeta} object containing the parsed journal metadata.
     */
    @Override
    public ContainerMeta parseJournalMeta(Item item) {
        ContainerMeta meta = new ContainerMeta();
        meta.setContainerTitle(item.getContainerTitle().get(0));
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
    
    /**
     * Parses article metadata from the given {@link Item} object and returns an {@link ArticleMeta} object
     * containing the extracted article metadata.
     *
     * @param item the {@link Item} object containing article metadata to be parsed. 
     * 
     * @return an {@link ArticleMeta} object containing the parsed article metadata.
     */
    @Override
    public ArticleMeta parseArticleMeta(Item item) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleTitle(item.getTitle().get(0));
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
                references.add(mapSingleReference(itemRef));
            }
        }
        return references;
    }

    private Reference mapSingleReference(edu.asu.diging.crossref.model.Reference itemRef) {
        Reference ref = new Reference();
        ref.setAuthorString(itemRef.getAuthor());
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

        return ref;
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

}
