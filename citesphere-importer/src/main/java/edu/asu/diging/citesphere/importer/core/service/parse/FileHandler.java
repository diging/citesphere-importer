package edu.asu.diging.citesphere.importer.core.service.parse;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;

public interface FileHandler {

    boolean canHandle(String path);

    BibEntryIterator getIterator(String path, IHandlerRegistry callback, JobInfo info) throws IteratorCreationException;

}