package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Article;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;

public class JStorArticleXmlIterator implements BibEntryIterator {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IArticleTagParser tagParserRegistry;
    private String filePath;
    private Article article;
    
    private boolean iteratorDone = false;
    
    public JStorArticleXmlIterator(String filePath, IArticleTagParser registry) {
        this.filePath = filePath;
        this.tagParserRegistry = registry;
        init();
    }
    
    private void init() {
        parseDocument();        
    }
    
    private void parseDocument() {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        Document doc;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(filePath);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Could not parse XML.", e);
            return;
        } 
        
        article = new Article();
        article.setArticleType(doc.getDocumentElement().getAttribute("article-type"));
        article.setJournalMeta(parseJournalMeta(doc.getDocumentElement()));
        article.setArticleMeta(parseArticleMeta(doc.getDocumentElement()));
        
    }
    
    private JournalMeta parseJournalMeta(Element element) {
        NodeList journalMetaList = element.getElementsByTagName("journal-meta");
        if (journalMetaList.getLength() == 0) {
            return null;
        }
        
        JournalMeta meta = new JournalMeta();
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
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.impl.BibEntryIterator#next()
     */
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
}
