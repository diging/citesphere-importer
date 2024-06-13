package edu.asu.diging.citesphere.importer.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.exception.MessageCreationException;
import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaRequestProducer;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

public abstract class AbstractImportProcessor implements ImportProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private KafkaRequestProducer requestProducer;

    @Autowired
    private ICitesphereConnector connector;
    
   
    @Override
    public void process(KafkaJobMessage message) {
        JobInfo info = getJobInfo(message);
        if (info == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X10);
            return;
        }
        startImport(message, info);
    }

    protected abstract void startImport(KafkaJobMessage message, JobInfo info);
    
    private JobInfo getJobInfo(KafkaJobMessage message) {
        JobInfo info = null;
        try {
            info = connector.getJobInfo(message.getId());
        } catch (CitesphereCommunicationException e) {
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return info;
    }
    
    protected void sendMessage(ItemCreationResponse message, String jobId, Status status, ResponseCode code) {
        KafkaImportReturnMessage returnMessage = new KafkaImportReturnMessage(message, jobId);
        returnMessage.setStatus(status);
        returnMessage.setCode(code);
        try {
            requestProducer.sendRequest(returnMessage, KafkaTopics.REFERENCES_IMPORT_DONE_TOPIC);
        } catch (MessageCreationException e) {
            logger.error("Exception sending message.", e);
        }
    }
    
    public ICitesphereConnector getCitesphereConnector() {
        return connector;
    }
}
