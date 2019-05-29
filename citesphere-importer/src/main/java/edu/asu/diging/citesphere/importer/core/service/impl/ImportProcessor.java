package edu.asu.diging.citesphere.importer.core.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.exception.CitesphereCommunicationException;
import edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage;
import edu.asu.diging.citesphere.importer.core.model.BibEntry;
import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.model.impl.Article;
import edu.asu.diging.citesphere.importer.core.service.ICitesphereConnector;
import edu.asu.diging.citesphere.importer.core.service.IImportProcessor;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;
import edu.asu.diging.citesphere.importer.core.zotero.template.IJsonGenerationService;

@Service
public class ImportProcessor implements IImportProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICitesphereConnector connector;
    
    @Autowired
    private IHandlerRegistry handlerRegistry;
    
    @Autowired
    private IZoteroConnector zoteroConnector;
    
    @Autowired
    private IJsonGenerationService generationService;
    
    private Map<Class<? extends BibEntry>, ItemType> itemTypeMapping = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // this needs to be changed and improved, but for now it works
        itemTypeMapping.put(Article.class, ItemType.JOURNAL_ARTICLE);
    }
    

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.impl.IImportProcessor#process(edu.asu.diging.citesphere.importer.core.kafka.impl.KafkaJobMessage)
     */
    @Override
    public void process(KafkaJobMessage message) {
        JobInfo info = getJobInfo(message);
        if (info == null) {
            return;
        }
        
        String filePath = downloadFile(message);
        if (filePath == null) {
            return;
        }
        
        BibEntryIterator bibIterator = handlerRegistry.handleFile(info, filePath);
        while(bibIterator.hasNext()) {
            BibEntry entry = bibIterator.next();
            ItemType type = itemTypeMapping.get(entry.getClass());
            JsonNode template = zoteroConnector.getTemplate(type);
            String msg = generationService.generateJson(template, entry);
            logger.error(msg);
        }
    }
    
    private JobInfo getJobInfo(KafkaJobMessage message) {
        JobInfo info = null;
        try {
            info = connector.getJobInfo(message.getId());
        } catch (CitesphereCommunicationException e) {
            // FIXME this needs to be handled better
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return info;
    }
    
    private String downloadFile(KafkaJobMessage message) {
        String file = null;
        try {
            file = connector.getUploadeFile(message.getId());
        } catch (CitesphereCommunicationException e) {
            // FIXME this needs to be handled better
            logger.error("Could not get Zotero info.", e);
            return null;
        }
        return file;
    }
}
