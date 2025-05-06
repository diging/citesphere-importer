package edu.asu.diging.citesphere.importer.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

/**
 * This class coordinates the import process. It connects with Citesphere and 
 * downloads the files to be imported. It then starts the transformation process from
 * import format to internal bibliographical format and then turns the internal 
 * bibliographical format to Json that can be submitted to Zotero.
 * @author jdamerow
 *
 */
@Service
public class FileImportProcessor extends AbstractImportProcessor {

    @Autowired
    private IHandlerRegistry handlerRegistry;

    private String downloadFile(KafkaJobMessage message) {
        String file = null;
        try {
            file = getCitesphereConnector().getUploadeFile(message.getId());
        } catch (CitesphereCommunicationException e) {
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return file;
    }

    @Override
    protected BibEntryIterator getBibEntryIterator(KafkaJobMessage message, JobInfo info) {
        String filePath = downloadFile(message);
        if (filePath == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X20);
            return null;
        }

        BibEntryIterator bibIterator = null;
        try {
            bibIterator = handlerRegistry.handleFile(info, filePath);
        } catch (IteratorCreationException e1) {
            logger.error("Could not create iterator.", e1);
        }
        
        return bibIterator;
    }
}
