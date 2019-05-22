package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.service.impl.ZoteroUserInfo;

public interface ICitesphereConnector {

    ZoteroUserInfo getZoteroInfo(String apiToken) throws CitesphereCommunicationException;

    String getUploadeFile(String apiToken) throws CitesphereCommunicationException;

}