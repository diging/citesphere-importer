package edu.asu.diging.citesphere.importer.core.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@PropertySource("classpath:/config.properties")
public class CitesphereConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${_citesphere_base_uri")
    private String citesphereBaseUri;
    
    @Value("${_citesphere_client_key}")
    private String citesphereClientKey;
    
    @Value("${_citesphere_client_secret}")
    private String citesphereClientSecret;
    
    private RestTemplate restTemplate;
    
    private String accessToken;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        if (!citesphereBaseUri.endsWith("/")) {
            citesphereBaseUri += "/";
        }
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(citesphereBaseUri));
    }

    public ZoteroUserInfo getZoteroInfo(String apiToken) {
        logger.debug(getAccessToken());
        return null;
    }
    
    protected String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(citesphereClientKey, citesphereClientSecret);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        // not working? FIXME
        ResponseEntity<String> response = restTemplate.exchange("api/v1/oauth/token?grant_type=client_credentials", HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}
