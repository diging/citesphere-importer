package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.crossref.ICrossRefParser;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

public class CrossRefIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private JobInfo info;

    private Map<String, String> typeMap;

    private CrossrefWorksService crossrefService;

    private Iterator<String> doisIterator;
    
    private ICrossRefParser crossRefParser;

    public CrossRefIterator(JobInfo info, ICrossRefParser crossRefParser) {
        this.info = info;
        doisIterator = info.getDois().iterator();
        this.crossRefParser = crossRefParser;
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

    @Override
    public BibEntry next() {
        if (!doisIterator.hasNext()) {
            return null;
        }
        BibEntry nextEntry = new Publication();

        try {
            Item item = crossrefService.get(doisIterator.next());
            nextEntry.setArticleType(typeMap.get(item.getType())); 
            nextEntry.setJournalMeta(crossRefParser.parseJournalMeta(item));
            nextEntry.setArticleMeta(crossRefParser.parseArticleMeta(item));
        } catch (RequestFailedException | IOException e) {
            logger.error("Could not retrieve work for doi: "+ doisIterator.next(), e);
            // for now we just log the exceptions
            // we might want to devise a way to decide if the 
            // service might be down and we should stop sending requests.
            return null;
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
