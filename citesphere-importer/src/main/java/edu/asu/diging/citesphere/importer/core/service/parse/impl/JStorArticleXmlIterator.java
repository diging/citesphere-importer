package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import edu.asu.diging.citesphere.importer.core.model.impl.BibEntry;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;

public class JStorArticleXmlIterator implements BibEntryIterator {

    private String filePath;
    
    public JStorArticleXmlIterator(String filePath) {
        this.filePath = filePath;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.impl.BibEntryIterator#next()
     */
    @Override
    public BibEntry next() {
        // do something
        return null;
    }
}
