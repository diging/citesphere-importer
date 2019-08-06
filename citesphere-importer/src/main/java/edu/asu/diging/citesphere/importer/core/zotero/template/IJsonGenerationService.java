package edu.asu.diging.citesphere.importer.core.zotero.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public interface IJsonGenerationService {

    ObjectNode generateJson(JsonNode template, BibEntry entry);

}