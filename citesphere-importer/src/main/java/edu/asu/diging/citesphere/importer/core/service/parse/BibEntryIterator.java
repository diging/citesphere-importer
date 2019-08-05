package edu.asu.diging.citesphere.importer.core.service.parse;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public interface BibEntryIterator {

    BibEntry next();
    
    boolean hasNext();

}