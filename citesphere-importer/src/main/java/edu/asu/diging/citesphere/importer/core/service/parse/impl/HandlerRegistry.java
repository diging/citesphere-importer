package edu.asu.diging.citesphere.importer.core.service.parse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.exception.IteratorCreationException;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.FileHandler;
import edu.asu.diging.citesphere.importer.core.service.parse.IHandlerRegistry;

@Service
public class HandlerRegistry implements IHandlerRegistry {
    
    @Autowired
    private ApplicationContext ctx;

    private List<FileHandler> handlers;
    
    @PostConstruct
    public void init() {
        handlers = new ArrayList<>();
        
        Map<String, FileHandler> allHandlers = ctx.getBeansOfType(FileHandler.class);
        allHandlers.values().forEach(h -> handlers.add(h));        
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.service.parse.impl.IHandlerRegistry#handleFile(edu.asu.diging.citesphere.importer.core.service.impl.JobInfo, java.lang.String)
     */
    @Override
    public BibEntryIterator handleFile(JobInfo info, String filePath) throws IteratorCreationException {
        for (FileHandler handler: handlers) {
            if (handler.canHandle(filePath)) {
                return handler.getIterator(filePath, this, info);
            }
        }
        return null;
    }
}
