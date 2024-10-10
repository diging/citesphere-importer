package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
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
        try {
            System.out.println(filePath + " filepath ======================");
            lineIterator = FileUtils.lineIterator(new File(filePath), "UTF-8");
        } catch (IOException e) {
            logger.error("Could not create line iterator.", e);
        }


    }

    @Override
    public BibEntry next() {
        System.out.println(" inside next -------------------------------");
        BibEntry entry = new Publication();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if(line.contains("@")) {
                entry.setArticleType(line.substring(line.indexOf('@')+1, line.indexOf('{')));
                System.out.println("type =========================== " +entry.getArticleType());
            }
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
