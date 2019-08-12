package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.WoSDocumentTypes;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.WoSFieldTags;

public class WoSTaggedFieldsIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IArticleWoSTagParser tagParserRegistry;
    private String filePath;
    private LineIterator lineIterator;
    private String currentLine = null;
    
    private Map<String, String> publicationsType;

    public WoSTaggedFieldsIterator(String filePath, IArticleWoSTagParser parserRegistry) {
        this.filePath = filePath;
        this.tagParserRegistry = parserRegistry;
        init();
    }

    private void init() {
        
        publicationsType = new HashMap<String, String>();
        publicationsType.put(WoSDocumentTypes.ARTICLE, Publication.ARTICLE);
        publicationsType.put(WoSDocumentTypes.BOOK, Publication.BOOK);
        
        try {
            lineIterator = FileUtils.lineIterator(new File(filePath));
            if (lineIterator.hasNext()) {
                // we're at the beginning, so we'll signal that with and empty string
                currentLine = "";
            }
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }

    }

    @Override
    public BibEntry next() {
        ArticleMeta articleMeta = new ArticleMeta();
        ContainerMeta containerMeta = new ContainerMeta();

        BibEntry entry = new Publication();
        entry.setArticleMeta(articleMeta);
        entry.setJournalMeta(containerMeta);
        
        String previousField = null;
        int fieldIdx = 0;
        
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            // this means we are at the end of an entry
            if (line.trim().isEmpty()) {
                break;
            }

            // not a valid line or not filled field
            if (line.length() < 2) {
                continue;
            }
            String field = line.substring(0, 2);
            String value = "";
            
            if (line.length() > 2) {
                value = line.substring(3);
            }

            if (field.equals(WoSFieldTags.DT)) {
                // we only take the first vale (might be several separated by ';')
                String[] types = value.split(";");
                if (types.length > 0) {
                    entry.setArticleType(publicationsType.get(types[0]));
                }
            } else {
                if (field.trim().isEmpty()) {
                    field = previousField;
                    fieldIdx++;
                } else {
                    fieldIdx = 0;
                }
                tagParserRegistry.parseMetaTag(field, value, previousField, fieldIdx, containerMeta, articleMeta);
            }
            
            previousField = field;
        }
        
        if (entry.getArticleType() == null) {
            entry.setArticleType(Publication.ARTICLE);
        }

        // in case there are several empty lines between entries
        // let's skip them
        advanceToNext();

        return entry;
    }

    private void advanceToNext() {
        if (lineIterator.hasNext()) {
            currentLine = lineIterator.next();
            if (currentLine.trim().isEmpty()) {
                advanceToNext();
            }
        } else {
            currentLine = null;
        }
    }

    @Override
    public boolean hasNext() {
        return currentLine != null;
    }

    @Override
    public void close() {
        if (lineIterator != null) {
            try {
                lineIterator.close();
            } catch (IOException e) {
                logger.error("Couldn't close line iterator.", e);
            }
        }
    }

}
