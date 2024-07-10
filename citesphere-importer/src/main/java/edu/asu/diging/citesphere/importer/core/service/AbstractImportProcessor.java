package edu.asu.diging.citesphere.importer.core.service;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.exception.MessageCreationException;
import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaRequestProducer;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.messages.KafkaTopics;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;
import edu.asu.diging.citesphere.messages.model.KafkaImportReturnMessage;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

public abstract class AbstractImportProcessor implements IImportProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private KafkaRequestProducer requestProducer;

    @Autowired
    private ICitesphereConnector connector;
    
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
        itemTypeMapping.put(Publication.MONOGRAPH, ItemType.BOOK);
        itemTypeMapping.put(Publication.JOURNAL_ISSUE, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.REFERNCE_ENTRY, ItemType.DICTIONARY_ENTRY);
        itemTypeMapping.put(Publication.POSTED_CONTENT, ItemType.WEBPAGE);
        itemTypeMapping.put(Publication.COMPONENT, ItemType.ATTACHMENT);
        itemTypeMapping.put(Publication.EDITED_BOOK, ItemType.BOOK);
        itemTypeMapping.put(Publication.PROCEEDINGS_ARTICLE, ItemType.CONFERENCE_PAPER);
        itemTypeMapping.put(Publication.DISSERTATION, ItemType.THESIS);
        itemTypeMapping.put(Publication.BOOK_SECTION, ItemType.BOOK_SECTION);
        itemTypeMapping.put(Publication.REPORT_COMPONENT, ItemType.REPORT);
        itemTypeMapping.put(Publication.REPORT, ItemType.REPORT);
        itemTypeMapping.put(Publication.PEER_REVIEW, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.BOOK_TRACK, ItemType.BOOK);
        itemTypeMapping.put(Publication.BOOK_PART, ItemType.BOOK_SECTION);
        itemTypeMapping.put(Publication.OTHER, ItemType.DOCUMENT);
        itemTypeMapping.put(Publication.JORUNAL_VOLUME, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.BOOK_SET, ItemType.BOOK);
        itemTypeMapping.put(Publication.JOURNAL, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.PROCEEDINGS_SERIES, ItemType.CONFERENCE_PAPER);
        itemTypeMapping.put(Publication.REPORT_SERIES, ItemType.REPORT);
        itemTypeMapping.put(Publication.PROCEEDINGS, ItemType.CONFERENCE_PAPER);
        itemTypeMapping.put(Publication.DATABASE, ItemType.DATABASE);
        itemTypeMapping.put(Publication.STANDARD, ItemType.STATUTE);
        itemTypeMapping.put(Publication.REFERENCE_BOOK, ItemType.DICTIONARY_ENTRY);
        itemTypeMapping.put(Publication.GRANT, ItemType.DOCUMENT);
        itemTypeMapping.put(Publication.DATASET, ItemType.DATABASE);
        itemTypeMapping.put(Publication.BOOK_SERIES, ItemType.BOOK);
    }
   
    @Override
    public void process(KafkaJobMessage message) {
        JobInfo info = getJobInfo(message);
        if (info == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X10);
            return;
        }
        startImport(message, info);
    }
   
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
    
    protected ICitesphereConnector getCitesphereConnector() {
        return connector;
    }
    
    private ItemCreationResponse submitEntries(ArrayNode entries, JobInfo info) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String msg = mapper.writeValueAsString(entries);
            logger.info("Submitting " + msg);
            ItemCreationResponse response = zoteroConnector.addEntries(info, entries);
            if (response != null) {
                logger.info(response.getSuccessful() + "");
                logger.error(response.getFailed() + "");
            } else {
                logger.error("Item creation failed.");
            }
            return response;
        } catch (URISyntaxException e) {
            logger.error("Could not store new entry.", e);
        } catch (JsonProcessingException e) {
            logger.error("Could not write JSON.");
        }
        return null;
    }
    
    private void startImport(KafkaJobMessage message, JobInfo info) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;
        
        sendMessage(null, message.getId(), Status.PROCESSING, ResponseCode.P00);
        
        BibEntryIterator bibIterator = getbibIterator(message, info);
        if (bibIterator == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X30);
            return;
        }
        
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
    
    protected abstract BibEntryIterator getbibIterator(KafkaJobMessage message, JobInfo info);
}
