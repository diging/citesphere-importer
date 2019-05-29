package edu.asu.diging.citesphere.importer.core.zotero.impl;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;

@Service
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_base_url}")
    private String zoteroBaseUrl;
    
    @Value("${_zotero_template_api_endpoint}")
    private String templateApiEndpoint;
    
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        if (!zoteroBaseUrl.endsWith("/")) {
            zoteroBaseUrl += "/";
        }
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(zoteroBaseUrl));
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.importer.core.zotero.impl.IZoteroConnector#getTemplate(edu.asu.diging.citesphere.importer.core.model.ItemType)
     */
    @Override
    public JsonNode getTemplate(ItemType itemType) {
        ResponseEntity<String> templateResponse = restTemplate.getForEntity(templateApiEndpoint + itemType.getZoteroKey(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(templateResponse.getBody());
        } catch (IOException e) {
            logger.error("Could not unmarshal response.", e);
        }
        return null;
    }
}
