package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import edu.asu.diging.citesphere.importer.core.exception.HandlerTestException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.WoSTabDelimitedIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.wos.tagged.IArticleWoSTagParser;

@Service
public class WoSTabDelimitedHandler implements FileHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IArticleWoSTagParser parserRegistry;

    @Override
    public boolean canHandle(String path) throws HandlerTestException {
        File file = new File(path);

        if ((path.toLowerCase().endsWith(".txt") || path.toLowerCase().endsWith(".csv"))
                && !file.getName().startsWith(".")) {
            final CSVParser parser = new CSVParserBuilder().withSeparator('\t').withIgnoreQuotations(true).build();
            try (CSVReader reader = new CSVReaderBuilder(new FileReader(path)).withCSVParser(parser)
                    .build()) {
                String[] firstLine = reader.readNext();
                for (String heading : firstLine) {
                    if (heading.trim().length() != 2) {
                        return false;
                    }
                }
                return true;
            } catch (IOException e) {
                logger.debug("Not a CSV file.", e);
                return false;
            }
        }

        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info)
            throws IteratorCreationException {
        return new WoSTabDelimitedIterator(path, parserRegistry);
    }

}
