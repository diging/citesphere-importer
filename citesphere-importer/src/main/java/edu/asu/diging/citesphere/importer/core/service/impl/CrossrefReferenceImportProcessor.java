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

import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.Publication;
import edu.asu.diging.citesphere.importer.core.service.AbstractImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.CrossRefIterator;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.citesphere.messages.model.ResponseCode;
import edu.asu.diging.citesphere.messages.model.Status;

@Service
public class CrossrefReferenceImportProcessor extends AbstractImportProcessor {
         
    private Map<String, ItemType> itemTypeMapping = new HashMap<>();
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private IJsonGenerationService generationService;
    
    @PostConstruct
    public void init() {      
        itemTypeMapping.put(Publication.ARTICLE, ItemType.JOURNAL_ARTICLE);
        itemTypeMapping.put(Publication.BOOK, ItemType.BOOK);
        itemTypeMapping.put(Publication.BOOK_CHAPTER, ItemType.BOOK_SECTION);
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
    
    public void startImport(KafkaJobMessage message, JobInfo info) {
        logger.info("Starting import for " + info.getDois());
        
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = mapper.createArrayNode();
        int entryCounter = 0;

        sendMessage(null, message.getId(), Status.PROCESSING, ResponseCode.P00);
        BibEntryIterator bibIterator = null;
        bibIterator = new CrossRefIterator(info);
        
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
    


}
