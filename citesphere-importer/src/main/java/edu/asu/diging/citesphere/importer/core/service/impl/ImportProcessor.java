package edu.asu.diging.citesphere.importer.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage;
import edu.asu.diging.citesphere.importer.core.service.ICitesphereConnector;
import edu.asu.diging.citesphere.importer.core.service.IImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;

@Service
public class ImportProcessor implements IImportProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitesphereConnector connector;
    
    @Autowired
    private IHandlerRegistry handlerRegistry;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.impl.IImportProcessor#process(edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage)
     */
    @Override
    public void process(KafkaJobMessage message) {
        JobInfo info = getJobInfo(message);
        if (info == null) {
            return;
        }
        
        String filePath = downloadFile(message);
        if (filePath == null) {
            return;
        }
        
        handlerRegistry.handleFile(info, filePath);
    }
    
    private JobInfo getJobInfo(KafkaJobMessage message) {
        JobInfo info = null;
        try {
            info = connector.getJobInfo(message.getId());
        } catch (CitesphereCommunicationException e) {
            // FIXME this needs to be handled better
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return info;
    }
    
    private String downloadFile(KafkaJobMessage message) {
        String file = null;
        try {
            file = connector.getUploadeFile(message.getId());
        } catch (CitesphereCommunicationException e) {
            // FIXME this needs to be handled better
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return file;
    }
}
