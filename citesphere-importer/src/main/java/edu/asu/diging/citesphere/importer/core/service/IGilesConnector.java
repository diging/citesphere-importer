package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;

public interface IGilesConnector {

    IGilesUpload uploadFile(String username, String token, String filename, byte[] fileBytes);
}
