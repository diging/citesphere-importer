package edu.asu.diging.citesphere.importer.core.service.giles.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.citesphere.importer.core.service.IGilesConnector;
import edu.asu.diging.citesphere.model.bib.IGilesUpload;
import edu.asu.diging.citesphere.model.bib.impl.GilesUpload;

@Service
@PropertySource("classpath:/config.properties")
public class GilesConnector implements IGilesConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;
       
    @Value("${_giles_baseurl}")
    private String gilesBaseurl;

    @Value("${_giles_upload_endpoint}")
    private String uploadEndpoint;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
    }
    
    @Override
    public IGilesUpload uploadFile(String username, String token, String filename, byte[] fileBytes) {
        
        if (restTemplate == null) {
            restTemplate = new RestTemplate();  // Ensure restTemplate is initialized
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", new MultipartFileResource(fileBytes, filename));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UploadResponse> response = null;
        try {
            response = restTemplate.exchange(gilesBaseurl + uploadEndpoint, HttpMethod.POST, requestEntity, UploadResponse.class);
        } catch (Exception ex) {
            logger.error("Upload request failed", ex);
            return null;
        }
        IGilesUpload upload = new GilesUpload();
        upload.setProgressId(response.getBody().getId());
        upload.setUploadingUser(username);
        return upload;
    }
    
    public class MultipartFileResource extends ByteArrayResource {

        private String filename;

        public MultipartFileResource(byte[] bytearray, String filename) {
            super(bytearray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }

}
