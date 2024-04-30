package edu.asu.diging.citesphere.importer.core.service.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.CrossRefPublication;
import edu.asu.diging.citesphere.importer.core.service.AbstractImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

@Service
public class CrossrefReferenceImportProcessor extends AbstractImportProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
      
    private Map<String, ItemType> itemTypeMapping = new HashMap<>();
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private IJsonGenerationService generationService;
    
    @Autowired
    private IHandlerRegistry handlerRegistry;
    
    @PostConstruct
    public void init() {      
        itemTypeMapping.put(CrossRefPublication.ARTICLE, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(CrossRefPublication.BOOK, ItemType.BOOK);
        itemTypeMapping.put(CrossRefPublication.BOOK_CHAPTER, ItemType.BOOK_SECTION);
        itemTypeMapping.put(CrossRefPublication.MONOGRAPH, ItemType.MONOGRAPH);
        itemTypeMapping.put(CrossRefPublication.JOURNAL_ISSUE, ItemType.JOURNAL_ISSUE);
        itemTypeMapping.put(CrossRefPublication.REFERNCE_ENTRY, ItemType.REFERNCE_ENTRY);
        itemTypeMapping.put(CrossRefPublication.POSTED_CONTENT, ItemType.POSTED_CONTENT);
        itemTypeMapping.put(CrossRefPublication.COMPONENT, ItemType.COMPONENT);
        itemTypeMapping.put(CrossRefPublication.EDITED_BOOK, ItemType.BOOK);
        itemTypeMapping.put(CrossRefPublication.PROCEEDINGS_ARTICLE, ItemType.CONFERENCE_PAPER);
        itemTypeMapping.put(CrossRefPublication.DISSERTATION, ItemType.DISSERTATION);
        itemTypeMapping.put(CrossRefPublication.BOOK_SECTION, ItemType.BOOK_SECTION);
        itemTypeMapping.put(CrossRefPublication.REPORT_COMPONENT, ItemType.REPORT_COMPONENT);
        itemTypeMapping.put(CrossRefPublication.REPORT, ItemType.REPORT);
        itemTypeMapping.put(CrossRefPublication.PEER_REVIEW, ItemType.PEER_REVIEW);
        itemTypeMapping.put(CrossRefPublication.BOOK_TRACK, ItemType.BOOK_TRACK);
        itemTypeMapping.put(CrossRefPublication.BOOK_PART, ItemType.BOOK_PART);
        itemTypeMapping.put(CrossRefPublication.OTHER, ItemType.OTHER);
        itemTypeMapping.put(CrossRefPublication.JORUNAL_VOLUME, ItemType.JORUNAL_VOLUME);
        itemTypeMapping.put(CrossRefPublication.BOOK_SET, ItemType.BOOK_SET);
        itemTypeMapping.put(CrossRefPublication.JOURNAL, ItemType.JOURNAL);
        itemTypeMapping.put(CrossRefPublication.PROCEEDINGS_SERIES, ItemType.PROCEEDINGS_SERIES);
        itemTypeMapping.put(CrossRefPublication.REPORT_SERIES, ItemType.REPORT_SERIES);
        itemTypeMapping.put(CrossRefPublication.PROCEEDINGS, ItemType.PROCEEDINGS);
        itemTypeMapping.put(CrossRefPublication.DATABASE, ItemType.DATABASE);
        itemTypeMapping.put(CrossRefPublication.STANDARD, ItemType.STANDARD);
        itemTypeMapping.put(CrossRefPublication.REFERENCE_BOOK, ItemType.REFERENCE_BOOK);
        itemTypeMapping.put(CrossRefPublication.GRANT, ItemType.GRANT);
        itemTypeMapping.put(CrossRefPublication.DATASET, ItemType.DATASET);
        itemTypeMapping.put(CrossRefPublication.BOOK_SERIES, ItemType.BOOK_SERIES);
    }
    
    public void startImport(KafkaJobMessage message, JobInfo info) {
        // message = jobToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKT0IxOTciLCJleHAiOjE2NzgzNDU3NDF9.5Xqh_AoMHcdlatULkCLFtny9pOF_uJ-SRARw0gCybY3h3qHL2mkIIQlk-qTA0Pn0VlhOLuW4FwACHmIdwZVmoA
        // info = dois: [10.2307/j.ctvcm4h07.67, 10.1515/9780691242507]
//        null
//        zotero: byRZjIk2y4e3kay1cnwy3KpB
//        zoteroId: 9154965
        logger.info("Starting import for " + info.getDois());
        
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;

        sendMessage(null, message.getId(), Status.PROCESSING, ResponseCode.P00);
        BibEntryIterator bibIterator = null;
        try {
            bibIterator = handlerRegistry.handleFile(info, null);
        } catch (IteratorCreationException e1) {
            logger.error("Could not create iterator.", e1);
        }
        
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
            System.out.println("======================================" + entry.getArticleType());
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
        sendMessage(response, message.getId(), Status.DONE, ResponseCode.S00);  // giving error 500 as response code mentioned
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

}
