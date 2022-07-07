package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

public interface ImportProcessor {

    void process(KafkaJobMessage message);

}