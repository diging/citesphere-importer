package edu.asu.diging.citesphere.importer.core.zotero;

import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.zotero.impl.ItemCreationResponse;

public interface IZoteroConnector {

    JsonNode getTemplate(ItemType itemType);

    ItemCreationResponse addEntry(JobInfo info, String json) throws URISyntaxException;

}