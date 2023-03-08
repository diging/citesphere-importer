package edu.asu.diging.citesphere.importer.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.service.AbstractImportProcessor;
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
    
    @PostConstruct
    public void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());    
    }
    
    public void startImport(KafkaJobMessage message, JobInfo info) {
        logger.info("Starting import for " + info.getDois());
        
        List<Item> items = new ArrayList<>();
        
        //
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;
       // 
       
        for (String doi : info.getDois()) {
            try {
                items.add(crossrefService.get(doi));
            } catch (RequestFailedException | IOException e) {
                logger.error("Couuld not retrieve work for doi: "+ doi, e);
                // for now we just log the exceptions
                // we might want to devise a way to decide if the 
                // service might be down and we should stop sending requests.
            }
        }
        
        //
        
        items.forEach((item) -> {
            if (item.getDoi() == null) {
                // something is wrong with this entry, let's ignore it
                continue;
            }
            ItemType type = itemTypeMapping.get(item.getDoi());
            JsonNode template = zoteroConnector.getTemplate(type);
            ObjectNode bibNode = generationService.generateJson(template, entry);

            root.add(item);
            entryCounter++;

            // we can submit max 50 entries to Zotoro
            if (entryCounter >= 50) {
                submitEntries(root, info);
                entryCounter = 0;
                root = mapper.createArrayNode();
            }

        });
        
        ItemCreationResponse response = null;
        if (entryCounter > 0) {
            response = submitEntries(root, info);
        }

        response = response != null ? response : new ItemCreationResponse();
        sendMessage(response, message.getId(), Status.DONE, ResponseCode.S00);
                
    }
}
