package edu.asu.diging.citesphere.importer.core.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

@Service
public class CrossrefReferenceImportProcessor extends AbstractImportProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private CrossrefWorksService crossrefService;
    
    private Map<String, ItemType> itemTypeMapping = new HashMap<>();
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private IJsonGenerationService generationService;
    
    @Autowired
    private IHandlerRegistry handlerRegistry;
    
    @PostConstruct
    public void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());
        itemTypeMapping.put(CrossRefPublication.ARTICLE, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(CrossRefPublication.BOOK, ItemType.BOOK);
        itemTypeMapping.put(CrossRefPublication.BOOK_CHAPTER, ItemType.BOOK_SECTION);
//        itemTypeMapping.put(CrossRefPublication.LETTER, ItemType.LETTER);
//        itemTypeMapping.put(CrossRefPublication.NEWS_ITEM, ItemType.NEWSPAPER_ARTICLE);
//        itemTypeMapping.put(CrossRefPublication.PROCEEDINGS_PAPER, ItemType.CONFERENCE_PAPER);
//        itemTypeMapping.put(CrossRefPublication.DOCUMENT, ItemType.DOCUMENT);
    }
    
    public void startImport(KafkaJobMessage message, JobInfo info) {
        // message = jobToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKT0IxOTciLCJleHAiOjE2NzgzNDU3NDF9.5Xqh_AoMHcdlatULkCLFtny9pOF_uJ-SRARw0gCybY3h3qHL2mkIIQlk-qTA0Pn0VlhOLuW4FwACHmIdwZVmoA
        // info = dois: [10.2307/j.ctvcm4h07.67, 10.1515/9780691242507]
//        null
//        zotero: byRZjIk2y4e3kay1cnwy3KpB
//        zoteroId: 9154965
        logger.info("Starting import for " + info.getDois());
        
        List<Item> items = new ArrayList<>();
        
        //
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;
       // 
        sendMessage(null, message.getId(), Status.PROCESSING, ResponseCode.P00);
        BibEntryIterator bibIterator = null;
        try {
            //TODO: Change the handleFile method. returns null currrently for crossref. 
            bibIterator = handlerRegistry.handleFile(info, null);
        } catch (IteratorCreationException e1) {
            logger.error("Could not create iterator.", e1);
        }
        
        if (bibIterator == null) {
            sendMessage(null, message.getId(), Status.FAILED, ResponseCode.X30);
            return;
        }
        
        for (String doi : info.getDois()) {
            try {
                Item item = crossrefService.get(doi);
                
                if (item.getType() == null) {
                    // something is wrong with this entry, let's ignore it
                    continue;
                }

                BibEntry entry = new CrossRefPublication();
                entry.setArticleType(item.getType()); 
                
                ItemType type = itemTypeMapping.get(item.getType());
                JsonNode template = zoteroConnector.getTemplate(type);
                ObjectNode crossRefNode = generationService.generateJson(template, entry);
                
                items.add(item);
                
                root.add(crossRefNode);
                entryCounter++;
                
                
                
            } catch (RequestFailedException | IOException e) {
                logger.error("Couuld not retrieve work for doi: "+ doi, e);
                // for now we just log the exceptions
                // we might want to devise a way to decide if the 
                // service might be down and we should stop sending requests.
            }
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
        
        //
        
//        items.forEach((item) -> {
//            if (item.getDoi() == null) {
//                // something is wrong with this entry, let's ignore it
//                continue;
//            }
//            ItemType type = itemTypeMapping.get(item.getDoi());
//            JsonNode template = zoteroConnector.getTemplate(type);
//            ObjectNode bibNode = generationService.generateJson(template, item);
//
//            root.add(item);
//            entryCounter++;
//
//            // we can submit max 50 entries to Zotoro
//            if (entryCounter >= 50) {
//                submitEntries(root, info);
//                entryCounter = 0;
//                root = mapper.createArrayNode();
//            }
//
//        });
//        
//        ItemCreationResponse response = null;
//        if (entryCounter > 0) {
//            response = submitEntries(root, info);
//        }
//
//        response = response != null ? response : new ItemCreationResponse();
//        sendMessage(response, message.getId(), Status.DONE, ResponseCode.S00);
                
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
