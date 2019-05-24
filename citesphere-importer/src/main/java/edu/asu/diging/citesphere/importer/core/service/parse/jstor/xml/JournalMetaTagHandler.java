package edu.asu.diging.citesphere.importer.core.service.parse.jstor.xml;

import org.w3c.dom.Node;

import edu.asu.diging.citesphere.importer.core.model.impl.JournalMeta;

public interface JournalMetaTagHandler {

    String handledTag();

    void handle(Node node, JournalMeta journalMeta);

}