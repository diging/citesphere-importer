package edu.asu.diging.citesphere.importer.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.service.AbstractImportProcessor;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;
import edu.asu.diging.crossref.exception.RequestFailedException;
import edu.asu.diging.crossref.model.Item;
import edu.asu.diging.crossref.service.CrossrefConfiguration;
import edu.asu.diging.crossref.service.CrossrefWorksService;
import edu.asu.diging.crossref.service.impl.CrossrefWorksServiceImpl;

@Service
public class CrossrefReferenceImportProcessor extends AbstractImportProcessor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private CrossrefWorksService crossrefService;
    
    @PostConstruct
    public void init() {
        crossrefService = new CrossrefWorksServiceImpl(CrossrefConfiguration.getDefaultConfig());    
    }
    
    public void startImport(KafkaJobMessage message, JobInfo info) {
        logger.info("Starting import for " + info.getDois());
        
        List<Item> items = new ArrayList<>();
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
                
    }
}
