package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;

@Service
public class XMLFileHandler implements FileHandler {

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.impl.FileHandler#canHandle(java.lang.String)
     */
    @Override
    public boolean canHandle(String path) {
        if (path.toLowerCase().endsWith(".xml")) {
            return true;
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path) {
        // needs to distiniguish for different XMLs
        return new JStorArticleXmlIterator(path);
    }
    
}
