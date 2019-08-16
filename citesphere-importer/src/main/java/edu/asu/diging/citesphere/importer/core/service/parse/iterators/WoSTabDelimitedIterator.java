package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.ArticleMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.ContainerMeta;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;

public class WoSTabDelimitedIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IArticleWoSTagParser tagParserRegistry;
    private String filePath;

    private CSVReader reader;
    private String[] headers;

    public WoSTabDelimitedIterator(String filePath, IArticleWoSTagParser parserRegistry) {
        this.filePath = filePath;
        this.tagParserRegistry = parserRegistry;
        init();
    }

    private void init() {
        final CSVParser parser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
        try {
            reader = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(parser).build();
            headers = reader.readNext();
        } catch (IOException e) {
            logger.error("Could not open CSVReader.", e);
        }
    }

    @Override
    public BibEntry next() {
        ArticleMeta articleMeta = new ArticleMeta();
        ContainerMeta containerMeta = new ContainerMeta();

        BibEntry entry = new Publication();
        entry.setArticleMeta(articleMeta);
        entry.setJournalMeta(containerMeta);

        String[] line;
        try {
            line = reader.readNext();
        } catch (IOException e) {
            logger.debug("Could not read next line.", e);
            return null;
        }

        if (line != null) {
            for (int i = 0; i < line.length; i++) {
                // apparently some lines have additional tabs at the end
                if (i >= headers.length) {
                    break;
                }
                String field = headers[i];
                String value = line[i];
                tagParserRegistry.parseMetaTag(field, value, null, -1, entry, true);
            }
        }

        return entry;
    }

    @Override
    public boolean hasNext() {
        try {
            return reader.peek() != null;
        } catch (IOException e) {
            logger.error("Could not check for next line.", e);
        }
        return false;
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error("Couldn't close csv reader.", e);
            }
        }
    }

}
