package edu.asu.diging.citesphere.importer.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.AbstractImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;
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

    @Autowired
    private IZoteroConnector zoteroConnector;

    @Autowired
    private IJsonGenerationService generationService;
    
    
    /**
     * Map that maps internal bibliographical formats (contants of {@link Publication} 
     * class) to Zotero item types ({@link ItemType} enum).
     */
    private Map<String, ItemType> itemTypeMapping = new HashMap<>();

    @PostConstruct
    public void init() {
        // this needs to be changed and improved, but for now it works
        itemTypeMapping.put(Publication.ARTICLE, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.BOOK, ItemType.BOOK);
        itemTypeMapping.put(Publication.BOOK_CHAPTER, ItemType.BOOK_SECTION);
        itemTypeMapping.put(Publication.LETTER, ItemType.LETTER);
        itemTypeMapping.put(Publication.NEWS_ITEM, ItemType.NEWSPAPER_ARTICLE);
        itemTypeMapping.put(Publication.PROCEEDINGS_PAPER, ItemType.CONFERENCE_PAPER);
        itemTypeMapping.put(Publication.DOCUMENT, ItemType.DOCUMENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.importer.core.service.impl.IImportProcessor#process
     * (edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage)
     */
    @Override
    public void startImport(KafkaJobMessage message, JobInfo info) {
        String filePath = downloadFile(message);
        if (filePath == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X20);
            return;
        }

        sendMessage(null, message.getId(), Status.PROCESSING, ResponseCode.P00);
        BibEntryIterator bibIterator = null;
        try {
            bibIterator = handlerRegistry.handleFile(info, filePath);
        } catch (IteratorCreationException e1) {
            logger.error("Could not create iterator.", e1);
        }
        
        if (bibIterator == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X30);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;
        while (bibIterator.hasNext()) {
            BibEntry entry = bibIterator.next();
            if (entry.getArticleType() == null) {
                // something is wrong with this entry, let's ignore it
                continue;
            }
            ItemType type = itemTypeMapping.get(entry.getArticleType());
            JsonNode template = zoteroConnector.getTemplate(type);
            ObjectNode bibNode = generationService.generateJson(template, entry);

            root.add(bibNode);
            entryCounter++;

            // we can submit max 50 entries to Zotoro
            if (entryCounter >= 50) {
                submitEntries(root, info);
                entryCounter = 0;
                root = mapper.createArrayNode();
            }

        }
        
        bibIterator.close();
        
        ItemCreationResponse response = null;
        if (entryCounter > 0) {
            response = submitEntries(root, info);
        }

        response = response != null ? response : new ItemCreationResponse();
        sendMessage(response, message.getId(), Status.DONE, ResponseCode.S00);
    }

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

}
