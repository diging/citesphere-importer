package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Article;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.WoSFieldTags;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.WoSPublicationTypes;

public class WoSTaggedFieldsIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IArticleWoSTagParser tagParserRegistry;
    private String filePath;
    private LineIterator lineIterator;
    private String currentLine = null;

    public WoSTaggedFieldsIterator(String filePath, IArticleWoSTagParser parserRegistry) {
        this.filePath = filePath;
        this.tagParserRegistry = parserRegistry;
        init();
    }

    private void init() {
        try {
            lineIterator = FileUtils.lineIterator(new File(filePath));
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }

    }

    @Override
    public BibEntry next() {
        ArticleMeta articleMeta = new ArticleMeta();
        ContainerMeta containerMeta = new ContainerMeta();

        BibEntry entry = null;
        String previousField = null;
        String previousValue = null;
        
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            // this means we are at the end of an entry
            if (line.trim().isEmpty()) {
                break;
            }

            // not a valid line or not filled field
            if (line.length() < 4) {
                continue;
            }
            String field = line.substring(0, 2);
            String value = line.substring(3);

            if (field == WoSFieldTags.PT) {
                switch (value) {
                case WoSPublicationTypes.JOURNAL:
                    entry = new Article();
                    ((Article) entry).setArticleMeta(articleMeta);
                    ((Article) entry).setJournalMeta(containerMeta);
                    ((Article) entry).setArticleType(value);
                    break;
                }
            } else {
                if (field.trim().isEmpty()) {
                    field = previousField;
                }
                tagParserRegistry.parseMetaTag(field, value, previousField, previousValue, containerMeta, articleMeta);
            }
            
            previousField = field;
            previousValue = value;
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
