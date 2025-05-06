package edu.asu.diging.citesphere.importer.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.importer.core.service.parse.BibEntryIterator;
import edu.asu.diging.citesphere.importer.core.service.parse.impl.CrossRefParser;
import edu.asu.diging.citesphere.importer.core.service.parse.iterators.CrossRefIterator;
import edu.asu.diging.citesphere.messages.model.KafkaJobMessage;

@Service
public class CrossrefReferenceImportProcessor extends AbstractImportProcessor {
         
    @Autowired
    private CrossRefParser crossRefParser;
    
    @Override
    protected BibEntryIterator getBibEntryIterator(KafkaJobMessage message, JobInfo info) {
        return new CrossRefIterator(info, crossRefParser);
    }
}
