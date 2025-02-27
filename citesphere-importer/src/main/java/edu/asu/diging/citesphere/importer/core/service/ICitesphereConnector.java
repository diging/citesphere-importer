package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;

public interface ICitesphereConnector {

    JobInfo getJobInfo(String apiToken) throws CitesphereCommunicationException;

    String getUploadeFile(String apiToken) throws CitesphereCommunicationException;

}