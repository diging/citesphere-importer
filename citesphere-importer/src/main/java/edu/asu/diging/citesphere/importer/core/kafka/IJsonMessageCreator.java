package edu.asu.diging.citesphere.importer.core.kafka;

import edu.asu.diging.citesphere.importer.core.exception.MessageCreationException;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;

public interface IJsonMessageCreator {

    /* (non-Javadoc)
     * @see edu.asu.giles.service.kafka.impl.IJsonMessageCreator#createMessage(edu.asu.giles.service.requests.IRequest)
     */
    String createMessage(KafkaImportReturnMessage msg) throws MessageCreationException;

}