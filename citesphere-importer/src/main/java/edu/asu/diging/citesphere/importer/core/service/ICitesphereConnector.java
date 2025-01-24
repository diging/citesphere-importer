package edu.asu.diging.citesphere.importer.core.service;

import org.springframework.social.zotero.api.Item;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;

public interface ICitesphereConnector {

    JobInfo getJobInfo(String apiToken) throws CitesphereCommunicationException;

    String getUploadeFile(String apiToken) throws CitesphereCommunicationException;
    
    Item getItem(String apiToken, String groupId, String itemKey) throws CitesphereCommunicationException;

    String uploadFile(String apiToken, String groupId, String itemKey, MultipartFile[] files) throws CitesphereCommunicationException;
}