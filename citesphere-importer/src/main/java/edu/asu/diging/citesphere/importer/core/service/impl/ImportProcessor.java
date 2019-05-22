package edu.asu.diging.citesphere.importer.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage;
import edu.asu.diging.citesphere.importer.core.service.IImportProcessor;

@Service
public class ImportProcessor implements IImportProcessor {
    
    @Autowired
    private CitesphereConnector connector;

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.impl.IImportProcessor#process(edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage)
     */
    @Override
    public void process(KafkaJobMessage message) {
        connector.getZoteroInfo(message.getId());
    }
}
