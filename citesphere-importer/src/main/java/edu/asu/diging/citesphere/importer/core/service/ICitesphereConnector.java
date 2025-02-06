package edu.asu.diging.citesphere.importer.core.service;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.model.bib.ICitation;

public interface ICitesphereConnector {

    JobInfo getJobInfo(String apiToken) throws CitesphereCommunicationException;

    String getUploadeFile(String apiToken) throws CitesphereCommunicationException;
    
    ICitation getItem(String apiToken, String groupId, String itemKey) throws CitesphereCommunicationException;

    String uploadFile(String apiToken, String groupId, String itemKey, MultipartFile[] files) throws CitesphereCommunicationException;
}