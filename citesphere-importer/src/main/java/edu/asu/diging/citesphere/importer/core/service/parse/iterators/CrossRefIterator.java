package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticlePublicationDate;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.CrossRefPublication;
import edu.asu.diging.citesphere.importer.core.model.impl.Issn;
import edu.asu.diging.citesphere.importer.core.model.impl.Reference;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.IssnType;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

public class CrossRefIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JobInfo info;
    private BibEntry article;

    private boolean iteratorDone = false;
    private Map<String, String> typeMap;
    
    private CrossrefWorksService crossrefService;
    


    public CrossRefIterator(JobInfo info) {
        this.info = info;
        System.out.println(info.toString());
        init();
    }

    private void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());
        typeMap = new HashMap<String, String>();
        typeMap.put("journal-article", CrossRefPublication.ARTICLE);
        typeMap.put("book", CrossRefPublication.BOOK);
        parseCrossRef();

    }
    
    private void parseCrossRef() {
        
        List<Item> items = new ArrayList<>();
        for (String doi : info.getDois()) {
            try {
                Item item = crossrefService.get(doi);
                
                BibEntry article = new CrossRefPublication();
                article.setArticleType(typeMap.get(item.getType())); 
                article.setJournalMeta(parseJournalMeta(item));
                article.setArticleMeta(parseArticleMeta(item));
//                
//                ItemType type = typeMap.get(entry.getArticleType());
//                System.out.println("type ================================= " + type.toString() + " " + type.getZoteroKey());
//                JsonNode template = zoteroConnector.getTemplate(type);
//                ObjectNode crossRefNode = generationService.generateJson(template, entry);
                
                items.add(item);
                
//                root.add(crossRefNode);
//                entryCounter++;
                
                
                
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
        meta.setJournalIds(null);
        meta.setContainerTitle(String.join(", ", item.getContainerTitle()));
        meta.setJournalAbbreviations(null);
        meta.setPublisherName(item.getPublisher());
        meta.setPublisherLocation(item.getPublisherLocation());
        meta.setPublisherAddress(null);
        meta.setSeriesTitle(null);
        meta.setSeriesSubTitle(null);
        List<Issn> issnList = new ArrayList<Issn>();
        for(IssnType issnType : item.getIssnType()) {
            Issn issn = new Issn();
            issn.setIssn(issnType.getValue());
            issn.setPubType(issnType.getType());
            issnList.add(issn);
        }
        meta.setIssns(issnList);
        meta.setContributors(null);
        return meta;
    }
    
    private ArticleMeta parseArticleMeta(Item item) {
        ArticleMeta meta = new ArticleMeta();
        meta.setArticleIds(null);
        meta.setArticleTitle(String.join(", ", item.getTitle()));
        meta.setCategories(null);
        meta.setContributors(null);
        meta.setAuthorNotesCorrespondence(null);
        ArticlePublicationDate publicationDate = new ArticlePublicationDate();
        List<Integer> dateParts = item.getPublished().getIndexedDateParts();
        publicationDate.setPublicationDate(dateParts.get(2).toString());
        publicationDate.setPublicationMonth(dateParts.get(1).toString());
        publicationDate.setPublicationYear(dateParts.get(0).toString());
        meta.setPublicationDate(publicationDate);
        meta.setVolume(item.getVolume());
        meta.setIssue(item.getIssue());
        meta.setIssueId(null);
        meta.setSpecialIssue(null);
        meta.setPartNumber(item.getPartNumber());
        meta.setSupplement(null);
        meta.setFirstPage(item.getPage());
        meta.setLastPage(null);
        meta.setPageCount(null);
        meta.setChapterCount(null);
        meta.setCopyrightStatement(null);
        meta.setCopyrightYear(null);
        meta.setCopyrightHolder(null);
        meta.setSelfUri(item.getUrl());
        meta.setArticleAbstract(item.getAbstractText());
        meta.setLanguage(item.getLanguage());
        meta.setReviewInfo(null);       // check if data can be added
        meta.setDocumentType(null);        // might be type of article
        meta.setConferenceTitle(null);
        meta.setConferenceDate(null);
        meta.setConferenceLocation(null);
        meta.setConferenceSponsor(null);
        meta.setConferenceHost(null);
        meta.setKeywords(null);
        meta.setReprintAddress(null);
        meta.setAdditionalData(null);
        meta.setUnassignedIds(null);
        meta.setFundingInfo(null);
        meta.setFundingText(null);
        List<Reference> references = new ArrayList<>();
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
            ref.setEndPage(null);
            ref.setVolume(itemRef.getVolume());
            ref.setReferenceId(itemRef.getKey());
            ref.setReferenceLabel(null);
            ref.setPublicationType(null);
            ref.setCitationId(null);
            ref.setReferenceString(itemRef.getUnstructured());
            ref.setReferenceStringRaw(itemRef.getUnstructured());
            references.add(ref);
        }
        meta.setReferences(references);
        meta.setReferenceCount(item.getReferenceCount().toString());
        meta.setRetrievalDate(null);
        
        return meta;
    }

    @Override
    public BibEntry next() {
        if (iteratorDone) {
            return null;
        }
        iteratorDone = true;
        return article;
    }


    @Override
    public boolean hasNext() {
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
