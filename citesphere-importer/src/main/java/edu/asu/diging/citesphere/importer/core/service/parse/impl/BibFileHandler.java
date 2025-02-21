package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.HandlerTestException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.IGilesConnector;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.BibFileIterator;


@Service
public class BibFileHandler implements FileHandler {
    
    @Autowired
    private IGilesConnector gilesConnector;
    
    @Override
    public boolean canHandle(String path) throws HandlerTestException {
        if (path.toLowerCase().endsWith(".bib")) {
            return true;
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info)
            throws IteratorCreationException {
        return new BibFileIterator(path, info, gilesConnector);
    }

}
