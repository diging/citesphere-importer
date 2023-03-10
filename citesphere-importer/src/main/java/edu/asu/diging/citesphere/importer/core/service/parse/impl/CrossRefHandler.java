package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.HandlerTestException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.CrossRefIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml.IArticleTagParser;

@Service
public class CrossRefHandler implements FileHandler {

    @Autowired
    private IArticleTagParser parserRegistry;
    
    @Value("${_citesphere_download_path}")
    private String downloadPath;

    @Override
    public boolean canHandle(String path) throws HandlerTestException {
        if (path == null) {
                return true;
        }
        return false;
    }

    @Override
    public BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info)
            throws IteratorCreationException {
        return new CrossRefIterator(path, parserRegistry);
    }

}
