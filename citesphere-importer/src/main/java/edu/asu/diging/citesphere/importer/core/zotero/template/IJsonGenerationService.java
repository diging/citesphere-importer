package edu.asu.diging.citesphere.importer.core.zotero.template;

import com.fasterxml.jackson.databind.JsonNode;

import edu.asu.diging.citesphere.importer.core.model.BibEntry;

public interface IJsonGenerationService {

    String generateJson(JsonNode template, BibEntry entry);

}