package edu.asu.diging.citesphere.importer.core.zotero;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.ItemType;

public interface IZoteroConnector {

    JsonNode getTemplate(ItemType itemType);

}