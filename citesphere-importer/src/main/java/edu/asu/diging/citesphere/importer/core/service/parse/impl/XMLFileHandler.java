package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser;
import edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.JStorArticleXmlIterator;

@Service
public class XMLFileHandler implements FileHandler {
    
    @Autowired
    private IArticleTagParser parserRegistry;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.impl.FileHandler#canHandle(java.lang.String)
     */
    @Override
    public boolean canHandle(String path) {
        File file = new File(path);
        // exclude hidden files
        if (path.toLowerCase().endsWith(".xml") && !file.getName().startsWith(".")) {
            return true;
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info) throws IteratorCreationException {
        // needs to distiniguish for different XMLs
        return new JStorArticleXmlIterator(path, parserRegistry);
    }
    
}
