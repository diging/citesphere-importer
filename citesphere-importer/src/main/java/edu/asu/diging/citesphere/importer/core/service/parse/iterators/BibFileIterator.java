package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private String filePath;
    private LineIterator lineIterator;
    private String currentLine = null;
    
    public BibFileIterator(String filePath) {
        System.out.println("------------------ inside bib file iterator");
        this.filePath = filePath;
        init();
    }
    
    private void init() {
        try {
            lineIterator = FileUtils.lineIterator(new File(filePath));
            if (lineIterator.hasNext()) {
                // we're at the beginning, so we'll signal that with and empty string
                currentLine = "";
                String line = lineIterator.nextLine();
                System.out.println(line + "-------------------------------");
            }
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }
    }
    
    @Override
    public BibEntry next() {
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            System.out.println(line + " 2 -------------------------------");
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

}
