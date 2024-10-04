package edu.asu.diging.citesphere.importer.core.service.parse.iterators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.BibEntryParser;

public class BibFileIterator implements BibEntryIterator {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public BibFileIterator(String filePath, BibEntryParser parser) {
        //TODO: add logic
    }
    
    @Override
    public BibEntry next() {
        // TODO Auto-generated method stub
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
