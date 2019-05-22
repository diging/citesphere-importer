package edu.asu.diging.citesphere.importer.core.service.parse;

public interface FileHandler {

    boolean canHandle(String path);

    BibEntryIterator getIterator(String path);

}