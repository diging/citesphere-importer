package edu.asu.diging.citesphere.importer.core.service;

import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage;

public interface IImportProcessor {

    void process(KafkaJobMessage message);

}