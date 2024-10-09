package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private String filePath;
    private Iterator<String> lineIterator;
    private String currentLine = null;
    
    public BibFileIterator(String filePath) {
        System.out.println("------------------ inside bib file iterator");
        this.filePath = filePath;
        init();
    }
    
    private void init() {
        //        try {
        //            System.out.println(filePath + " filepath ======================");
        //            lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        //            if (lineIterator.hasNext()) {
        //                // we're at the beginning, so we'll signal that with and empty string
        ////                currentLine = "";
        //                String line = lineIterator.nextLine();
        //                System.out.println(line + "-------------------------------");
        //            }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            lineIterator = reader.lines().iterator();

            //        while (iterator.hasNext()) {
            //            String line = iterator.next();
            //            // Process the line
            //            System.out.println(line);
            //        }
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }
    }
    
    @Override
    public BibEntry next() {
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            System.out.println(line + " 2 -------------------------------");
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return lineIterator.hasNext();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

}
