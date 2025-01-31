package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.user.IUser;

public interface IGilesConnector {

    IGilesUpload uploadFile(IUser user, String token, String filename, byte[] fileBytes);
}
