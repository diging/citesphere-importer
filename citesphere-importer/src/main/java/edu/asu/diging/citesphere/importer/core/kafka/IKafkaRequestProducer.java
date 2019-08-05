package edu.asu.diging.citesphere.importer.core.kafka;

import edu.asu.diging.citesphere.importer.core.exception.MessageCreationException;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;

public interface IKafkaRequestProducer {

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.kafka.impl.IOCRRequestProducer#sendOCRRequest(java.lang
     * .String)
     */
    void sendRequest(KafkaImportReturnMessage msg, String topic) throws MessageCreationException;

}