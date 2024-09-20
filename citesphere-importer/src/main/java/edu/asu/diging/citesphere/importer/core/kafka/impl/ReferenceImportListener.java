package edu.asu.diging.citesphere.importer.core.kafka.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.importer.core.service.IImportProcessor;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

public class ReferenceImportListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("fileImportProcessor")
    private IImportProcessor fileProcessor;
    
    @Autowired
    @Qualifier("crossrefReferenceImportProcessor")
    private IImportProcessor crossrefProcessor;

    @KafkaListener(topics = KafkaTopics.REFERENCES_IMPORT_TOPIC)
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        KafkaJobMessage msg = null;
        try {
            msg = mapper.readValue(message, KafkaJobMessage.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall message.", e);
            // FIXME: handle this case
            return;
        }
        
        fileProcessor.process(msg);
    }
    
    @KafkaListener(topics = KafkaTopics.REFERENCES_IMPORT_CROSSREF_TOPIC)
    public void receiveCrossrefImportMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        KafkaJobMessage msg = null;
        try {
            msg = mapper.readValue(message, KafkaJobMessage.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall message.", e);
            return;
        }
        
        crossrefProcessor.process(msg);
    }
}
