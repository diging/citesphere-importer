package edu.asu.diging.citesphere.importer.core.zotero.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.asu.diging.citesphere.importer.core.model.ItemType;
import edu.asu.diging.citesphere.importer.core.service.impl.JobInfo;
import edu.asu.diging.citesphere.importer.core.zotero.IZoteroConnector;

@Service
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_base_url}")
    private String zoteroBaseUrl;
    
    @Value("${_zotero_template_api_endpoint}")
    private String templateApiEndpoint;
    
    @Value("${_zotero_create_item_api_endpoint}")
    private String createItemApiEndpoint;
    
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        if (!zoteroBaseUrl.endsWith("/")) {
            zoteroBaseUrl += "/";
        }
        ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus status = response.getStatusCode();
                if (HttpStatus.Series.valueOf(status) == HttpStatus.Series.SUCCESSFUL || status == HttpStatus.BAD_REQUEST) {
                    return false;
                }
                return true;
            }
            
        };
        restTemplate.setErrorHandler(errorHandler);
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
    
    @Override
    public ItemCreationResponse addEntries(JobInfo info, ArrayNode entries) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(info.getZotero());
        HttpEntity<ArrayNode> entity = new HttpEntity<ArrayNode>(entries, headers);
        String url = String.format(createItemApiEndpoint, info.getGroupId());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(response.getBody(), ItemCreationResponse.class);
            } catch (IOException e) {
               logger.error("Could not unmarshall response.", e);
               return null;
            }
        }
        logger.info("Creating item returned: " + response.getStatusCodeValue());
        return null;// response.getBody();
    }
}
