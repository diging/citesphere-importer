package edu.asu.diging.citesphere.importer.core.zotero;

import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.messages.model.ItemCreationResponse;

public interface IZoteroConnector {

    JsonNode getTemplate(ItemType itemType);

    ItemCreationResponse addEntries(JobInfo info, ArrayNode entries) throws URISyntaxException;

}