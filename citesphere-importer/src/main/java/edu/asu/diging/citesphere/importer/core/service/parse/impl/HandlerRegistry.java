package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;

@Service
public class HandlerRegistry {
    
    @Autowired
    private ApplicationContext ctx;

    private List<FileHandler> handlers;
    
    @PostConstruct
    public void init() {
        handlers = new ArrayList<>();
        
        Map<String, FileHandler> allHandlers = ctx.getBeansOfType(FileHandler.class);
        allHandlers.values().forEach(h -> handlers.add(h));        
    }
    
    public void handleFile(JobInfo info, String filePath) {
        for (FileHandler handler: handlers) {
            if (handler.canHandle(filePath)) {
                BibEntryIterator iterator = handler.getIterator(filePath);
                // do stuff
                break;
            }
        }
    }
}
