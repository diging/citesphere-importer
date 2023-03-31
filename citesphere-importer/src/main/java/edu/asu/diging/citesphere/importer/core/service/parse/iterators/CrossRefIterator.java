package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;

public class CrossRefIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IArticleTagParser tagParserRegistry;
    private String filePath;
    private BibEntry article;
    
    private boolean iteratorDone = false;
    private Map<String, String> typeMap;

    public CrossRefIterator() {
//        this.filePath = filePath;
//        this.tagParserRegistry = parserRegistry;
        init();
    }

    private void init() {
        typeMap = new HashMap<String, String>();
        typeMap.put("research-article", Publication.ARTICLE);
        typeMap.put("book-review", Publication.REVIEW);
//        parseDocument();

    }

    @Override
    public BibEntry next() {
        if (iteratorDone) {
            return null;
        }
        iteratorDone = true;
        return article;
    }
    
    private ContainerMeta parseJournalMeta(Element element) {
        NodeList journalMetaList = element.getElementsByTagName("journal-meta");
        if (journalMetaList.getLength() == 0) {
            return null;
        }
        
        ContainerMeta meta = new ContainerMeta();
        // there should only be one
        Node journalMetaNode = journalMetaList.item(0);
        
        NodeList children = journalMetaNode.getChildNodes();
        for (int i = 0; i<children.getLength(); i++) {
            tagParserRegistry.parseJournalMetaTag(children.item(i), meta);
        }
        return meta;
    }
    
    private ArticleMeta parseArticleMeta(Element element) {
        NodeList articlelMetaList = element.getElementsByTagName("article-meta");
        if (articlelMetaList.getLength() == 0) {
            return null;
        }
        
        ArticleMeta meta = new ArticleMeta();
        Node articleMetaNode = articlelMetaList.item(0);
        NodeList children = articleMetaNode.getChildNodes();
        for (int i = 0; i<children.getLength(); i++) {
            tagParserRegistry.parseArticleMetaTag(children.item(i), meta);
        }
        return meta;
    }
    

//    private void advanceToNext() {
//        if (lineIterator.hasNext()) {
//            currentLine = lineIterator.next();
//            if (currentLine.trim().isEmpty()) {
//                advanceToNext();
//            }
//        } else {
//            currentLine = null;
//        }
//    }

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
